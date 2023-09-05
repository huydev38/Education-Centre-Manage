package com.example.education_center.service;

import com.example.education_center.dto.LearnerDTO;
import com.example.education_center.dto.PageDTO;
import com.example.education_center.dto.TeacherDTO;
import com.example.education_center.dto.UserDTO;
import com.example.education_center.dto.search.SearchLearnerDTO;
import com.example.education_center.dto.search.SearchTeacherDTO;
import com.example.education_center.entity.Teacher;
import com.example.education_center.entity.User;
import com.example.education_center.exception.NotFoundException;
import com.example.education_center.repos.TeacherRepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherService {
    @Autowired
    TeacherRepo teacherRepo;

    public TeacherDTO convert(Teacher teacher){
        return new ModelMapper().map(teacher, TeacherDTO.class);
    }

    @Transactional
    public void create(TeacherDTO teacherDTO){
        UserDTO userDTO = teacherDTO.getUser();
        //TODO
        //thieu doan set password bao mat
        //userDTO.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
        teacherDTO.setUser(userDTO);
        teacherRepo.save(new ModelMapper().map(teacherDTO, Teacher.class));
    }

    @Transactional
    public void update(TeacherDTO teacherDTO) throws NotFoundException {
        if(teacherRepo.findById(teacherDTO.getId()).isPresent()){
            UserDTO userDTO = teacherDTO.getUser();
            //TODO
            //thieu doan set password bao mat
            //userDTO.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
            teacherDTO.setUser(userDTO);
            teacherRepo.save(new ModelMapper().map(teacherDTO, Teacher.class));
        }else{
            throw new NotFoundException("No found teacher");
        }
    }

    @Transactional
    public void delete(int id){
        teacherRepo.deleteById(id);
    }

    public PageDTO<List<TeacherDTO>> search(SearchTeacherDTO searchTeacherDTO) {
        Sort sortBy = Sort.by("id").ascending(); //sap xep theo ten va tuoi (mac dinh)


        //sort theo yeu cau
        if (StringUtils.hasText(searchTeacherDTO.getSortedField())) { //check xem co empty khong
            sortBy = Sort.by(searchTeacherDTO.getSortedField());
        }
        if (searchTeacherDTO.getCurrentPage() == null) {
            searchTeacherDTO.setCurrentPage(0);
        }
        if (searchTeacherDTO.getSize() == null) {
            searchTeacherDTO.setSize(20);
        }

        //tao PageRequest de truyen vao Pageable
        PageRequest pageRequest = PageRequest.of(searchTeacherDTO.getCurrentPage(), searchTeacherDTO.getSize(), sortBy);
        Page<Teacher> page = teacherRepo.findAll(pageRequest);

        if (StringUtils.hasText(searchTeacherDTO.getKeyword())) {
            page = teacherRepo.searchByName("%" + searchTeacherDTO.getKeyword() + "%", pageRequest);
        } else if (StringUtils.hasText(searchTeacherDTO.getEmail())) {
            page = teacherRepo.searchByMail(searchTeacherDTO.getEmail(), pageRequest);
        } else if (StringUtils.hasText(searchTeacherDTO.getPhone())) {
            page = teacherRepo.searchByPhone(searchTeacherDTO.getPhone(), pageRequest);
        }
            PageDTO<List<TeacherDTO>> pageDTO = new PageDTO<>();
            pageDTO.setTotalPages(page.getTotalPages());
            pageDTO.setTotalElements(page.getTotalElements());
            pageDTO.setSize(page.getSize());
            //List<User> users = page.getContent();
            List<TeacherDTO> teacherDTOS = page.get().map(u -> convert(u)).collect(Collectors.toList());

            //T: List<UserDTO>
            pageDTO.setData(teacherDTOS);
            return pageDTO;
        }


    public TeacherDTO findById(int id) {
        return new ModelMapper().map(teacherRepo.findById(id), TeacherDTO.class);
    }
}
