package com.example.diplom.dto;

import com.example.diplom.entities.User;
import lombok.Data;

@Data
public class BankAccountDTO {

    private Long id;

    private String fullNumber;

    private double balance;

    private User user;
}
