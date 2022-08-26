package com.example.diplom.services.storage;

import com.example.diplom.entities.Card;
import com.example.diplom.entities.User;
import com.example.diplom.repositories.storage.CardRepository;
import com.example.diplom.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final UserService userService;

    @Autowired
    public CardService(CardRepository cardRepository, UserService userService) {
        this.cardRepository = cardRepository;
        this.userService = userService;
    }

    public Card findById(Long id){
        return cardRepository.findById(id).orElse(null);
    }

    public void saveCard(Card card) {
        cardRepository.save(card);
    }

    public List<Card> findCardsByUser(User user){
        return cardRepository.findCardsByUser(user);
    }

    public Card findCardByFullNumber(String number){
        return cardRepository.findCardByFullNumber(number);
    }

    public Card createCard() {
        Card card = new Card();
        card.setAffiliationNumberOfCard("427600");
        card.setPersonalNumberOfCard(Integer.toString((int) (Math.random() * 10_000)));
        card.setFullNumber(card.getAffiliationNumberOfCard() + card.getPersonalNumberOfCard());
        card.setBalance(0);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        card.setUser(user);
        return card;
    }

}
