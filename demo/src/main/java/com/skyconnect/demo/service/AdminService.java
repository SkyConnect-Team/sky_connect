package com.skyconnect.demo.service;

import com.skyconnect.demo.dto.request.UpdateCustomerRequest;
import com.skyconnect.demo.dto.response.BookingResponse;
import com.skyconnect.demo.dto.response.CustomerResponse;
import com.skyconnect.demo.entity.Booking;
import com.skyconnect.demo.entity.User;
import com.skyconnect.demo.enums.Role;
import com.skyconnect.demo.mapper.BookingMapper;
import com.skyconnect.demo.repository.BookingRepository;
import com.skyconnect.demo.repository.UserRepository;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;


    // =====================================================
    // GET ALL CUSTOMERS
    // =====================================================

    @Transactional
    public List<CustomerResponse> getAllCustomers() {

        return userRepository
                .findByRole(Role.CUSTOMER)
                .stream()
                .map(this::toCustomerResponse)
                .toList();
    }


    // =====================================================
    // SEARCH CUSTOMERS
    // =====================================================

    @Transactional
    public List<CustomerResponse> searchCustomers(
            String keyword
    ) {

        if (keyword == null || keyword.trim().isEmpty()) {

            return getAllCustomers();
        }

        return userRepository
                .searchCustomers(
                        Role.CUSTOMER,
                        keyword.trim()
                )
                .stream()
                .map(this::toCustomerResponse)
                .toList();
    }


    // =====================================================
    // GET CUSTOMER BY ID
    // =====================================================

    @Transactional
    public CustomerResponse getCustomer(
            Long id
    ) {

        User user = userRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found with id: " + id
                        )
                );

        // Make sure admin cannot access another admin
        if (user.getRole() != Role.CUSTOMER) {

            throw new RuntimeException(
                    "User is not a customer"
            );
        }

        return toCustomerResponse(user);
    }


    // =====================================================
    // UPDATE CUSTOMER
    // =====================================================

    @Transactional
    public CustomerResponse updateCustomer(
            Long id,
            UpdateCustomerRequest request
    ) {

        User user = userRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found with id: " + id
                        )
                );

        // Only CUSTOMER can be updated
        if (user.getRole() != Role.CUSTOMER) {

            throw new RuntimeException(
                    "Only customers can be updated"
            );
        }


        // -------------------------------------------------
        // Check email
        // -------------------------------------------------

        userRepository
                .findByEmail(request.getEmail())
                .ifPresent(existingUser -> {

                    if (!existingUser.getId().equals(id)) {

                        throw new IllegalStateException(
                                "Email already registered"
                        );
                    }
                });


        // -------------------------------------------------
        // Update fields
        // -------------------------------------------------

        user.setName(request.getName());

        user.setEmail(request.getEmail());

        user.setPhone(request.getPhone());


        // -------------------------------------------------
        // Save
        // -------------------------------------------------

        User updatedUser =
                userRepository.save(user);


        return toCustomerResponse(updatedUser);
    }


    // =====================================================
    // DELETE CUSTOMER
    // =====================================================

    @Transactional
    public void deleteCustomer(
            Long id
    ) {

        User user = userRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found with id: " + id
                        )
                );


        // Never allow admin to delete another admin
        if (user.getRole() != Role.CUSTOMER) {

            throw new RuntimeException(
                    "Only customers can be deleted"
            );
        }


        userRepository.delete(user);
    }


    // =====================================================
    // GET CUSTOMER BOOKINGS
    // =====================================================

    @Transactional
    public List<BookingResponse> getCustomerBookings(
            Long userId
    ) {

        // -------------------------------------------------
        // Find customer
        // -------------------------------------------------

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found with id: "
                                        + userId
                        )
                );


        // -------------------------------------------------
        // Check role
        // -------------------------------------------------

        if (user.getRole() != Role.CUSTOMER) {

            throw new RuntimeException(
                    "User is not a customer"
            );
        }


        // -------------------------------------------------
        // Find bookings using customer email
        //
        // Your existing BookingRepository already has:
        //
        // findByPassenger_Email(String email)
        // -------------------------------------------------

        List<Booking> bookings =
                bookingRepository
                        .findByPassenger_Email(
                                user.getEmail()
                        );


        // -------------------------------------------------
        // Convert Booking -> BookingResponse
        // -------------------------------------------------

        return bookings
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }


    // =====================================================
    // CUSTOMER RESPONSE MAPPER
    // =====================================================

    private CustomerResponse toCustomerResponse(
            User user
    ) {

        return CustomerResponse.builder()

                .id(user.getId())

                .name(user.getName())

                .email(user.getEmail())

                .phone(user.getPhone())

                .role(user.getRole().name())

                .build();
    }
}