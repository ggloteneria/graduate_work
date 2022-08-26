package com.example.diplom.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transfers")
public class Transfer {
    @Id
    @Column(name = "transfer_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long transferId;

    @NotEmpty(message = "Введите номер карты")
    @Pattern(regexp = "^[0-9]{10}|[0-9]{20}$", message = "Номер карты состоит из 10 цифр, номер счёта из 20 цфир")
    private String numberForTransfer;

    private LocalDateTime localDateTime;

    @Min(value = 1, message = "Минимальный перевод 1 рубль")
    private double sum;

    @Value("false")
    boolean isNeedToCheck;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    @ManyToOne
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;

    @ManyToOne
    @JoinTable(name = "transfer_credit_card", joinColumns = @JoinColumn(name = "transfer_id"),
            inverseJoinColumns = @JoinColumn(name = "credit_card_id"))
    @JoinColumn(name = "credit_card_id")
    private CreditCard creditCard;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
