package com.example.diplom.entities;

import org.springframework.stereotype.Component;

@Component
public interface MoneyStorage {

    void setBalance(double balance);
    double getBalance();

}
