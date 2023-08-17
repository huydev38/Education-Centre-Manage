package com.example.education_center.service;

import com.example.education_center.dto.AddressDTO;
import com.example.education_center.dto.PageDTO;
import com.example.education_center.dto.RoomDTO;
import com.example.education_center.dto.search.SearchAddressDTO;
import com.example.education_center.dto.search.SearchRoomDTO;
import com.example.education_center.entity.Address;
import com.example.education_center.entity.Room;
import com.example.education_center.repos.RoomRepo;
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
public class RoomService {
    @Autowired
    RoomRepo roomRepo;
    public RoomDTO convert(Room room){
        return new ModelMapper().map(room, RoomDTO.class);
    }

    @Transactional
    public void create(RoomDTO roomDTO){
        roomRepo.save(new ModelMapper().map(roomDTO,Room.class));
    }

    @Transactional
    public void update(RoomDTO roomDTO) {
        if (roomRepo.findById(roomDTO.getId()).isPresent()) {
            roomRepo.save(new ModelMapper().map(roomDTO, Room.class));
        }
    }

    @Transactional
    public void delete(int id){
        roomRepo.deleteById(id);
    }

    public PageDTO<List<RoomDTO>> search(SearchRoomDTO searchRoomDTO){
        Sort sortBy=Sort.by("id").ascending(); //sap xep theo ten va tuoi (mac dinh)

        //sort theo yeu cau
        if(StringUtils.hasText(searchRoomDTO.getSortedField())){ //check xem co empty khong
            sortBy=Sort.by(searchRoomDTO.getSortedField());
        }
        if(searchRoomDTO.getCurrentPage()==null){
            searchRoomDTO.setCurrentPage(0);
        }
        if(searchRoomDTO.getSize()==null){
            searchRoomDTO.setSize(20);
        }

        //tao PageRequest de truyen vao Pageable
        PageRequest pageRequest = PageRequest.of(searchRoomDTO.getCurrentPage(),searchRoomDTO.getSize(),sortBy);
        Page<Room> page = roomRepo.findAll(pageRequest);

        if(searchRoomDTO.getAddressDTO()==null&& StringUtils.hasText(searchRoomDTO.getKeyword())) {
            page = roomRepo.searchByLocation("%" + searchRoomDTO.getKeyword() + "%", pageRequest);
        } else if(searchRoomDTO.getAddressDTO()!=null && !StringUtils.hasText(searchRoomDTO.getKeyword())){
            page = roomRepo.searchByAddress(searchRoomDTO.getAddressDTO().getId(), pageRequest);
        }else if(searchRoomDTO.getAddressDTO()!=null && StringUtils.hasText(searchRoomDTO.getKeyword())){
            page = roomRepo.searchByCentreAndLocation(searchRoomDTO.getAddressDTO().getId(),"%"+ searchRoomDTO.getKeyword()+"%", pageRequest);
        }
        PageDTO<List<RoomDTO>> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(page.getTotalPages());
        pageDTO.setTotalElements(page.getTotalElements());
        pageDTO.setSize(page.getSize());
        //List<User> users = page.getContent();
        List<RoomDTO> roomDTOS = page.get().map(u->convert(u)).collect(Collectors.toList());

        //T: List<UserDTO>
        pageDTO.setData(roomDTOS);
        return pageDTO;

    }



}
