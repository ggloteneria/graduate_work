package com.example.diplom.repositories.storage;

import com.example.diplom.entities.credit.Credit;
import com.example.diplom.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {

    List<Credit> findCreditsByCreditCardId(Long creditCard_id);

    List<Credit> findCreditsByUser(User user);

}
