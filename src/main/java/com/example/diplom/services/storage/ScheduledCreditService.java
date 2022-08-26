package com.example.diplom.services.storage;

import com.example.diplom.entities.credit.Credit;
import com.example.diplom.repositories.storage.CreditRepository;
import com.example.diplom.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ScheduledCreditService {

    private final int PERIOD_OF_CREDIT = 30;
    private final long DAY_IN_MILI_SECONDS = 86_400_000;
    private final double PENALTY_CHARGE_LIMIT = 0.4;
    private final double PENALTY = 0.01;

    @Autowired
    CreditRepository creditRepository;

    @Autowired
    UserService userService;

    @Scheduled(fixedRate = DAY_IN_MILI_SECONDS)
    public void calculateCreditInterest() {
        for (Credit credit : creditRepository.findAll()) {
            if (credit.getLoanExpirationDate().isBefore(LocalDate.now())) {
                double newSumOfCredit = credit.getSumOfCredit() + credit.getSumOfCredit() *
                        credit.getCreditCard().getPercent() / PERIOD_OF_CREDIT * (LocalDate.now().getDayOfYear() - credit.getLoanExpirationDate().getDayOfYear());
                credit.setSumOfCredit(newSumOfCredit);
                creditRepository.save(credit);
            }
        }
    }

    @Scheduled(fixedRate = DAY_IN_MILI_SECONDS)
    public void chargeCreditPenalties() {
        for (Credit credit : creditRepository.findAll()) {
            if (credit.getLoanExpirationDate().isBefore(LocalDate.now())) {
                if (credit.getInitialSumOfCredit() * PENALTY_CHARGE_LIMIT >= credit.getPenaltiesSpentOnRepayment()) {
                    double penalty = PENALTY * credit.getSumOfCredit();
                    credit.setSumOfCredit(credit.getSumOfCredit() + penalty);
                    credit.setPenaltiesSpentOnRepayment(credit.getPenaltiesSpentOnRepayment() + penalty);
                    creditRepository.save(credit);
                }
            }
        }
    }
}

