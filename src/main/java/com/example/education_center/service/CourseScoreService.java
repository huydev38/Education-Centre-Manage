package com.example.education_center.service;

import com.example.education_center.dto.CourseScoreDTO;
import com.example.education_center.dto.PageDTO;
import com.example.education_center.dto.search.SearchScoreDTO;
import com.example.education_center.entity.CourseScore;
import com.example.education_center.repos.CourseScoreRepo;
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
public class CourseScoreService {

    @Autowired
    CourseScoreRepo courseScoreRepo;


    public CourseScoreDTO convertScore(CourseScore courseScore){
        return new ModelMapper().map(courseScore, CourseScoreDTO.class);
    }



    @Transactional
    public void addCourseScore(List<CourseScoreDTO> courseScoreDTO){
        for(CourseScoreDTO c: courseScoreDTO){
            courseScoreRepo.save(new ModelMapper().map(c, CourseScore.class));
        }
    }

    @Transactional
    public void updateCourseScore(CourseScoreDTO courseScoreDTO){
        if(courseScoreRepo.findById(courseScoreDTO.getId()).isPresent()){
            courseScoreRepo.save(new ModelMapper().map(courseScoreDTO, CourseScore.class));
        }
    }

    @Transactional
    public void deteleCourseScore(int id){
        courseScoreRepo.deleteById(id);
    }

    public PageDTO<List<CourseScoreDTO>> searchScore(SearchScoreDTO searchScoreDTO) {
        Sort sortBy = Sort.by("id").ascending(); //sap xep theo ten va tuoi (mac dinh)


        //sort theo yeu cau
        if (StringUtils.hasText(searchScoreDTO.getSortedField())) { //check xem co empty khong
            sortBy = Sort.by(searchScoreDTO.getSortedField());
        }
        if (searchScoreDTO.getCurrentPage() == null) {
            searchScoreDTO.setCurrentPage(0);
        }
        if (searchScoreDTO.getSize() == null) {
            searchScoreDTO.setSize(20);
        }

        //tao PageRequest de truyen vao Pageable
        PageRequest pageRequest = PageRequest.of(searchScoreDTO.getCurrentPage(), searchScoreDTO.getSize(), sortBy);
        Page<CourseScore> page = courseScoreRepo.findAll(pageRequest);

        if(searchScoreDTO.getCourseDTO() != null && searchScoreDTO.getStart_date() != null && searchScoreDTO.getEnd_date() != null && searchScoreDTO.getLearnerDTO() == null) {
            page = courseScoreRepo.searchByCourseAndDate(searchScoreDTO.getCourseDTO().getId(), searchScoreDTO.getStart_date(), searchScoreDTO.getEnd_date(), pageRequest);
        } else if (searchScoreDTO.getCourseDTO() == null && searchScoreDTO.getStart_date() != null && searchScoreDTO.getEnd_date() != null && searchScoreDTO.getLearnerDTO() != null) {
            page = courseScoreRepo.searchByLearnerAndDate(searchScoreDTO.getLearnerDTO().getId(), searchScoreDTO.getStart_date(), searchScoreDTO.getEnd_date(), pageRequest);
        }else if(searchScoreDTO.getCourseDTO() != null && searchScoreDTO.getLearnerDTO() != null){
            page = courseScoreRepo.searchByLearnerAndCourse(searchScoreDTO.getLearnerDTO().getId(),searchScoreDTO.getCourseDTO().getId(), pageRequest);
        }else if (searchScoreDTO.getCourseDTO() != null) {
            page = courseScoreRepo.searchByCourse(searchScoreDTO.getCourseDTO().getId(), pageRequest);
        }else if (searchScoreDTO.getLearnerDTO() != null) {
            page = courseScoreRepo.searchByLearner(searchScoreDTO.getLearnerDTO().getId(), pageRequest);
        }
        PageDTO<List<CourseScoreDTO>> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(page.getTotalPages());
        pageDTO.setTotalElements(page.getTotalElements());
        pageDTO.setSize(page.getSize());
        //List<User> users = page.getContent();
        List<CourseScoreDTO> courseDTOS = page.get().map(u -> convertScore(u)).collect(Collectors.toList());

        //T: List<UserDTO>
        pageDTO.setData(courseDTOS);
        return pageDTO;
    }


    public PageDTO<List<CourseScoreDTO>> searchScoreByUser(SearchScoreDTO searchScoreDTO) {
        Sort sortBy = Sort.by("id").ascending(); //sap xep theo ten va tuoi (mac dinh)


        //sort theo yeu cau
        if (StringUtils.hasText(searchScoreDTO.getSortedField())) { //check xem co empty khong
            sortBy = Sort.by(searchScoreDTO.getSortedField());
        }
        if (searchScoreDTO.getCurrentPage() == null) {
            searchScoreDTO.setCurrentPage(0);
        }
        if (searchScoreDTO.getSize() == null) {
            searchScoreDTO.setSize(20);
        }

        //tao PageRequest de truyen vao Pageable
        PageRequest pageRequest = PageRequest.of(searchScoreDTO.getCurrentPage(), searchScoreDTO.getSize(), sortBy);
        Page<CourseScore> page = courseScoreRepo.findAll(pageRequest);

        if(searchScoreDTO.getStart_date() != null && searchScoreDTO.getEnd_date() != null && searchScoreDTO.getLearnerDTO() != null) {
            page = courseScoreRepo.searchByLearnerAndDate(searchScoreDTO.getCourseDTO().getId(), searchScoreDTO.getStart_date(), searchScoreDTO.getEnd_date(), pageRequest);
        }else{
            page = courseScoreRepo.searchByLearner(searchScoreDTO.getLearnerDTO().getId(), pageRequest);
        }
        PageDTO<List<CourseScoreDTO>> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(page.getTotalPages());
        pageDTO.setTotalElements(page.getTotalElements());
        pageDTO.setSize(page.getSize());
        //List<User> users = page.getContent();
        List<CourseScoreDTO> courseDTOS = page.get().map(u -> convertScore(u)).collect(Collectors.toList());

        //T: List<UserDTO>
        pageDTO.setData(courseDTOS);
        return pageDTO;
    }

    public Double getGPA(SearchScoreDTO searchScoreDTO){
        double gpa = 0;
        gpa = courseScoreRepo.avgScore(searchScoreDTO.getCourseDTO().getId(), searchScoreDTO.getLearnerDTO().getId());
        return gpa;
    }

    public CourseScoreDTO findByScoreId(int id) {
        return new ModelMapper().map(courseScoreRepo.findById(id), CourseScoreDTO.class);
    }
}
