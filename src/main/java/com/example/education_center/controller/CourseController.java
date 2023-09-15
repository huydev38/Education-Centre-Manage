package com.example.education_center.controller;


import com.example.education_center.dto.*;
import com.example.education_center.dto.search.SearchCourseDTO;
import com.example.education_center.dto.search.SearchNotiDTO;
import com.example.education_center.dto.search.SearchScoreDTO;
import com.example.education_center.exception.NotAuthenticateException;
import com.example.education_center.exception.NotAvailableException;
import com.example.education_center.exception.NotFoundException;
import com.example.education_center.service.*;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    CourseService courseService;

    @Autowired
    LearnerService learnerService;

    @Autowired
    UserService userService;

    @Autowired
    CourseNotiService courseNotiService;

    @Autowired
    CourseScoreService courseScoreService;

    public boolean checkExist(List<LearnerDTO>list, int id){
        for(LearnerDTO l:list){
            if(l.getId()==id){
                return true;
            }
        }
        return false;
    }

    @PostMapping("/")
    public ResponseDTO<Void> createCourse(@RequestBody CourseDTO courseDTO) throws NotAvailableException {
        courseService.createCourse(courseDTO);
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }

    @PutMapping("/")
    public ResponseDTO<CourseDTO> updateCourse(@RequestBody CourseDTO courseDTO) throws NotFoundException, NotAvailableException {
        courseService.update(courseDTO);
        return ResponseDTO.<CourseDTO>builder().msg("Success").status(200).data(courseService.findById(courseDTO.getId())).build();
    }

    @DeleteMapping("/")
    public ResponseDTO<Void> deleteCourse(@RequestParam("id") int id) {
        courseService.delete(id);
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }

    @GetMapping("/search")
    public ResponseDTO<PageDTO<List<CourseDTO>>> search(@RequestBody SearchCourseDTO searchCourseDTO) {
        return ResponseDTO.<PageDTO<List<CourseDTO>>>builder()
                .data(courseService.search(searchCourseDTO)).msg("Success").status(200).build();
    }

    @GetMapping("/{id}")
    public ResponseDTO<CourseDTO> getCourse(@PathParam("id") int id){
        return ResponseDTO.<CourseDTO>builder().data(courseService.findById(id)).build();
    }

    @PostMapping("/noti")
    public ResponseDTO<Void> newNoti(@RequestBody CourseNotiDTO courseNotiDTO){
        courseNotiService.addNoti(courseNotiDTO);

        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }

    @PutMapping("/noti")
    public ResponseDTO<CourseNotiDTO> updateNoti(@RequestBody CourseNotiDTO courseNotiDTO){
        courseNotiService.updateNoti(courseNotiDTO);
        return ResponseDTO.<CourseNotiDTO>builder().msg("Success").status(200).
                data(courseNotiService.findByNotiId(courseNotiDTO.getId())).build();
    }

    @DeleteMapping("/noti")
    public ResponseDTO<Void> deleteNoti(@RequestParam("id") int id){
        courseNotiService.deleteNoti(id);
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }

    //for teacher and admin
    @GetMapping("/search/noti")
    public ResponseDTO<List<CourseNotiDTO>> searchNoti(@RequestBody CourseDTO courseDTO){
        return ResponseDTO.<List<CourseNotiDTO>>builder().msg("Success")
                .data(courseNotiService.getCourseNoti(courseDTO)).status(200).build();
    }

    @GetMapping("/noti/{id}")
    public ResponseDTO<CourseNotiDTO> getCourseNoti(@PathParam("id") int id) {
        return ResponseDTO.<CourseNotiDTO>builder().data(courseNotiService.findByNotiId(id)).build();
    }

    @PostMapping("/score")
    public ResponseDTO<Void> addCourseScore(@RequestBody List<CourseScoreDTO> list){
        courseScoreService.addCourseScore(list);
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }

    @PutMapping("/score")
    public ResponseDTO<CourseScoreDTO> updateCourseScore(@RequestBody CourseScoreDTO scoreDTO){
        courseScoreService.updateCourseScore(scoreDTO);
        return ResponseDTO.<CourseScoreDTO>builder().msg("Success").status(200)
                .data(courseScoreService.findByScoreId(scoreDTO.getId())).build();
    }

    @DeleteMapping("/score")
    public ResponseDTO<Void> deleteScore(@RequestParam("id")int id){
        courseScoreService.deteleCourseScore(id);
        return ResponseDTO.<Void>builder().msg("Success delete score id "+id).status(200).build();
    }

    @GetMapping("/score/search")
    public ResponseDTO<PageDTO<List<CourseScoreDTO>>> searchScore(@RequestBody SearchScoreDTO searchScoreDTO){
        return ResponseDTO.<PageDTO<List<CourseScoreDTO>>>builder().status(200).
                data(courseScoreService.searchScore(searchScoreDTO)).build();
    }

    @GetMapping("/score/{id}")
    public ResponseDTO<CourseScoreDTO> getCourseScore(@PathVariable("id") int id) {
        return ResponseDTO.<CourseScoreDTO>builder().data(courseScoreService.findByScoreId(id)).build();
    }

    //admin, teacher
    @PostMapping("/add")
    public ResponseDTO<Void> addLearner(@RequestParam("course_id") int course_id, @RequestParam("learner_id")int leaner_id) throws NotFoundException, NotAvailableException {
        courseService.addLearner(leaner_id, course_id);
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }

    @PostMapping("/enroll")
    public ResponseDTO<Void> enrollCourse(@RequestParam("course_id")int course_id, Principal p) throws NotFoundException, NotAvailableException {
        UserDTO user = userService.findByUsername(p.getName());
        LearnerDTO l = learnerService.findByUserId(user.getId());
        courseService.registerCourse(course_id, l.getId());
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }

    @PostMapping("/getCourseNoti")
    public ResponseDTO<PageDTO<List<CourseNotiDTO>>> getCourseNoti(@RequestBody SearchNotiDTO searchNotiDTO, Principal p) throws NotAuthenticateException {
        UserDTO user = userService.findByUsername(p.getName());
        LearnerDTO l = learnerService.findByUserId(user.getId());
        List<LearnerDTO> list = courseService.findById(searchNotiDTO.getCourseDTO().getId()).getLearners();
        if(checkExist(list, l.getId())){
            return ResponseDTO.<PageDTO<List<CourseNotiDTO>>>builder().data(courseNotiService.searchNoti(searchNotiDTO))
                    .msg("Success").status(200).build();
        }
        else{
            throw new NotAuthenticateException("Invalid username");
        }
    }
    @PostMapping("/getCourseScore")
    public ResponseDTO<PageDTO<List<CourseScoreDTO>>> getCourseScore(@RequestBody SearchScoreDTO searchScoreDTO, Principal p) throws NotAuthenticateException {
        UserDTO user = userService.findByUsername(p.getName());
        LearnerDTO l = learnerService.findByUserId(user.getId());
        List<LearnerDTO> list = courseService.findById(searchScoreDTO.getCourseDTO().getId()).getLearners();
        if(checkExist(list, l.getId())){
            return ResponseDTO.<PageDTO<List<CourseScoreDTO>>>builder().data(courseScoreService.searchScore(searchScoreDTO))
                    .msg("Success").status(200).build();
        }
        else{
            throw new NotAuthenticateException("Invalid username");
        }
    }
}
