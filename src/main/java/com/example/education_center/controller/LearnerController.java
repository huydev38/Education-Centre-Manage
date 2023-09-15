package com.example.education_center.controller;

import com.example.education_center.dto.*;
import com.example.education_center.dto.search.SearchLearnerDTO;
import com.example.education_center.dto.search.SearchNotiDTO;
import com.example.education_center.dto.search.SearchScoreDTO;
import com.example.education_center.entity.User;
import com.example.education_center.exception.NotAuthenticateException;
import com.example.education_center.exception.NotFoundException;
import com.example.education_center.service.*;
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

    @Autowired
    CourseScoreService courseScoreService;

    @Autowired
    CourseNotiService courseNotiService;




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
    public ResponseDTO<LearnerDTO> getLearner(@PathVariable("id") int id){
        return ResponseDTO.<LearnerDTO>builder().data(learnerService.findById(id)).build();
    }

    @GetMapping("/search")
    public ResponseDTO<PageDTO<List<LearnerDTO>>> search(@RequestBody SearchLearnerDTO searchLearnerDTO){
        return ResponseDTO.<PageDTO<List<LearnerDTO>>>builder().data(learnerService.search(searchLearnerDTO)).build();
    }

    @PostMapping("/getGPA")
    public ResponseDTO<Double> getGpa(@RequestBody SearchScoreDTO searchScoreDTO, Principal p) throws NotAuthenticateException {
        if(p.getName().equals(searchScoreDTO.getLearnerDTO().getName())){
            return ResponseDTO.<Double>builder().data(courseScoreService.getGPA(searchScoreDTO)).build();
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
        return ResponseDTO.<PageDTO<List<CourseNotiDTO>>>builder().data(courseNotiService.searchNotiByLearner(searchNotiDTO,listCourse))
                .status(200).msg("Success").build();
    }

    @PostMapping("/getScore")
    public ResponseDTO<PageDTO<List<CourseScoreDTO>>> getAllScore(@RequestBody SearchScoreDTO searchScoreDTO, Principal p){
        //khong can input learnerDTO trong search
        UserDTO user = userService.findByUsername(p.getName());
        LearnerDTO l = learnerService.findByUserId(user.getId());
        searchScoreDTO.setLearnerDTO(l);
        return ResponseDTO.<PageDTO<List<CourseScoreDTO>>>builder().data(courseScoreService.searchScoreByUser(searchScoreDTO)).msg("Success").status(200).build();

    }




}
