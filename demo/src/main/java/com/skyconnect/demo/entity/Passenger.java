package com.skyconnect.demo.entity;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(
        name = "passengers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_passenger_email",
                        columnNames = "email"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            length = 100
    )
    private String firstName;

    @Column(
            nullable = false,
            length = 100
    )
    private String lastName;

    @Column(
            nullable = false,
            unique = true,
            length = 150
    )
    private String email;

    @Column(
            nullable = false,
            length = 15
    )
    private String phone;

    @Column(length = 20)
    private String gender;

    @Column(length = 20)
    private String dateOfBirth;

    @Column(length = 50)
    private String passportNumber;
}