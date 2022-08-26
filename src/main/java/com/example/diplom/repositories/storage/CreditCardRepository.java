package com.example.diplom.repositories.storage;

import com.example.diplom.entities.CreditCard;
import com.example.diplom.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

    List<CreditCard> findCreditCardsByUser(User user);

    @Query("SELECT c FROM CreditCard c WHERE c.isReleased = true AND c.user = ?1")
    List<CreditCard> findCreditCardsByUserAndIsReleasedTrue(User user);

    @Query("SELECT c FROM CreditCard c WHERE c.isReleased = false AND c.user = ?1")
    List<CreditCard> findCreditCardsByUserAndIsReleasedFalse(User user);
}
