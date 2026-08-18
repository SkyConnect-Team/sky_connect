package com.skyconnect.demo.controller;

import com.skyconnect.demo.dto.request.UpdateCustomerRequest;
import com.skyconnect.demo.dto.response.BookingResponse;
import com.skyconnect.demo.dto.response.CustomerResponse;
import com.skyconnect.demo.service.AdminService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;


    // =====================================================
    // GET ALL CUSTOMERS
    // =====================================================

    @GetMapping("/users")
    public ResponseEntity<List<CustomerResponse>>
    getAllCustomers() {

        return ResponseEntity.ok(
                adminService.getAllCustomers()
        );
    }


    // =====================================================
    // SEARCH CUSTOMERS
    // =====================================================

    @GetMapping("/users/search")
    public ResponseEntity<List<CustomerResponse>>
    searchCustomers(
            @RequestParam String keyword
    ) {

        return ResponseEntity.ok(
                adminService.searchCustomers(
                        keyword
                )
        );
    }


    // =====================================================
    // GET CUSTOMER BY ID
    // =====================================================

    @GetMapping("/users/{id}")
    public ResponseEntity<CustomerResponse>
    getCustomer(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                adminService.getCustomer(id)
        );
    }


    // =====================================================
    // UPDATE CUSTOMER
    // =====================================================

    @PutMapping("/users/{id}")
    public ResponseEntity<CustomerResponse>
    updateCustomer(

            @PathVariable Long id,

            @Valid
            @RequestBody
            UpdateCustomerRequest request

    ) {

        return ResponseEntity.ok(
                adminService.updateCustomer(
                        id,
                        request
                )
        );
    }


    // =====================================================
    // DELETE CUSTOMER
    // =====================================================

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String>
    deleteCustomer(
            @PathVariable Long id
    ) {

        adminService.deleteCustomer(id);

        return ResponseEntity.ok(
                "Customer deleted successfully"
        );
    }


    // =====================================================
    // GET CUSTOMER BOOKINGS
    // =====================================================

    @GetMapping("/users/{id}/bookings")
    public ResponseEntity<List<BookingResponse>>
    getCustomerBookings(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                adminService.getCustomerBookings(id)
        );
    }
}