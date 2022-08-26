package com.example.diplom.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BankAccount implements MoneyStorage{
    @Id
    @Column(name = "account_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String affiliationNumberOfAccount;

    private String personalNumberOfAccount;

    private String fullNumber = getAffiliationNumberOfAccount() + getPersonalNumberOfAccount();

    private double balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "bankAccount")
    private List<Transfer> transfers;

}
