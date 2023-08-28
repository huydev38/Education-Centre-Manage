package com.example.education_center.controller;
import com.example.education_center.dto.AddressDTO;
import com.example.education_center.dto.ResponseDTO;
import com.example.education_center.exception.NotFoundException;
import com.example.education_center.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/address")
@RestController
@Slf4j
public class AddressController {
    @Autowired
    AddressService addressService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseDTO<Void> newAddress(@RequestBody AddressDTO addressDTO){
        addressService.create(addressDTO);
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }

    public ResponseDTO<AddressDTO> updateAddress(@RequestBody AddressDTO addressDTO) throws NotFoundException {
        addressService.update(addressDTO);
        return ResponseDTO.<AddressDTO>builder().data(addressDTO).msg("Success").status(200).build();
    }

    public ResponseDTO<Void> deleteAddress(@RequestParam("id") int id){
        addressService.delete(id);
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }


}
