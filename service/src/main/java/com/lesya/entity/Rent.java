package com.lesya.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rent {

    private Integer id;
    private LocalDate dateStart;
    private LocalDate terminationCarRental;
    private Integer carId;
    private RequestStatus requestStatus;
    private Integer userId;
    private String passport;
    private Integer drivingExperience;
    private String message;
}