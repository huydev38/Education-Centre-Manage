package com.example.education_center.controller;

import com.example.education_center.dto.ResponseDTO;
import com.example.education_center.dto.UserDTO;
import com.example.education_center.entity.Learner;
import com.example.education_center.entity.User;
import com.example.education_center.service.UserService;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class UserController{
    @Autowired
    UserService userService;

    @GetMapping("/me")
    public UserDTO me(Principal p){
        String username = p.getName();
        return userService.findByUsername(username);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/")
    public ResponseDTO<Void> createAdminAccount(@RequestBody UserDTO userDTO){
        userService.createAdmin(userDTO);
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/")
    public ResponseDTO<UserDTO> updateAdminAccount(@RequestBody UserDTO userDTO){
        userService.updateAdmin(userDTO);
        return ResponseDTO.<UserDTO>builder().msg("Success").status(200)
                .data(userService.findById(userDTO.getId())).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/")
    public ResponseDTO<Void> deleteAdminAccount(@RequestParam("id") int id){
        userService.deleteAdmin(id);
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }
}
