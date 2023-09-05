package com.example.education_center.controller;

import com.example.education_center.dto.UserDTO;
import com.example.education_center.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
