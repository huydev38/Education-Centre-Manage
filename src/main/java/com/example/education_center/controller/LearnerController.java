package com.example.education_center.controller;

import com.example.education_center.dto.*;
import com.example.education_center.dto.search.SearchLearnerDTO;
import com.example.education_center.dto.search.SearchNotiDTO;
import com.example.education_center.dto.search.SearchScoreDTO;
import com.example.education_center.entity.User;
import com.example.education_center.exception.NotAuthenticateException;
import com.example.education_center.exception.NotFoundException;
import com.example.education_center.service.CourseService;
import com.example.education_center.service.LearnerService;
import com.example.education_center.service.UserService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/learner")
public class LearnerController {
    //search: nếu có mail với phone thì search hàm riêng, nếu có keyword hoac khong co gi (search phân trang theo tên)

    @Autowired
    LearnerService learnerService;

    @Autowired
    CourseService courseService;

    @Autowired
    UserService userService;




    @PostMapping("/")
    public ResponseDTO<Void> createLearner(@RequestBody LearnerDTO learnerDTO){
        learnerService.create(learnerDTO);
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }

    @PutMapping("/")
    public ResponseDTO<LearnerDTO> updateLearner(@RequestBody LearnerDTO learnerDTO) throws NotFoundException {
        learnerService.update(learnerDTO);
        return ResponseDTO.<LearnerDTO>builder().msg("Success").status(200).data(learnerService.findById(learnerDTO.getId())).build();
    }

    @DeleteMapping("/")
    public ResponseDTO<Void> deleteLearner(@RequestParam("id") int id){
        learnerService.delete(id);
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }

    @GetMapping("/{id}")
    public ResponseDTO<LearnerDTO> getLearner(@PathParam("id") int id){
        return ResponseDTO.<LearnerDTO>builder().data(learnerService.findById(id)).build();
    }

    @GetMapping("/search")
    public ResponseDTO<PageDTO<List<LearnerDTO>>> search(@RequestBody SearchLearnerDTO searchLearnerDTO){
        return ResponseDTO.<PageDTO<List<LearnerDTO>>>builder().data(learnerService.search(searchLearnerDTO)).build();
    }

    @PostMapping("/getGPA")
    public ResponseDTO<Double> getGpa(@RequestBody SearchScoreDTO searchScoreDTO, Principal p) throws NotAuthenticateException {
        if(p.getName().equals(searchScoreDTO.getLearnerDTO().getName())){
            return ResponseDTO.<Double>builder().data(courseService.getGPA(searchScoreDTO)).build();
        }
        else{
            throw new NotAuthenticateException("Invalid username");
        }
    }

    public boolean checkExist(List<LearnerDTO>list, int id){
        for(LearnerDTO l:list){
            if(l.getId()==id){
                return true;
            }
        }
        return false;
    }
    @PostMapping("/getCourseNoti")
    public ResponseDTO<PageDTO<List<CourseNotiDTO>>> getCourseNoti(@RequestBody SearchNotiDTO searchNotiDTO, Principal p) throws NotAuthenticateException {
        UserDTO user = userService.findByUsername(p.getName());
        LearnerDTO l = learnerService.findByUserId(user.getId());
        List<LearnerDTO> list = courseService.findById(searchNotiDTO.getCourseDTO().getId()).getLearners();
        if(checkExist(list, l.getId())){
           return ResponseDTO.<PageDTO<List<CourseNotiDTO>>>builder().data(courseService.searchNoti(searchNotiDTO))
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
            return ResponseDTO.<PageDTO<List<CourseScoreDTO>>>builder().data(courseService.searchScore(searchScoreDTO))
                    .msg("Success").status(200).build();
        }
        else{
            throw new NotAuthenticateException("Invalid username");
        }
    }

    //all noti
    //khong can dien courseDTO
    @PostMapping("/getNoti")
    public ResponseDTO<PageDTO<List<CourseNotiDTO>>> getAllNoti(@RequestBody SearchNotiDTO searchNotiDTO, Principal p) throws NotAuthenticateException {
        UserDTO user = userService.findByUsername(p.getName());
        LearnerDTO l = learnerService.findByUserId(user.getId());
        List<CourseDTO> listCourse = l.getCourses();
        return ResponseDTO.<PageDTO<List<CourseNotiDTO>>>builder().data(courseService.searchNotiByLearner(searchNotiDTO,listCourse))
                .status(200).msg("Success").build();
    }

//    @PostMapping("/getScore")
//    //TODO

}
