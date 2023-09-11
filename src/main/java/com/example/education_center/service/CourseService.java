package com.example.education_center.service;
import com.example.education_center.dto.*;
import com.example.education_center.dto.search.SearchCourseDTO;
import com.example.education_center.dto.search.SearchNotiDTO;
import com.example.education_center.dto.search.SearchScoreDTO;
import com.example.education_center.email.EmailService;
import com.example.education_center.entity.*;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class CourseService {
    @Autowired
    CourseRepo courseRepo;

    @Autowired
    LearnerRepo learnerRepo;

    @Autowired
    CourseScheduleRepo courseScheduleRepo;

    @Autowired
    EmailService emailService;

    @Autowired
    CourseRequestRepo courseRequestRepo;

    public CourseDTO convertCourse(Course course){
        return new ModelMapper().map(course, CourseDTO.class);
    }

    public CourseRequestDTO convertCourseRequest(CourseRequest course){
        return new ModelMapper().map(course, CourseRequestDTO.class);
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


    //for teacher
    //admin
    @Transactional
    public void addLearner(int learner_id, int course_id) throws NotAvailableException, NotFoundException {
        if (learnerRepo.findById(learner_id).isPresent() && courseRepo.findById(course_id).isPresent()) {
            Course course = courseRepo.findById(course_id).orElse(null);
            Learner learner = learnerRepo.findById(learner_id).orElse(null);
            if (course == null || learner == null) {
                throw new NotFoundException("Can't find course or learner");
            } else {
                if (course.getIsOpen() == 1) {
                    course.getLearners().add(learner);
                    course.setRemain(course.getRemain() - 1);
                    if(course.getRemain()==0){
                        course.setIsOpen(0);
                    }
                    courseRepo.save(course);
                    learner.setTuition_fee(learner.getTuition_fee() + course.getCost());
                    learner.getCourses().add(course);
                    learnerRepo.save(learner);

                } else {
                    throw new NotAvailableException("Can't add learner to this course");
                }
            }
        }
    }

    //for learners
    @Transactional
    public void registerCourse(int course_id, int learner_id) throws NotAvailableException, NotFoundException {
        if (learnerRepo.findById(learner_id).isPresent() && courseRepo.findById(course_id).isPresent()) {
            Course course = courseRepo.findById(course_id).orElse(null);
            Learner learner = learnerRepo.findById(learner_id).orElse(null);
            if (course == null || learner == null) {
                throw new NotFoundException("Invalid course or learner");
            } else {
                if (course.getIsOpen() == 1) {
                    CourseRequest c = new CourseRequest();
                    c.setLearner(learner);
                    c.setCourse(course);
                    c.setStatus(2);
                    courseRequestRepo.save(c);
                    emailService.sendNotiEmail(learner.getUser().getEmail(),"You have been added to new course",course.getName());
                } else {
                    throw new NotAvailableException("Can't add learner to this course");
                }
            }
        }

    }

    @Transactional
    public void checkRequest(int request_id, int isAccept) throws NotFoundException {
        CourseRequest courseRequest = courseRequestRepo.findById(request_id).orElse(null);
        if(courseRequest!=null){
            Course c = courseRequest.getCourse();
            Learner l = courseRequest.getLearner();
            if(isAccept==0){
                courseRequest.setStatus(0);
            }else{
                courseRequest.setStatus(1);
                c.getLearners().add(l);
                c.setRemain(c.getRemain() - 1);
                if(c.getRemain()==0){
                    c.setIsOpen(0);
                }
                courseRepo.save(c);
                l.setTuition_fee(l.getTuition_fee() + c.getCost());
                l.getCourses().add(c);
                learnerRepo.save(l);
                emailService.sendNotiEmail(l.getUser().getEmail(),"You have been added to new course",c.getName());
            }
            courseRequestRepo.save(courseRequest);

        }else{
            throw new NotFoundException("Invalid request id");
        }
    }

    public List<CourseRequestDTO> getRequestByLearner(int learner_id){
        return courseRequestRepo.findByLearner(learner_id).stream().map(this::convertCourseRequest).collect(Collectors.toList());
    }

    public List<CourseRequestDTO> getRequestByCourse(int course_id){
        return courseRequestRepo.findByCourse(course_id).stream().map(this::convertCourseRequest).collect(Collectors.toList());
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

    public CourseDTO findById(int id) {
        return new ModelMapper().map(courseRepo.findById(id), CourseDTO.class);
    }

    public void checkStatus(){
        Date now= new Date();
        List<Course> list = courseRepo.searchByEndDateAndStatus(now);
        for(Course c: list){
            c.setStatus(0);
            courseRepo.save(c);
        }
    }

    public void checkOpen(){
        Date now= new Date();
        List<Course> list = courseRepo.searchByStartDateAndIsOpen(now);
        for(Course c: list){
            c.setIsOpen(0);
            courseRepo.save(c);
        }
    }



}
