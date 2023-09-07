package com.example.education_center.controller;

import com.example.education_center.dto.PageDTO;
import com.example.education_center.dto.ResponseDTO;
import com.example.education_center.dto.TeacherDTO;
import com.example.education_center.dto.search.SearchTeacherDTO;
import com.example.education_center.exception.NotFoundException;
import com.example.education_center.service.TeacherService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Value("${upload.folder}")
    String Upload_Folder;
    @Autowired
    TeacherService teacherService;

    @PostMapping("/")
    public ResponseDTO<Void> createTeacher(@ModelAttribute TeacherDTO teacherDTO) throws IOException {
        if(! new File(Upload_Folder).exists()){
            new File(Upload_Folder).mkdirs();
        }
        if(!teacherDTO.getFile().isEmpty()){
            String filename = teacherDTO.getFile().getOriginalFilename();

            assert filename != null;
            String extension = filename.substring(filename.lastIndexOf("."));
            String newFilename = UUID.randomUUID() + extension;

            File saveFile = new File(Upload_Folder + newFilename);

            teacherDTO.getFile().transferTo(saveFile);
            teacherDTO.setAvatar_url(newFilename); //luu file xuong db
        }
        teacherService.create(teacherDTO);
        return ResponseDTO.<Void>builder().status(200).msg("Success").build();
    }

    @PutMapping("/")
    public ResponseDTO<TeacherDTO> updateTeacher(@ModelAttribute TeacherDTO teacherDTO) throws NotFoundException, IOException {
        if (!new File(Upload_Folder).exists()) {
            new File(Upload_Folder).mkdirs();
        }
        if (!teacherDTO.getFile().isEmpty()) {
            String filename = teacherDTO.getFile().getOriginalFilename();

            assert filename != null;
            String extension = filename.substring(filename.lastIndexOf("."));
            String newFilename = UUID.randomUUID() + extension;

            File saveFile = new File(Upload_Folder + newFilename);

            teacherDTO.getFile().transferTo(saveFile);
            teacherDTO.setAvatar_url(newFilename); //luu file xuong db
        }
        teacherService.update(teacherDTO);
        return ResponseDTO.<TeacherDTO>builder().status(200).msg("Success").build();
    }

    @DeleteMapping("/")
    public ResponseDTO<Void> deleteTeacher(@RequestParam("id") int id){
        teacherService.delete(id);
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }

    @GetMapping("/search")
    public ResponseDTO<PageDTO<List<TeacherDTO>>> search(@RequestBody SearchTeacherDTO searchTeacherDTO){
        return ResponseDTO.<PageDTO<List<TeacherDTO>>>builder().data(teacherService.search(searchTeacherDTO)).build();
    }

    @GetMapping("/{id}")
    public ResponseDTO<TeacherDTO> getTeacher(@PathVariable("id") int id){
        return ResponseDTO.<TeacherDTO>builder().data(teacherService.findById(id)).build();
    }
}
