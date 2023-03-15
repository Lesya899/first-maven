package com.lesya.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Car {

    private Integer id;
    private String brandName;
    private Integer carYear;
    private String color;
    private String image;
    private Model model;
    private Integer rentalPrice;
    private RentalStatus status;
}