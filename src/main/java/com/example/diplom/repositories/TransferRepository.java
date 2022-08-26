package com.example.diplom.repositories;

import com.example.diplom.entities.Transfer;
import com.example.diplom.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;


@EnableJpaRepositories
@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findTransfersByUserAndBankAccountNotNull(User user);
    List<Transfer> findTransfersByUserAndCardNotNull(User user);

    @Query("SELECT t FROM Transfer t WHERE t.isNeedToCheck = true AND t.card IS NULL")
    List<Transfer> findTransfersFromAccountByNeedToCheck();

    @Query("SELECT t FROM Transfer t WHERE t.isNeedToCheck = true AND t.bankAccount IS NULL")
    List<Transfer> findTransfersFromCardByNeedToCheck();

}
