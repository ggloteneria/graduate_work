package com.example.diplom.dto;

import com.example.diplom.entities.BankAccount;
import com.example.diplom.entities.Card;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
public class TransferDTO {

    private Long transferId;

    @NotEmpty(message = "Введите номер карты")
    @Pattern(regexp = "^[0-9]{10}|[0-9]{20}$", message = "Номер карты состоит из 10 цифр, номер счёта из 20 цфир")
    private String numberForTransfer;

    private LocalDateTime localDateTime;

    @Min(value = 1, message = "Минимальный перевод 1 рубль")
    private double sum;

    @Value("false")
    private boolean isNeedToCheck;

    private Card card;

    private BankAccount bankAccount;

}
