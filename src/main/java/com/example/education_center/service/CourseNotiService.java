package com.example.education_center.service;

import com.example.education_center.dto.*;
import com.example.education_center.dto.search.SearchNotiDTO;
import com.example.education_center.email.EmailService;
import com.example.education_center.entity.CourseNoti;
import com.example.education_center.repos.CourseNotiRepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseNotiService {

    public CourseNotiDTO convertNoti(CourseNoti courseNoti){
        return new ModelMapper().map(courseNoti, CourseNotiDTO.class);
    }
    @Autowired
    EmailService emailService;

    @Autowired
    CourseNotiRepo courseNotiRepo;

    @Transactional
    public void addNoti(CourseNotiDTO courseNotiDTO){
        List<LearnerDTO> learners = courseNotiDTO.getCourse().getLearners();

        for(LearnerDTO l: learners){
            UserDTO u = l.getUser();
            emailService.sendNotiEmail(u.getEmail(), courseNotiDTO.getMsg(), courseNotiDTO.getCourse().getName());
        }

        courseNotiRepo.save(new ModelMapper().map(courseNotiDTO, CourseNoti.class));
    }

    @Transactional
    public void deleteNoti(int id){
        courseNotiRepo.deleteById(id);
    }

    @Transactional
    public void updateNoti(CourseNotiDTO courseNotiDTO){
        courseNotiRepo.save(new ModelMapper().map(courseNotiDTO, CourseNoti.class));
    }

    public PageDTO<List<CourseNotiDTO>> searchNoti(SearchNotiDTO searchNotiDTO){
        Sort sortBy=Sort.by("id").ascending(); //sap xep theo ten va tuoi (mac dinh)


        //sort theo yeu cau
        if(StringUtils.hasText(searchNotiDTO.getSortedField())){ //check xem co empty khong
            sortBy=Sort.by(searchNotiDTO.getSortedField());
        }
        if(searchNotiDTO.getCurrentPage()==null){
            searchNotiDTO.setCurrentPage(0);
        }
        if(searchNotiDTO.getSize()==null){
            searchNotiDTO.setSize(20);
        }

        //tao PageRequest de truyen vao Pageable
        PageRequest pageRequest = PageRequest.of(searchNotiDTO.getCurrentPage(),searchNotiDTO.getSize(),sortBy);
        Page<CourseNoti> page = courseNotiRepo.findAll(pageRequest);

        if(searchNotiDTO.getCourseDTO()!=null&&searchNotiDTO.getStart_date()==null&&searchNotiDTO.getEnd_date()==null){
            page = courseNotiRepo.searchByCourse(searchNotiDTO.getCourseDTO().getId(), pageRequest);
        }else if(searchNotiDTO.getCourseDTO()!=null&&searchNotiDTO.getStart_date()!=null&&searchNotiDTO.getEnd_date()!=null){
            page = courseNotiRepo.searchByCourseAndDate(searchNotiDTO.getCourseDTO().getId(),searchNotiDTO.getStart_date(), searchNotiDTO.getEnd_date(), pageRequest);
        }
        PageDTO<List<CourseNotiDTO>> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(page.getTotalPages());
        pageDTO.setTotalElements(page.getTotalElements());
        pageDTO.setSize(page.getSize());
        //List<User> users = page.getContent();
        List<CourseNotiDTO> courseDTOS = page.get().map(u->convertNoti(u)).collect(Collectors.toList());

        //T: List<UserDTO>
        pageDTO.setData(courseDTOS);
        return pageDTO;
    }

    public PageDTO<List<CourseNotiDTO>> searchNotiByLearner(SearchNotiDTO searchNotiDTO, List<CourseDTO> courseDTOList){
        Sort sortBy=Sort.by("createdAt").descending(); //sap xep theo ten va tuoi (mac dinh)
        List<Integer> course_ids = new ArrayList<>();
        for(CourseDTO c:courseDTOList){
            course_ids.add(c.getId());
        }

        //sort theo yeu cau
        if(StringUtils.hasText(searchNotiDTO.getSortedField())){ //check xem co empty khong
            sortBy=Sort.by(searchNotiDTO.getSortedField());
        }
        if(searchNotiDTO.getCurrentPage()==null){
            searchNotiDTO.setCurrentPage(0);
        }
        if(searchNotiDTO.getSize()==null){
            searchNotiDTO.setSize(20);
        }

        //tao PageRequest de truyen vao Pageable
        PageRequest pageRequest = PageRequest.of(searchNotiDTO.getCurrentPage(),searchNotiDTO.getSize(),sortBy);
        Page<CourseNoti> page = courseNotiRepo.searchByUser(course_ids,pageRequest);

        if(searchNotiDTO.getStart_date()!=null&&searchNotiDTO.getEnd_date()!=null){
            page = courseNotiRepo.searchByUserAndDate(course_ids,searchNotiDTO.getStart_date(), searchNotiDTO.getEnd_date(), pageRequest);
        }
        PageDTO<List<CourseNotiDTO>> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(page.getTotalPages());
        pageDTO.setTotalElements(page.getTotalElements());
        pageDTO.setSize(page.getSize());
        //List<User> users = page.getContent();
        List<CourseNotiDTO> courseDTOS = page.get().map(u->convertNoti(u)).collect(Collectors.toList());

        //T: List<UserDTO>
        pageDTO.setData(courseDTOS);
        return pageDTO;
    }

    public List<CourseNotiDTO> getCourseNoti(CourseDTO courseDTO){
        return courseNotiRepo.findByCourse(courseDTO.getId()).stream().map(this::convertNoti).collect(Collectors.toList());
    }

    public CourseNotiDTO findByNotiId(int id) {
        return new ModelMapper().map(courseNotiRepo.findById(id), CourseNotiDTO.class);
    }
}
