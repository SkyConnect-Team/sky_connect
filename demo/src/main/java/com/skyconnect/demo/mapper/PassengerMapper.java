package com.skyconnect.demo.mapper;

import com.skyconnect.demo.dto.request.PassengerRequest;
import com.skyconnect.demo.dto.response.PassengerResponse;
import com.skyconnect.demo.entity.Passenger;

import org.springframework.stereotype.Component;

@Component
public class PassengerMapper {

    public Passenger toEntity(
            PassengerRequest request
    ) {

        return Passenger.builder()
                .firstName(
                        request.getFirstName()
                )
                .lastName(
                        request.getLastName()
                )
                .email(
                        request.getEmail()
                )
                .phone(
                        request.getPhone()
                )
                .gender(
                        request.getGender()
                )
                .dateOfBirth(
                        request.getDateOfBirth()
                )
                .passportNumber(
                        request.getPassportNumber()
                )
                .build();
    }


    public PassengerResponse toResponse(
            Passenger passenger
    ) {

        return PassengerResponse.builder()
                .id(passenger.getId())
                .firstName(
                        passenger.getFirstName()
                )
                .lastName(
                        passenger.getLastName()
                )
                .email(
                        passenger.getEmail()
                )
                .phone(
                        passenger.getPhone()
                )
                .gender(
                        passenger.getGender()
                )
                .dateOfBirth(
                        passenger.getDateOfBirth()
                )
                .passportNumber(
                        passenger.getPassportNumber()
                )
                .build();
    }
}
