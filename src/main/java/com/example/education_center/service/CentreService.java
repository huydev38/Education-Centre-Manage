package com.example.education_center.service;

import com.example.education_center.dto.AddressDTO;
import com.example.education_center.dto.CentreDTO;
import com.example.education_center.dto.PageDTO;
import com.example.education_center.dto.search.SearchAddressDTO;
import com.example.education_center.dto.search.SearchDTO;
import com.example.education_center.entity.Address;
import com.example.education_center.entity.Centre;
import com.example.education_center.exception.NotFoundException;
import com.example.education_center.repos.CentreRepo;
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
public class CentreService {
    @Autowired
    CentreRepo centreRepo;

    public CentreDTO convert(Centre centre){
        return new ModelMapper().map(centre, CentreDTO.class);
    }

    @Transactional
    public void create(CentreDTO centreDTO){
        for(AddressDTO a: centreDTO.getAddressList()){
            a.setCentre(centreDTO);
        }
        centreRepo.save(new ModelMapper().map(centreDTO, Centre.class));
    }

    @Transactional
    public void delete(int id){
        centreRepo.deleteById(id);
    }

    @Transactional
    public CentreDTO update(CentreDTO centreDTO) throws NotFoundException {
        if(centreRepo.findById(centreDTO.getId()).isPresent()){
            for(AddressDTO a: centreDTO.getAddressList()){
                a.setCentre(centreDTO);
            }
            centreRepo.save(new ModelMapper().map(centreDTO, Centre.class));
        }else{
            throw new NotFoundException("Not found centre");
        }
        return centreDTO;
    }

    public List<CentreDTO> getAll(){
        return centreRepo.findAll().stream().map(c->convert(c)).collect(Collectors.toList());
    }

    public PageDTO<List<CentreDTO>> search(SearchDTO searchDTO){
        Sort sortBy=Sort.by("id").ascending(); //sap xep theo ten va tuoi (mac dinh)

        //sort theo yeu cau
        if(StringUtils.hasText(searchDTO.getSortedField())){ //check xem co empty khong
            sortBy=Sort.by(searchDTO.getSortedField());
        }
        if(searchDTO.getCurrentPage()==null){
            searchDTO.setCurrentPage(0);
        }
        if(searchDTO.getSize()==null){
            searchDTO.setSize(20);
        }

        //tao PageRequest de truyen vao Pageable
        PageRequest pageRequest = PageRequest.of(searchDTO.getCurrentPage(),searchDTO.getSize(),sortBy);
        Page<Centre> page = centreRepo.findAll(pageRequest);

        if(StringUtils.hasText(searchDTO.getKeyword())) {
            page = centreRepo.searchByName("%" + searchDTO.getKeyword() + "%", pageRequest);
        }
        PageDTO<List<CentreDTO>> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(page.getTotalPages());
        pageDTO.setTotalElements(page.getTotalElements());
        pageDTO.setSize(page.getSize());
        List<CentreDTO> centreDTOS = page.get().map(u->convert(u)).collect(Collectors.toList());

        pageDTO.setData(centreDTOS);
        return pageDTO;

    }
}
