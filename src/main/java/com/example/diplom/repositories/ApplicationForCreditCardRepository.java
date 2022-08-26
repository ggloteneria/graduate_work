package com.example.diplom.repositories;

import com.example.diplom.entities.ApplicationForCreditCard;
import com.example.diplom.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface ApplicationForCreditCardRepository extends JpaRepository<ApplicationForCreditCard, Long> {
}
