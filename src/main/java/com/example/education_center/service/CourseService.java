package com.example.education_center.service;

import com.example.education_center.dto.CourseDTO;
import com.example.education_center.dto.CourseScheduleDTO;
import com.example.education_center.dto.LearnerDTO;
import com.example.education_center.dto.PageDTO;
import com.example.education_center.dto.search.SearchCourseDTO;
import com.example.education_center.entity.Course;
import com.example.education_center.entity.Learner;
import com.example.education_center.exception.NotAvailableException;
import com.example.education_center.exception.NotFoundException;
import com.example.education_center.repos.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {
    @Autowired
    CourseRepo courseRepo;

    @Autowired
    LearnerRepo learnerRepo;

    @Autowired
    CourseScoreRepo courseScoreRepo;

    @Autowired
    CourseNotiRepo courseNotiRepo;

    @Autowired
    CourseScheduleRepo courseScheduleRepo;

    public CourseDTO convertCourse(Course course){
        return new ModelMapper().map(course, CourseDTO.class);
    }


    public int checkAvailable(CourseDTO courseDTO){
        //loi 1: teacher khong ranh
        //loi 2: phong hoc khong trong
        for( CourseScheduleDTO l: courseDTO.getCourseSchedules()){
            if(courseScheduleRepo.searchByTeacherAndDateTime(l.getTeacher().getId(),l.getDow(),l.getTime_start(), courseDTO.getStart_date())!=null){
                return 1;
            }
            if(courseScheduleRepo.searchByRoom(l.getDow(), l.getTime_start(), l.getRoom().getId(),l.getCourse().getStart_date())!=null){
                return 2;
            }
        }
        return 0;
    }
    @Transactional
    public void createCourse(CourseDTO courseDTO) throws NotAvailableException {
        courseDTO.setName(courseDTO.getName() + courseDTO.getAddress() + courseDTO.getStart_date().toString());
        for (CourseScheduleDTO l : courseDTO.getCourseSchedules()) {
            l.setCourse(courseDTO);
        }
        if (checkAvailable(courseDTO)==0) {
            courseRepo.save(new ModelMapper().map(courseDTO, Course.class));
        }else if(checkAvailable(courseDTO)==1){
            throw new NotAvailableException("Teacher's is not available");
        }else{
            throw new NotAvailableException("Room is not available");
        }
    }

    @Transactional
    public void update(CourseDTO courseDTO) throws NotFoundException, NotAvailableException {
        if(courseRepo.findById(courseDTO.getId()).isPresent()){
            for (CourseScheduleDTO l: courseDTO.getCourseSchedules()){
                l.setCourse(courseDTO);
            }
            if (checkAvailable(courseDTO)==0) {
                courseRepo.save(new ModelMapper().map(courseDTO, Course.class));
            }else if(checkAvailable(courseDTO)==1){
                throw new NotAvailableException("Teacher's is not available");
            }else{
                throw new NotAvailableException("Room is not available");
            }
        }else{
            throw new NotFoundException("Not found course");
        }
    }

    @Transactional
    public void delete(int id){
        courseRepo.deleteById(id);
    }

    @Transactional
    public void courseRegister(LearnerDTO learnerDTO, CourseDTO courseDTO){
        if(learnerRepo.findById(learnerDTO.getId()).isPresent()&&courseRepo.findById(courseDTO.getId()).isPresent()){
           Course course = courseRepo.findById(courseDTO.getId()).orElse(null);
           Learner learner = learnerRepo.findById(learnerDTO.getId()).orElse(null);
            assert course != null;
            course.getLearners().add(new ModelMapper().map(learnerDTO, Learner.class));
            courseRepo.save(course);
            assert learner != null;
            learner.getCourses().add(new ModelMapper().map(courseDTO, Course.class));
            learnerRepo.save(learner);

        }
    }

    public PageDTO<List<CourseDTO>> search(SearchCourseDTO searchCourseDTO){
        Sort sortBy=Sort.by("id").ascending(); //sap xep theo ten va tuoi (mac dinh)


        //sort theo yeu cau
        if(StringUtils.hasText(searchCourseDTO.getSortedField())){ //check xem co empty khong
            sortBy=Sort.by(searchCourseDTO.getSortedField());
        }
        if(searchCourseDTO.getCurrentPage()==null){
            searchCourseDTO.setCurrentPage(0);
        }
        if(searchCourseDTO.getSize()==null){
            searchCourseDTO.setSize(20);
        }

        //tao PageRequest de truyen vao Pageable
        PageRequest pageRequest = PageRequest.of(searchCourseDTO.getCurrentPage(),searchCourseDTO.getSize(),sortBy);
        Page<Course> page = courseRepo.findAll(pageRequest);

        if(StringUtils.hasText(searchCourseDTO.getKeyword())&&searchCourseDTO.getStart_date()==null&&searchCourseDTO.getEnd_date()==null){
            page = courseRepo.searchByName("%"+ searchCourseDTO.getKeyword()+"%",searchCourseDTO.getStatus(), pageRequest);
        }else if(StringUtils.hasText(searchCourseDTO.getKeyword())&&searchCourseDTO.getStart_date()!=null&&searchCourseDTO.getEnd_date()!=null){
            page = courseRepo.searchByNameAndDate("%"+ searchCourseDTO.getKeyword()+"%",searchCourseDTO.getStatus(),searchCourseDTO.getStart_date(), searchCourseDTO.getEnd_date(), pageRequest);
        }else if(!StringUtils.hasText(searchCourseDTO.getKeyword())&&searchCourseDTO.getStart_date()!=null&&searchCourseDTO.getEnd_date()!=null){
            page = courseRepo.searchByDate(searchCourseDTO.getStart_date(),searchCourseDTO.getEnd_date(),searchCourseDTO.getStatus(), pageRequest);
        }
        PageDTO<List<CourseDTO>> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(page.getTotalPages());
        pageDTO.setTotalElements(page.getTotalElements());
        pageDTO.setSize(page.getSize());
        //List<User> users = page.getContent();
        List<CourseDTO> courseDTOS = page.get().map(u->convertCourse(u)).collect(Collectors.toList());

        //T: List<UserDTO>
        pageDTO.setData(courseDTOS);
        return pageDTO;
    }

}
