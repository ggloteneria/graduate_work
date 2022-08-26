package com.example.diplom.entities.credit;

import com.example.diplom.entities.CreditCard;
import com.example.diplom.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double initialSumOfCredit;

    private double sumOfCredit;

    private double penaltiesSpentOnRepayment;

    @Enumerated(EnumType.STRING)
    private CreditCurrency currency;

    private LocalDate loanExpirationDate;

    private int loanExpirationDateInDays;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "credit_card_id")
    private CreditCard creditCard;
}
