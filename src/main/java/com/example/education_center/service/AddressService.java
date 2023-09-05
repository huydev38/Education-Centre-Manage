package com.example.education_center.service;

import com.example.education_center.dto.AddressDTO;
import com.example.education_center.dto.PageDTO;
import com.example.education_center.dto.PurchaseDTO;
import com.example.education_center.dto.search.SearchAddressDTO;
import com.example.education_center.entity.Address;
import com.example.education_center.exception.NotFoundException;
import com.example.education_center.repos.AddressRepo;
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
public class AddressService {
    @Autowired
    AddressRepo addressRepo;

    public AddressDTO convert(Address address){
        return new ModelMapper().map(address, AddressDTO.class);
    }

    @Transactional
    public void create(AddressDTO addressDTO){
        addressRepo.save(new ModelMapper().map(addressDTO, Address.class));
    }

    @Transactional
    public void delete(int id){
        addressRepo.deleteById(id);
    }

    @Transactional
    public AddressDTO update(AddressDTO addressDTO) throws NotFoundException {
        if(addressRepo.findById(addressDTO.getId()).isPresent()){
            addressRepo.save(new ModelMapper().map(addressDTO, Address.class));
        }else{
            throw new NotFoundException("Not found Address");
        }
        return addressDTO;
    }

    public PageDTO<List<AddressDTO>> search(SearchAddressDTO searchAddressDTO){
        Sort sortBy=Sort.by("id").ascending(); //sap xep theo ten va tuoi (mac dinh)

        //sort theo yeu cau
        if(StringUtils.hasText(searchAddressDTO.getSortedField())){ //check xem co empty khong
            sortBy=Sort.by(searchAddressDTO.getSortedField());
        }
        if(searchAddressDTO.getCurrentPage()==null){
            searchAddressDTO.setCurrentPage(0);
        }
        if(searchAddressDTO.getSize()==null){
            searchAddressDTO.setSize(20);
        }

        //tao PageRequest de truyen vao Pageable
        PageRequest pageRequest = PageRequest.of(searchAddressDTO.getCurrentPage(),searchAddressDTO.getSize(),sortBy);
        Page<Address> page = addressRepo.findAll(pageRequest);

        if(searchAddressDTO.getCentreDTO()==null&& StringUtils.hasText(searchAddressDTO.getKeyword())) {
             page = addressRepo.searchByLocation("%" + searchAddressDTO.getKeyword() + "%", pageRequest);
        } else if(searchAddressDTO.getCentreDTO()!=null && !StringUtils.hasText(searchAddressDTO.getKeyword())){
            page = addressRepo.searchByCentre(searchAddressDTO.getCentreDTO().getId(), pageRequest);
        }else if(searchAddressDTO.getCentreDTO()!=null && StringUtils.hasText(searchAddressDTO.getKeyword())){
            page = addressRepo.searchByCentreAndLocation(searchAddressDTO.getCentreDTO().getId(),"%"+ searchAddressDTO.getKeyword()+"%", pageRequest);
        }
        PageDTO<List<AddressDTO>> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(page.getTotalPages());
        pageDTO.setTotalElements(page.getTotalElements());
        pageDTO.setSize(page.getSize());
        //List<User> users = page.getContent();
        List<AddressDTO> addressDTOS = page.get().map(u->convert(u)).collect(Collectors.toList());

        //T: List<UserDTO>
        pageDTO.setData(addressDTOS);
        return pageDTO;

    }

    public AddressDTO findById(int id) {
        return new ModelMapper().map(addressRepo.findById(id), AddressDTO.class);
    }
}
