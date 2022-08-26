package com.example.diplom.entities;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Card implements MoneyStorage {
    @Id
    @Column(name = "card_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String affiliationNumberOfCard;

    private String personalNumberOfCard;

    private String fullNumber = getAffiliationNumberOfCard() + getPersonalNumberOfCard();

    private double balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "card")
    private List<Transfer> transfers;


}
