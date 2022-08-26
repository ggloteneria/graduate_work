package com.example.diplom.services.storage;

import com.example.diplom.entities.credit.Credit;
import com.example.diplom.entities.credit.CreditCurrency;
import com.example.diplom.entities.CreditCard;
import com.example.diplom.entities.User;
import com.example.diplom.repositories.storage.CreditRepository;
import com.example.diplom.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreditService {

    private final int EUR = 70;
    private final int USD = 65;
    private final double COMMISSION = 0.1;

    @Autowired
    CreditRepository creditRepository;

    @Autowired
    UserService userService;

    public void save(Credit credit) {
        creditRepository.save(credit);
    }

    public List<Credit> findCreditsByCreditCardId(Long creditCardId){
        return creditRepository.findCreditsByCreditCardId(creditCardId);
    }

    public Credit findById(Long id){
        return creditRepository.findById(id).orElse(null);
    }

    public void repayCredit(Credit credit, double sumOfCreditRepay){
        credit.setSumOfCredit(credit.getSumOfCredit() - sumOfCreditRepay);
        CreditCard creditCard = credit.getCreditCard();
        creditCard.setBalance(creditCard.getBalance() - sumOfCreditRepay);
    }

    public Credit createCredit(double sum, CreditCard creditCard, CreditCurrency currency) {
        Credit credit = new Credit();
        credit.setCurrency(currency);
        credit.setPenaltiesSpentOnRepayment(0);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        credit.setUser(user);
        LocalDate creditExpirationDate = LocalDate.now().plusMonths(1);
        if (isCreditDataAfterCreditCardData(creditCard.getValidityPeriod(), creditExpirationDate)) {
//            LocalDate newData = creditCard.getValidityPeriod().minusDays(LocalDate.now().getDayOfMonth());
            credit.setLoanExpirationDate(creditCard.getValidityPeriod());
        } else {
            credit.setLoanExpirationDate(LocalDate.now().plusMonths(1));
        }
        credit.setCreditCard(creditCard);
        CreditCard creditCardRelatedToLoan = credit.getCreditCard();
        replenishmentBalanceOfCreditCardWithCurrency(sum, currency, creditCardRelatedToLoan);
        double newSum = getSumIfForeignCurrency(currency, sum);
        credit.setSumOfCredit(newSum);
        credit.setInitialSumOfCredit(newSum);
        return credit;
    }

    public boolean areThereAnyOverdueCredit() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        List<Credit> credits = creditRepository.findCreditsByUser(user);
        List<Credit> overdueCredits = credits.stream().filter(x -> x.getLoanExpirationDate().isBefore(LocalDate.now())).collect(Collectors.toList());
        return !overdueCredits.isEmpty();
    }

    public double getSumIfForeignCurrency(CreditCurrency currency, double sum) {
        if (currency == CreditCurrency.EUR) {
            sum = sum * (EUR + EUR * COMMISSION);
        } else if (currency == CreditCurrency.USD) {
            sum = sum * (USD + USD * COMMISSION);
        }
        return sum;
    }


    public void replenishmentBalanceOfCreditCardWithCurrency(double sum, CreditCurrency currency, CreditCard creditCardRelatedToLoan) {
        if (currency == CreditCurrency.EUR) {
            sum *= EUR;
        } else if (currency == CreditCurrency.USD) {
            sum *= USD;
        }
        creditCardRelatedToLoan.setBalance(creditCardRelatedToLoan.getBalance() + sum);
    }


    public boolean isCorrectSumOfCredit(double sum, CreditCard creditCard, CreditCurrency currency) {
        if (currency == CreditCurrency.EUR) {
            sum *= EUR;
        } else if (currency == CreditCurrency.USD) {
            sum *= USD;
        }
        return sum <= creditCard.getLimitOfCard() && sum >= 1;
    }

    public boolean isCorrectSumOfRepay(Credit credit, double sumOfCreditRepay) {
            return credit.getSumOfCredit() - sumOfCreditRepay < 0;
    }

    public boolean isCreditDataAfterCreditCardData(LocalDate creditCardData, LocalDate creditData) {
        return creditData.isAfter(creditCardData);
    }

    public boolean isEnoughBalanceOnCreditCard(double balance, double sumOfRepay) {
        return balance >= sumOfRepay;
    }

    public void deleteCreditIfDateExpired(List<Credit> creditList) {
        for (Credit credit : creditList) {
            if (credit.getLoanExpirationDate().isBefore(LocalDate.now()) && credit.getSumOfCredit() == 0) {
                creditRepository.delete(credit);
            }
        }
    }

}
