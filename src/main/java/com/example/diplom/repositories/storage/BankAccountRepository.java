package com.example.diplom.repositories.storage;

import com.example.diplom.entities.BankAccount;
import com.example.diplom.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    List<BankAccount> findBankAccountsByUser(User user);

    BankAccount findBankAccountsByFullNumber(String fullNumber);

}
