package com.skyconnect.demo.service;

import com.skyconnect.demo.dto.request.PassengerRequest;
import com.skyconnect.demo.dto.response.PassengerResponse;

import com.skyconnect.demo.entity.Passenger;

import com.skyconnect.demo.mapper.PassengerMapper;

import com.skyconnect.demo.repository.PassengerRepository;

import com.skyconnect.demo.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PassengerService {

    private final PassengerRepository passengerRepository;

    private final PassengerMapper passengerMapper;


    // ==========================================
    // CREATE PASSENGER
    // ==========================================

    public PassengerResponse createPassenger(
            PassengerRequest request
    ) {

        if (passengerRepository.existsByEmail(
                request.getEmail()
        )) {

            throw new IllegalArgumentException(
                    "Passenger with this email already exists"
            );
        }


        Passenger passenger =
                passengerMapper.toEntity(
                        request
                );


        Passenger savedPassenger =
                passengerRepository.save(
                        passenger
                );


        return passengerMapper.toResponse(
                savedPassenger
        );
    }


    // ==========================================
    // GET ALL PASSENGERS
    // ==========================================

    @Transactional(readOnly = true)
    public List<PassengerResponse> getAllPassengers() {

        return passengerRepository
                .findAll()
                .stream()
                .map(passengerMapper::toResponse)
                .toList();
    }


    // ==========================================
    // GET PASSENGER BY ID
    // ==========================================

    @Transactional(readOnly = true)
    public PassengerResponse getPassenger(
            Long id
    ) {

        Passenger passenger =
                passengerRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Passenger not found with id: "
                                                + id
                                )
                        );

        return passengerMapper.toResponse(
                passenger
        );
    }


    // ==========================================
    // GET PASSENGER BY EMAIL
    // ==========================================

    @Transactional(readOnly = true)
    public PassengerResponse getPassengerByEmail(
            String email
    ) {

        Passenger passenger =
                passengerRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Passenger not found with email: "
                                                + email
                                )
                        );

        return passengerMapper.toResponse(
                passenger
        );
    }


    // ==========================================
    // UPDATE PASSENGER
    // ==========================================

    public PassengerResponse updatePassenger(
            Long id,
            PassengerRequest request
    ) {

        Passenger passenger =
                passengerRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Passenger not found with id: "
                                                + id
                                )
                        );


        // Check if another passenger
        // already uses this email
        passengerRepository
                .findByEmail(request.getEmail())
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {

                        throw new IllegalArgumentException(
                                "Another passenger already uses this email"
                        );
                    }
                });


        passenger.setFirstName(
                request.getFirstName()
        );

        passenger.setLastName(
                request.getLastName()
        );

        passenger.setEmail(
                request.getEmail()
        );

        passenger.setPhone(
                request.getPhone()
        );

        passenger.setGender(
                request.getGender()
        );

        passenger.setDateOfBirth(
                request.getDateOfBirth()
        );

        passenger.setPassportNumber(
                request.getPassportNumber()
        );


        Passenger updatedPassenger =
                passengerRepository.save(
                        passenger
                );


        return passengerMapper.toResponse(
                updatedPassenger
        );
    }


    // ==========================================
    // DELETE PASSENGER
    // ==========================================

    public void deletePassenger(
            Long id
    ) {

        Passenger passenger =
                passengerRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Passenger not found with id: "
                                                + id
                                )
                        );

        passengerRepository.delete(
                passenger
        );
    }
}