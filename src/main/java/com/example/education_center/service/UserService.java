package com.example.education_center.service;

import com.example.education_center.dto.UserDTO;
import com.example.education_center.entity.User;
import com.example.education_center.repos.UserRepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepo userRepo;

    @Transactional
    public void updatePassword(UserDTO userDTO){
        if(userRepo.findById(userDTO.getId()).isPresent()){

            User user = new ModelMapper().map(userDTO, User.class);
            //thieu doan set password bao mat
            //user.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
            userRepo.save(user);

        }
    }

}
