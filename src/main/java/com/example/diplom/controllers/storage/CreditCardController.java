package com.example.diplom.controllers.storage;

import com.example.diplom.dto.CardDTO;
import com.example.diplom.dto.CreditCardDTO;
import com.example.diplom.entities.ApplicationForCreditCard;
import com.example.diplom.entities.Card;
import com.example.diplom.entities.CreditCard;
import com.example.diplom.entities.User;
import com.example.diplom.repositories.storage.CreditCardRepository;
import com.example.diplom.services.storage.ApplicationForCreditCardService;
import com.example.diplom.services.UserService;
import com.example.diplom.services.storage.CreditCardService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/credit_cards")
public class CreditCardController {
    private final ApplicationForCreditCardService applicationForCreditCardService;
    private final CreditCardService creditCardService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public CreditCardController(ApplicationForCreditCardService applicationForCreditCardService, CreditCardService creditCardService, UserService userService, ModelMapper modelMapper) {
        this.applicationForCreditCardService = applicationForCreditCardService;
        this.creditCardService = creditCardService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public String showInfoAboutCreditCards(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());

        creditCardService.isClearCreditCard(creditCardService.findCreditCardsByUserAndIsReleasedTrue(user));

        List<CreditCardDTO> creditCardList = creditCardService.findCreditCardsByUserAndIsReleasedTrue(user).stream()
                .map(this::convertToCreditCardDTO)
                .collect(Collectors.toList());
        model.addAttribute("creditCards", creditCardList);

        List<CreditCardDTO> creditOffers = creditCardService.findCreditCardsByUserAndIsReleasedFalse(user).stream()
                .map(this::convertToCreditCardDTO)
                .collect(Collectors.toList());
        model.addAttribute("creditOffers", creditOffers);
        return "/credit_card/credit_cards_info";
    }

    @PostMapping(params = "submitAnApplication")
    public String submitAnApplication(Model model) {
        ApplicationForCreditCard application = applicationForCreditCardService.createApplication();
        applicationForCreditCardService.save(application);
        model.addAttribute("message", "Заявка была успешно отправлена");
        return "/credit_card/credit_cards_info";
    }

    @PostMapping(params = "accept_button")
    public String acceptOffer(@RequestParam Long creditCardId) {
        CreditCard creditCard = creditCardService.findById(creditCardId);
        creditCard.setReleased(true);
        creditCardService.save(creditCard);
        return "/credit_card/credit_cards_info";
    }

    @PostMapping(params = "reject_button")
    public String rejectOffer(@RequestParam Long creditCardId) {
        creditCardService.deleteById(creditCardId);
        return "/credit_card/credit_cards_info";
    }

    private CreditCardDTO convertToCreditCardDTO(CreditCard creditCard) {
        return modelMapper.map(creditCard, CreditCardDTO.class);
    }
}
