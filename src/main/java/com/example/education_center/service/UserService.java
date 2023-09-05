package com.example.education_center.service;

import com.example.education_center.dto.UserDTO;
import com.example.education_center.entity.User;
import com.example.education_center.repos.UserRepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepo userRepo;

    @Transactional
    public void updatePassword(UserDTO userDTO) {
        if (userRepo.findById(userDTO.getId()).isPresent()) {

            User user = new ModelMapper().map(userDTO, User.class);
            //thieu doan set password bao mat
            //user.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
            userRepo.save(user);

        }
    }

    public UserDTO findByUsername(String username) {
        return new ModelMapper().map(userRepo.findByUsername(username), UserDTO.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User userEntity = userRepo.findByUsername(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException("Not Found");
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(userEntity.getRole()));
        return new org.springframework.security.core.userdetails.User(username, userEntity.getPassword(), authorities);
    }
}

