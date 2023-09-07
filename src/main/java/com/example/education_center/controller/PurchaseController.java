package com.example.education_center.controller;

import com.example.education_center.dto.AddressDTO;
import com.example.education_center.dto.PageDTO;
import com.example.education_center.dto.PurchaseDTO;
import com.example.education_center.dto.ResponseDTO;
import com.example.education_center.dto.search.SearchPurchaseDTO;
import com.example.education_center.service.PurchaseService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {
    @Autowired
    PurchaseService purchaseService;


    @PostMapping("/")
    public ResponseDTO<Void> newPurchase(@RequestBody PurchaseDTO purchaseDTO){
        purchaseService.create(purchaseDTO);
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }

    @DeleteMapping("/")
    public ResponseDTO<Void> deletePurchase(@RequestParam("id") int id){
        purchaseService.delete(id);
        return ResponseDTO.<Void>builder().msg("Success").status(200).build();
    }

    @GetMapping("/search")
    public ResponseDTO<PageDTO<List<PurchaseDTO>>> searchPurchase(@RequestBody SearchPurchaseDTO searchPurchaseDTO){
        return ResponseDTO.<PageDTO<List<PurchaseDTO>>>builder().data(purchaseService.search(searchPurchaseDTO)).msg("Success").status(200).build();

    }

    @GetMapping("/{id}")
    public ResponseDTO<PurchaseDTO> getPurchase(@PathVariable("id") int id){
        return ResponseDTO.<PurchaseDTO>builder().data(purchaseService.findById(id)).build();
    }
}
