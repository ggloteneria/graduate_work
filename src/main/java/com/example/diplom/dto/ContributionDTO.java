package com.example.diplom.dto;

import com.example.diplom.entities.User;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class ContributionDTO {

    private Long id;

    private double sumOfContribution;

    @Min(value = 0, message = "Сумма не может быть отрицательной")
    private double minOfSum;

    @Min(value = 0, message = "Сумма не может быть отрицательной")
    private double maxOfSum;

    private int durationOfContributionInDays;

    private double interestRate;

    private boolean isReplenished;

    private boolean isClosing;

    private boolean isCapitalize;

    private boolean isPossibleWithdrawFunds;

    private User user;

}
