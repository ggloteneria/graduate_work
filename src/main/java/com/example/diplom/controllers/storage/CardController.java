package com.example.diplom.controllers.storage;

import com.example.diplom.dto.CardDTO;
import com.example.diplom.entities.Card;
import com.example.diplom.entities.User;
import com.example.diplom.services.UserService;
import com.example.diplom.services.storage.CardService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public CardController(CardService cardService, UserService userService, ModelMapper modelMapper) {
        this.cardService = cardService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public String createNewCard() {
        Card card = cardService.createCard();
        cardService.saveCard(card);
        return "redirect:/cards";
    }

    @GetMapping
    public String showInfoAboutCard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        List<CardDTO> cardDTOList = cardService.findCardsByUser(user).stream()
                .map(this::convertToCardDTO)
                .collect(Collectors.toList());
        model.addAttribute("cards", cardDTOList);
        return "/card/cards_info";
    }

    @GetMapping("/{card_id}")
    public String getCardById(@PathVariable("card_id") Long card_id, Model model) {
        CardDTO cardDTO = convertToCardDTO(cardService.findById(card_id));
        model.addAttribute("card", cardDTO);
        return "card/card";
    }

    private CardDTO convertToCardDTO(Card card) {
        return modelMapper.map(card, CardDTO.class);
    }

}
