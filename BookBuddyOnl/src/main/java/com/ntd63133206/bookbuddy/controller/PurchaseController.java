package com.ntd63133206.bookbuddy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ntd63133206.bookbuddy.model.Purchase;
import com.ntd63133206.bookbuddy.service.PurchaseService;


@RequestMapping("/admin/purchases")
public class PurchaseController {
	
    @Autowired
    private PurchaseService purchaseService;
    
	@GetMapping({"", "/", "/index"})
    public String viewPurchases(Model model) {
        List<Purchase> purchases = purchaseService.getAllPurchases();
        model.addAttribute("purchases", purchases);
        return "admin/purchases/purchase-list";
    }

    @PostMapping("/{purchaseId}/edit")
    public String editPurchase(@PathVariable Long purchaseId, @RequestParam("status") String status) {
        purchaseService.updatePurchaseStatus(purchaseId, status);
        return "redirect:/admin/purchases/";
    }

}
