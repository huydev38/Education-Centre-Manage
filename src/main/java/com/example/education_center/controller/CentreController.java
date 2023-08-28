package com.example.education_center.controller;

import com.example.education_center.dto.CentreDTO;
import com.example.education_center.dto.PageDTO;
import com.example.education_center.dto.ResponseDTO;
import com.example.education_center.dto.search.SearchDTO;
import com.example.education_center.exception.NotFoundException;
import com.example.education_center.service.CentreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/centre")
@Slf4j
public class CentreController {

    @Autowired
    CentreService centreService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseDTO<Void> newCentre(@RequestBody CentreDTO centreDTO){
        centreService.create(centreDTO);
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }

    @PutMapping("/")
    public ResponseDTO<CentreDTO> updateCentre(@RequestBody CentreDTO centreDTO) throws NotFoundException {
        centreService.update(centreDTO);
        return ResponseDTO.<CentreDTO>builder().msg("Success").status(200).data(centreDTO).build();
    }

    @DeleteMapping("/")
    public ResponseDTO<Void> deleteCentre(@RequestParam("id") int id){
        centreService.delete(id);
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }

    @GetMapping("/search")
    public ResponseDTO<PageDTO<List<CentreDTO>>> searchCentre(@RequestBody SearchDTO searchDTO){
        return ResponseDTO.<PageDTO<List<CentreDTO>>>builder().msg("Success").data(centreService.search(searchDTO)).status(200).build();
    }
}
