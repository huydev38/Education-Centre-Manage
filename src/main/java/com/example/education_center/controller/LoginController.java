package com.example.education_center.controller;

import com.example.education_center.dto.ResponseDTO;
import com.example.education_center.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenService jwtTokenService;

    @PostMapping("/login")
    public ResponseDTO<String> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
        return ResponseDTO.<String>builder().status(200).data(jwtTokenService.createToken(username)).build();
    }
}
