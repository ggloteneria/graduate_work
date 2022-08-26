package com.example.diplom.repositories.storage;

import com.example.diplom.entities.Card;
import com.example.diplom.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findCardsByUser(User user);

    Card findCardByFullNumber(String fullNumber);

}
