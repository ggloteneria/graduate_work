package com.example.diplom.dto;

import com.example.diplom.entities.credit.Credit;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreditCardDTO {

    private Long id;

    private String fullNumber;

    private double balance;

    private double percent;

    private double limitOfCard;

    private LocalDate validityPeriod;

    private List<Credit> credits;

}
