package com.skyconnect.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PassengerResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String gender;

    private String dateOfBirth;

    private String passportNumber;
}