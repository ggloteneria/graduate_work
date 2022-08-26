package com.example.diplom.dto;

import com.example.diplom.entities.CreditCard;
import com.example.diplom.entities.credit.CreditCurrency;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

@Data
public class CreditDTO {

    private Long id;

    private double initialSumOfCredit;

    private double sumOfCredit;

    private double penaltiesSpentOnRepayment;

    @Enumerated(EnumType.STRING)
    private CreditCurrency currency;

    private LocalDate loanExpirationDate;

    private int loanExpirationDateInDays;

    private CreditCard creditCard;
}
