package com.example.education_center.service;

import com.example.education_center.dto.UserDTO;
import com.example.education_center.email.EmailService;
import com.example.education_center.entity.Learner;
import com.example.education_center.entity.User;
import com.example.education_center.repos.UserRepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepo userRepo;

    @Autowired
    EmailService emailService;

    public UserDTO convert(User user){
        return new ModelMapper().map(user, UserDTO.class);
    }

    @Transactional
    public void createAdmin(UserDTO userDTO){
        userDTO.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
        userDTO.setRole("ROLE_ADMIN");
        userRepo.save(new ModelMapper().map(userDTO, User.class));
    }

    @Transactional
    public void updateAdmin(UserDTO userDTO){
        userDTO.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
        userDTO.setRole("ROLE_ADMIN");
        userRepo.save(new ModelMapper().map(userDTO, User.class));
    }

    @Transactional
    public void updatePassword(UserDTO userDTO) {
        if (userRepo.findById(userDTO.getId()).isPresent()) {

            User user = new ModelMapper().map(userDTO, User.class);
            //thieu doan set password bao mat
            //user.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
            userRepo.save(user);

        }
    }

    @Transactional
    public void deleteAdmin(int id){
        userRepo.deleteById(id);
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

    public UserDTO findById(int id) {
        return new ModelMapper().map(userRepo.findById(id), UserDTO.class);
    }

    public List<UserDTO> findUserByBirthday(Date now) {
        return userRepo.findByBirthdate(now).stream().map(this::convert).collect(Collectors.toList());
    }
}

