package com.example.education_center.controller;

import com.example.education_center.dto.LearnerDTO;
import com.example.education_center.dto.PageDTO;
import com.example.education_center.dto.ResponseDTO;
import com.example.education_center.dto.search.SearchLearnerDTO;
import com.example.education_center.exception.NotFoundException;
import com.example.education_center.service.LearnerService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/learner")
public class LearnerController {
    //search: nếu có mail với phone thì search hàm riêng, nếu có keyword hoac khong co gi (search phân trang theo tên)

    @Autowired
    LearnerService learnerService;

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

}
