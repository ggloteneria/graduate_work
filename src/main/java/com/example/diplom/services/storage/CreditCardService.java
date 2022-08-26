package com.example.diplom.services.storage;

import com.example.diplom.entities.CreditCard;
import com.example.diplom.entities.User;
import com.example.diplom.repositories.storage.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CreditCardService {

    @Autowired
    private CreditCardRepository creditCardRepository;

    public void saveCreditCard(CreditCard creditCard) {
        creditCardRepository.save(creditCard);
    }

    public List<CreditCard> findCreditCardsByUserAndIsReleasedTrue(User user){
        return creditCardRepository.findCreditCardsByUserAndIsReleasedTrue(user);
    }

    public List<CreditCard> findCreditCardsByUserAndIsReleasedFalse(User user){
        return creditCardRepository.findCreditCardsByUserAndIsReleasedFalse(user);
    }

    public CreditCard findById(Long id){
        return creditCardRepository.findById(id).orElse(null);
    }

    public void save(CreditCard creditCard){
        creditCardRepository.save(creditCard);
    }

    public void deleteById(Long id){
        creditCardRepository.deleteById(id);
    }

    public CreditCard createCreditCard(User user, double limitOfCard, double percent, LocalDate validityPeriod) {
        CreditCard creditCard = new CreditCard();
        creditCard.setUser(user);
        creditCard.setLimitOfCard(limitOfCard);
        creditCard.setPercent(percent);
        creditCard.setValidityPeriod(validityPeriod);
        creditCard.setAffiliationNumberOfCreditCard("427611");
        creditCard.setPersonalNumberOfCreditCard(Integer.toString((int) (Math.random() * 10_000)));
        creditCard.setFullNumber(creditCard.getAffiliationNumberOfCreditCard() + creditCard.getPersonalNumberOfCreditCard());
        creditCard.setBalance(0);
        return creditCard;
    }

    public void isClearCreditCard(List<CreditCard> creditCards){
        for (CreditCard card : creditCards) {
            if (card.getCredits().isEmpty() && card.getValidityPeriod().isBefore(LocalDate.now())) {
                creditCardRepository.delete(card);
            }
        }
    }

}
