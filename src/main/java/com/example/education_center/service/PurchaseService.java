package com.example.education_center.service;
import com.example.education_center.dto.AddressDTO;
import com.example.education_center.dto.PageDTO;
import com.example.education_center.dto.PurchaseDTO;
import com.example.education_center.dto.UserDTO;
import com.example.education_center.dto.search.SearchAddressDTO;
import com.example.education_center.dto.search.SearchDTO;
import com.example.education_center.dto.search.SearchPurchaseDTO;
import com.example.education_center.entity.Address;
import com.example.education_center.entity.Purchase;
import com.example.education_center.entity.User;
import com.example.education_center.repos.PurchaseRepo;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Service
public class PurchaseService {
    @Autowired
    PurchaseRepo purchaseRepo;

    public PurchaseDTO convert(Purchase purchase){
        return new ModelMapper().map(purchase, PurchaseDTO.class);
    }
    @Transactional
    public void create(PurchaseDTO purchaseDTO){
        purchaseRepo.save(new ModelMapper().map(purchaseDTO, Purchase.class));
    }

    @Transactional
    public void delete(int id){
        purchaseRepo.deleteById(id);
    }

    public PageDTO<List<PurchaseDTO>> search(SearchPurchaseDTO searchPurchaseDTO){
        Sort sortBy=Sort.by("id").ascending(); //sap xep theo ten va tuoi (mac dinh)

        //sort theo yeu cau
        if(StringUtils.hasText(searchPurchaseDTO.getSortedField())){ //check xem co empty khong
            sortBy=Sort.by(searchPurchaseDTO.getSortedField());
        }
        if(searchPurchaseDTO.getCurrentPage()==null){
            searchPurchaseDTO.setCurrentPage(0);
        }
        if(searchPurchaseDTO.getSize()==null){
            searchPurchaseDTO.setSize(20);
        }

        //tao PageRequest de truyen vao Pageable
        PageRequest pageRequest = PageRequest.of(searchPurchaseDTO.getCurrentPage(),searchPurchaseDTO.getSize(),sortBy);
        Page<Purchase> page = purchaseRepo.findAll(pageRequest);

        if(searchPurchaseDTO.getUserDTO()==null
                &&searchPurchaseDTO.getStart_date()==null
                &&searchPurchaseDTO.getEnd_date()==null) {
            page = purchaseRepo.searchByUser(searchPurchaseDTO.getUserDTO().getId(), pageRequest);
        }else if(searchPurchaseDTO.getUserDTO()==null
                &&searchPurchaseDTO.getStart_date()!=null
                &&searchPurchaseDTO.getEnd_date()!=null){
            page = purchaseRepo.searchByDate(searchPurchaseDTO.getStart_date(),searchPurchaseDTO.getEnd_date(), pageRequest);

        }else if(searchPurchaseDTO.getUserDTO()==null
                &&searchPurchaseDTO.getStart_date()==null
                &&searchPurchaseDTO.getEnd_date()!=null){
            page = purchaseRepo.searchByEndDate(searchPurchaseDTO.getEnd_date(), pageRequest);
        }else if(searchPurchaseDTO.getUserDTO()==null
                &&searchPurchaseDTO.getStart_date()!=null
                &&searchPurchaseDTO.getEnd_date()==null){
            page = purchaseRepo.searchByStartDate(searchPurchaseDTO.getStart_date(), pageRequest);
        }
        PageDTO<List<PurchaseDTO>> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(page.getTotalPages());
        pageDTO.setTotalElements(page.getTotalElements());
        pageDTO.setSize(page.getSize());
        //List<User> users = page.getContent();
        List<PurchaseDTO> purchaseDTOS = page.get().map(u->convert(u)).collect(Collectors.toList());

        //T: List<UserDTO>
        pageDTO.setData(purchaseDTOS);
        return pageDTO;

    }
}
