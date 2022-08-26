package com.example.diplom.dto;

import com.example.diplom.entities.User;
import lombok.Data;

@Data
public class CardDTO {
    private Long id;

    private String fullNumber;

    private double balance;

    private User user;

}
