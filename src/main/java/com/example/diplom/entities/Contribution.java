package com.example.diplom.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Contribution {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double sumOfContribution;

    @Min(value = 0, message = "Сумма не может быть отрицательной")
    private double minOfSum;

    @Min(value = 0, message = "Сумма не может быть отрицательной")
    private double maxOfSum;

    private int durationOfContributionInDays;

    private double interestRate;

    private double minSumOfMonth;

    private LocalDate durationOfContribution;

    private boolean isReplenished;

    private boolean isClosing;

    private boolean isCapitalize;

    private boolean isPossibleWithdrawFunds;

    @Value("false")
    private boolean isCreatedByAdmin;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
