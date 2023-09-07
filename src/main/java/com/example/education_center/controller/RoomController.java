package com.example.education_center.controller;

import com.example.education_center.dto.AddressDTO;
import com.example.education_center.dto.PageDTO;
import com.example.education_center.dto.ResponseDTO;
import com.example.education_center.dto.RoomDTO;
import com.example.education_center.dto.search.SearchRoomDTO;
import com.example.education_center.entity.Room;
import com.example.education_center.service.RoomService;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/room")
public class RoomController {
    @Autowired
    RoomService roomService;

    @PostMapping("/")
    public ResponseDTO<Void> createRoom(@RequestBody RoomDTO roomDTO){
        roomService.create(roomDTO);
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }

    @PutMapping("/")
    public ResponseDTO<RoomDTO> updateRoom(@RequestBody RoomDTO roomDTO){
        roomService.update(roomDTO);
        return ResponseDTO.<RoomDTO>builder().msg("Success").status(200).data(roomService.findById(roomDTO.getId())).build();
    }

    @DeleteMapping("/")
    public ResponseDTO<Void> deleteRoom(@RequestParam("id") int id){
        roomService.delete(id);
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }

    @GetMapping("/search")
    public ResponseDTO<PageDTO<List<RoomDTO>>> searchRoom(@RequestBody SearchRoomDTO searchRoomDTO){
        return ResponseDTO.<PageDTO<List<RoomDTO>>>builder().data(roomService.search(searchRoomDTO)).msg("Success").status(200).build();
    }

    @GetMapping("/{id}")
    public ResponseDTO<RoomDTO> getRoom(@PathVariable("id") Integer id){
//        if(id==null) {
//            log.info("null id");
//        }
        return ResponseDTO.<RoomDTO>builder().data(roomService.findById(id)).build();
    }

}
