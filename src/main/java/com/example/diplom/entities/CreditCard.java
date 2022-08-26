package com.example.diplom.entities;

import com.example.diplom.entities.credit.Credit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CreditCard implements MoneyStorage{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String affiliationNumberOfCreditCard;

    private String personalNumberOfCreditCard;

    private String fullNumber = getAffiliationNumberOfCreditCard() + getPersonalNumberOfCreditCard();

    private double balance;

    private double percent;

    private double limitOfCard;

    private LocalDate validityPeriod;

    @Value("false")
    private boolean isReleased;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "creditCard")
    private List<Transfer> transfers;

    @OneToMany(mappedBy = "creditCard")
    private List<Credit> credits;

}
