package com.example.education_center.service;

import com.example.education_center.dto.*;
import com.example.education_center.dto.search.SearchCourseDTO;
import com.example.education_center.dto.search.SearchNotiDTO;
import com.example.education_center.dto.search.SearchScoreDTO;
import com.example.education_center.entity.Course;
import com.example.education_center.entity.CourseNoti;
import com.example.education_center.entity.CourseScore;
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

import java.util.ArrayList;
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

    public CourseNotiDTO convertNoti(CourseNoti courseNoti){
        return new ModelMapper().map(courseNoti, CourseNotiDTO.class);
    }

    public CourseScoreDTO convertScore(CourseScore courseScore){
        return new ModelMapper().map(courseScore, CourseScoreDTO.class);
    }

    public int checkAvailable(CourseDTO courseDTO){
        //loi 1: teacher khong ranh
        //loi 2: phong hoc khong trong
        //loi 3: thoi gian course sai
        if(courseDTO.getStart_date().after(courseDTO.getEnd_date())){
            return 3;
        }
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
        }else if(checkAvailable(courseDTO)==1) {
            throw new NotAvailableException("Teacher's is not available");
        }else if(checkAvailable(courseDTO)==3){
            throw new NotAvailableException("Date invalid");
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
    public void courseRegister(LearnerDTO learnerDTO, CourseDTO courseDTO) throws NotAvailableException, NotFoundException {
        if (learnerRepo.findById(learnerDTO.getId()).isPresent() && courseRepo.findById(courseDTO.getId()).isPresent()) {
            Course course = courseRepo.findById(courseDTO.getId()).orElse(null);
            Learner learner = learnerRepo.findById(learnerDTO.getId()).orElse(null);
            if (course == null || learner == null) {
                throw new NotFoundException("Cant find course or learner");
            } else {
                if (course.getIsOpen() == 1) {
                    course.getLearners().add(new ModelMapper().map(learnerDTO, Learner.class));
                    course.setRemain(course.getRemain() - 1);

                    courseRepo.save(course);
                    learner.setTuition_fee(learner.getTuition_fee() + course.getCost());
                    learner.getCourses().add(new ModelMapper().map(courseDTO, Course.class));
                    learnerRepo.save(learner);
                } else {
                    throw new NotAvailableException("Cannot enroll to this course");
                }
            }
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

    @Transactional
    public void addNoti(CourseNotiDTO courseNotiDTO){
        courseNotiRepo.save(new ModelMapper().map(courseNotiDTO, CourseNoti.class));
    }

    @Transactional
    public void deleteNoti(int id){
        courseNotiRepo.deleteById(id);
    }

    @Transactional
    public void updateNoti(CourseNotiDTO courseNotiDTO){
        courseNotiRepo.save(new ModelMapper().map(courseNotiDTO, CourseNoti.class));
    }

    public PageDTO<List<CourseNotiDTO>> searchNoti(SearchNotiDTO searchNotiDTO){
        Sort sortBy=Sort.by("id").ascending(); //sap xep theo ten va tuoi (mac dinh)


        //sort theo yeu cau
        if(StringUtils.hasText(searchNotiDTO.getSortedField())){ //check xem co empty khong
            sortBy=Sort.by(searchNotiDTO.getSortedField());
        }
        if(searchNotiDTO.getCurrentPage()==null){
            searchNotiDTO.setCurrentPage(0);
        }
        if(searchNotiDTO.getSize()==null){
            searchNotiDTO.setSize(20);
        }

        //tao PageRequest de truyen vao Pageable
        PageRequest pageRequest = PageRequest.of(searchNotiDTO.getCurrentPage(),searchNotiDTO.getSize(),sortBy);
        Page<CourseNoti> page = courseNotiRepo.findAll(pageRequest);

        if(searchNotiDTO.getCourseDTO()!=null&&searchNotiDTO.getStart_date()==null&&searchNotiDTO.getEnd_date()==null){
            page = courseNotiRepo.searchByCourse(searchNotiDTO.getCourseDTO().getId(), pageRequest);
        }else if(searchNotiDTO.getCourseDTO()!=null&&searchNotiDTO.getStart_date()!=null&&searchNotiDTO.getEnd_date()!=null){
            page = courseNotiRepo.searchByCourseAndDate(searchNotiDTO.getCourseDTO().getId(),searchNotiDTO.getStart_date(), searchNotiDTO.getEnd_date(), pageRequest);
        }
        PageDTO<List<CourseNotiDTO>> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(page.getTotalPages());
        pageDTO.setTotalElements(page.getTotalElements());
        pageDTO.setSize(page.getSize());
        //List<User> users = page.getContent();
        List<CourseNotiDTO> courseDTOS = page.get().map(u->convertNoti(u)).collect(Collectors.toList());

        //T: List<UserDTO>
        pageDTO.setData(courseDTOS);
        return pageDTO;
    }

    public PageDTO<List<CourseNotiDTO>> searchNotiByLearner(SearchNotiDTO searchNotiDTO, List<CourseDTO> courseDTOList){
        Sort sortBy=Sort.by("createdAt").descending(); //sap xep theo ten va tuoi (mac dinh)
        List<Integer> course_ids = new ArrayList<>();
        for(CourseDTO c:courseDTOList){
            course_ids.add(c.getId());
        }

        //sort theo yeu cau
        if(StringUtils.hasText(searchNotiDTO.getSortedField())){ //check xem co empty khong
            sortBy=Sort.by(searchNotiDTO.getSortedField());
        }
        if(searchNotiDTO.getCurrentPage()==null){
            searchNotiDTO.setCurrentPage(0);
        }
        if(searchNotiDTO.getSize()==null){
            searchNotiDTO.setSize(20);
        }

        //tao PageRequest de truyen vao Pageable
        PageRequest pageRequest = PageRequest.of(searchNotiDTO.getCurrentPage(),searchNotiDTO.getSize(),sortBy);
        Page<CourseNoti> page = courseNotiRepo.searchByUser(course_ids,pageRequest);

        if(searchNotiDTO.getStart_date()!=null&&searchNotiDTO.getEnd_date()!=null){
            page = courseNotiRepo.searchByUserAndDate(course_ids,searchNotiDTO.getStart_date(), searchNotiDTO.getEnd_date(), pageRequest);
        }
        PageDTO<List<CourseNotiDTO>> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(page.getTotalPages());
        pageDTO.setTotalElements(page.getTotalElements());
        pageDTO.setSize(page.getSize());
        //List<User> users = page.getContent();
        List<CourseNotiDTO> courseDTOS = page.get().map(u->convertNoti(u)).collect(Collectors.toList());

        //T: List<UserDTO>
        pageDTO.setData(courseDTOS);
        return pageDTO;
    }

    public List<CourseNotiDTO> getCourseNoti(CourseDTO courseDTO){
        return courseNotiRepo.findByCourse(courseDTO.getId()).stream().map(this::convertNoti).collect(Collectors.toList());
    }

    @Transactional
    public void addCourseScore(List<CourseScoreDTO> courseScoreDTO){
        for(CourseScoreDTO c: courseScoreDTO){
            courseScoreRepo.save(new ModelMapper().map(c, CourseScore.class));
        }
    }

    @Transactional
    public void updateCourseScore(CourseScoreDTO courseScoreDTO){
        if(courseScoreRepo.findById(courseScoreDTO.getId()).isPresent()){
            courseScoreRepo.save(new ModelMapper().map(courseScoreDTO, CourseScore.class));
        }
    }

    @Transactional
    public void deteleCourseScore(int id){
        courseScoreRepo.deleteById(id);
    }

    public PageDTO<List<CourseScoreDTO>> searchScore(SearchScoreDTO searchScoreDTO) {
        Sort sortBy = Sort.by("id").ascending(); //sap xep theo ten va tuoi (mac dinh)


        //sort theo yeu cau
        if (StringUtils.hasText(searchScoreDTO.getSortedField())) { //check xem co empty khong
            sortBy = Sort.by(searchScoreDTO.getSortedField());
        }
        if (searchScoreDTO.getCurrentPage() == null) {
            searchScoreDTO.setCurrentPage(0);
        }
        if (searchScoreDTO.getSize() == null) {
            searchScoreDTO.setSize(20);
        }

        //tao PageRequest de truyen vao Pageable
        PageRequest pageRequest = PageRequest.of(searchScoreDTO.getCurrentPage(), searchScoreDTO.getSize(), sortBy);
        Page<CourseScore> page = courseScoreRepo.findAll(pageRequest);

        if(searchScoreDTO.getCourseDTO() != null && searchScoreDTO.getStart_date() != null && searchScoreDTO.getEnd_date() != null && searchScoreDTO.getLearnerDTO() == null) {
            page = courseScoreRepo.searchByCourseAndDate(searchScoreDTO.getCourseDTO().getId(), searchScoreDTO.getStart_date(), searchScoreDTO.getEnd_date(), pageRequest);
        } else if (searchScoreDTO.getCourseDTO() == null && searchScoreDTO.getStart_date() != null && searchScoreDTO.getEnd_date() != null && searchScoreDTO.getLearnerDTO() != null) {
            page = courseScoreRepo.searchByLearnerAndDate(searchScoreDTO.getLearnerDTO().getId(), searchScoreDTO.getStart_date(), searchScoreDTO.getEnd_date(), pageRequest);
        }else if(searchScoreDTO.getCourseDTO() != null && searchScoreDTO.getLearnerDTO() != null){
            page = courseScoreRepo.searchByLearnerAndCourse(searchScoreDTO.getLearnerDTO().getId(),searchScoreDTO.getCourseDTO().getId(), pageRequest);
        }else if (searchScoreDTO.getCourseDTO() != null) {
            page = courseScoreRepo.searchByCourse(searchScoreDTO.getCourseDTO().getId(), pageRequest);
        }else if (searchScoreDTO.getLearnerDTO() != null) {
            page = courseScoreRepo.searchByLearner(searchScoreDTO.getLearnerDTO().getId(), pageRequest);
        }
        PageDTO<List<CourseScoreDTO>> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(page.getTotalPages());
        pageDTO.setTotalElements(page.getTotalElements());
        pageDTO.setSize(page.getSize());
        //List<User> users = page.getContent();
        List<CourseScoreDTO> courseDTOS = page.get().map(u -> convertScore(u)).collect(Collectors.toList());

        //T: List<UserDTO>
        pageDTO.setData(courseDTOS);
        return pageDTO;
    }

    public Double getGPA(SearchScoreDTO searchScoreDTO){
        double gpa = 0;
        gpa = courseScoreRepo.avgScore(searchScoreDTO.getCourseDTO().getId(), searchScoreDTO.getLearnerDTO().getId());
        return gpa;
    }


    public CourseDTO findById(int id) {
        return new ModelMapper().map(courseRepo.findById(id), CourseDTO.class);
    }

    public CourseNotiDTO findByNotiId(int id) {
        return new ModelMapper().map(courseNotiRepo.findById(id), CourseNotiDTO.class);
    }

    public CourseScoreDTO findByScoreId(int id) {
        return new ModelMapper().map(courseScoreRepo.findById(id), CourseScoreDTO.class);
    }

}
