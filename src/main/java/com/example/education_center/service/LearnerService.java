package com.example.education_center.service;

import com.example.education_center.dto.LearnerDTO;
import com.example.education_center.dto.PageDTO;
import com.example.education_center.dto.UserDTO;
import com.example.education_center.dto.search.SearchLearnerDTO;
import com.example.education_center.entity.Learner;
import com.example.education_center.exception.NotFoundException;
import com.example.education_center.repos.LearnerRepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LearnerService {
    @Autowired
    LearnerRepo learnerRepo;

    @Autowired
    CourseService courseService;



    public LearnerDTO convert(Learner learner){
        return new ModelMapper().map(learner, LearnerDTO.class);
    }

    @Transactional
    public void create(LearnerDTO learnerDTO){
        UserDTO userDTO = learnerDTO.getUser();
        userDTO.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
        userDTO.setRole("ROLE_LEARNER");
        learnerDTO.setUser(userDTO);
        learnerRepo.save(new ModelMapper().map(learnerDTO, Learner.class));
    }

    @Transactional
    public void update(LearnerDTO learnerDTO) throws NotFoundException {
        if(learnerRepo.findById(learnerDTO.getId()).isPresent()){
            UserDTO userDTO = learnerDTO.getUser();
            userDTO.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
            userDTO.setRole("ROLE_LEARNER");
            learnerDTO.setUser(userDTO);
            learnerRepo.save(new ModelMapper().map(learnerDTO, Learner.class));
        }else{
            throw new NotFoundException("Not found Learner");
        }
    }



    @Transactional
    public void delete(int id){
        learnerRepo.deleteById(id);
    }

    public PageDTO<List<LearnerDTO>> search(SearchLearnerDTO searchLearnerDTO){
        Sort sortBy=Sort.by("id").ascending(); //sap xep theo ten va tuoi (mac dinh)


        //sort theo yeu cau
        if(StringUtils.hasText(searchLearnerDTO.getSortedField())){ //check xem co empty khong
            sortBy=Sort.by(searchLearnerDTO.getSortedField());
        }
        if(searchLearnerDTO.getCurrentPage()==null){
            searchLearnerDTO.setCurrentPage(0);
        }
        if(searchLearnerDTO.getSize()==null){
            searchLearnerDTO.setSize(20);
        }

        //tao PageRequest de truyen vao Pageable
            PageRequest pageRequest = PageRequest.of(searchLearnerDTO.getCurrentPage(),searchLearnerDTO.getSize(),sortBy);
        Page<Learner> page = learnerRepo.findAll(pageRequest);

        if(StringUtils.hasText(searchLearnerDTO.getKeyword())){
            page = learnerRepo.searchByName("%"+ searchLearnerDTO.getKeyword()+"%", pageRequest);
        }else if(StringUtils.hasText(searchLearnerDTO.getPhone())){
            page = learnerRepo.searchByPhone(searchLearnerDTO.getPhone(), pageRequest);
        }else if(StringUtils.hasText(searchLearnerDTO.getEmail())){
            page = learnerRepo.seachByEmail(searchLearnerDTO.getEmail(), pageRequest);
        }
        PageDTO<List<LearnerDTO>> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(page.getTotalPages());
        pageDTO.setTotalElements(page.getTotalElements());
        pageDTO.setSize(page.getSize());
        //List<User> users = page.getContent();
        List<LearnerDTO> learnerDTOS = page.get().map(u->convert(u)).collect(Collectors.toList());

        //T: List<UserDTO>
        pageDTO.setData(learnerDTOS);
        return pageDTO;
    }

    public LearnerDTO findById(int id) {
        return new ModelMapper().map(learnerRepo.findById(id), LearnerDTO.class);
    }

    public LearnerDTO findByUserId(int id){
        return new ModelMapper().map(learnerRepo.findByUserId(id), LearnerDTO.class);

    }


}
