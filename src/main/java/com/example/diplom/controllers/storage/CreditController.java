package com.example.diplom.controllers.storage;

import com.example.diplom.dto.CreditCardDTO;
import com.example.diplom.dto.CreditDTO;
import com.example.diplom.entities.CreditCard;
import com.example.diplom.entities.credit.Credit;
import com.example.diplom.entities.credit.CreditCurrency;
import com.example.diplom.services.storage.CreditCardService;
import com.example.diplom.services.storage.CreditService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/credit_cards")
public class CreditController {
    private final CreditCardService creditCardService;
    private final CreditService creditService;
    private final ModelMapper modelMapper;

    @Autowired
    public CreditController(CreditCardService creditCardService, CreditService creditService, ModelMapper modelMapper) {
        this.creditCardService = creditCardService;
        this.creditService = creditService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/take_credit")
    public String showTakeCreditPage(@RequestParam Long credit_card_id, Model model) {
        CreditCardDTO creditCardDTO = convertToCreditCardDTO(creditCardService.findById(credit_card_id));
        model.addAttribute("creditCard", creditCardDTO);
        CreditDTO creditDTO = convertToCreditDTO(new Credit());
        model.addAttribute("credit", creditDTO);
        return "/credit/take_credit";
    }

    @PostMapping(value = "/take_credit", params = "take_credit_button")
    public String createCredit(@RequestParam Long credit_card_id,
                               @RequestParam double sumOfCredit,
                               @RequestParam CreditCurrency currency,
                               Model model) {
        CreditCard creditCard = creditCardService.findById(credit_card_id);
        model.addAttribute("creditCard", creditCard);
        model.addAttribute("currency", currency);
        if (creditService.areThereAnyOverdueCredit()) {
            model.addAttribute("messageAboutCredit", "Нельзя взять кредит, так как у вас есть просроченный кредит");
            return "/credit/take_credit";
        } else if (creditService.isCorrectSumOfCredit(sumOfCredit, creditCard, currency)) {
            Credit credit = creditService.createCredit(sumOfCredit, creditCard, currency);
            creditService.save(credit);
            model.addAttribute("messageAboutCredit", "Кредит успешно взят");
        } else {
            model.addAttribute("messageAboutCredit", "Сумма кредита превышает лимит кредитной карты");
            return "/credit/take_credit";
        }
        return "/credit/take_credit";
    }

    @GetMapping(value = "/credits")
    public String showInfoAboutCredits(@RequestParam Long credit_card_id,
                                       Model model) {
        CreditCardDTO creditCardDTO = convertToCreditCardDTO(creditCardService.findById(credit_card_id));
        model.addAttribute("creditCard", creditCardDTO);

        creditService.deleteCreditIfDateExpired(creditService.findCreditsByCreditCardId(credit_card_id));

        List<CreditDTO> creditDTOList = creditService.findCreditsByCreditCardId(credit_card_id).stream()
                .map(this::convertToCreditDTO)
                .collect(Collectors.toList());
        model.addAttribute("credits", creditDTOList);
        model.addAttribute("dateNow", LocalDate.now());
        return "/credit/credits_info";
    }

    @GetMapping(value = "/credits/repay")
    public String showRepayCreditPage(@RequestParam Long credit_id,
                                      Model model) {
        CreditDTO creditDTO = convertToCreditDTO(creditService.findById(credit_id));

        model.addAttribute("credit", creditDTO);
        model.addAttribute("creditCurrency", creditDTO.getCurrency());
        return "/credit/repay_credit";
    }

    @PostMapping(value = "/credits/repay", params = "repay_loan_button")
    public String repayCredit(@RequestParam Long credit_id,
                              @RequestParam double sumOfCreditRepay,
                              Model model) {
        Credit credit = creditService.findById(credit_id);
        model.addAttribute("credit", credit);

        if (creditService.isCorrectSumOfRepay(credit, sumOfCreditRepay)) {
            model.addAttribute("messageAboutCredit", "Сумма погашения превышает сумму долга");
            return "/credit/repay_credit";
        }

        if (creditService.isEnoughBalanceOnCreditCard(credit.getCreditCard().getBalance(), sumOfCreditRepay)) {
            creditService.repayCredit(credit, sumOfCreditRepay);
            creditService.save(credit);
            model.addAttribute("messageAboutCredit", "Сумма успешно погашена");
        } else {
            model.addAttribute("messageAboutCredit", "На карте недостаточно средств");
            return "/credit/repay_credit";
        }
        return "/credit/repay_credit";
    }

    private CreditDTO convertToCreditDTO(Credit credit) {
        return modelMapper.map(credit, CreditDTO.class);
    }

    private CreditCardDTO convertToCreditCardDTO(CreditCard creditCard) {
        return modelMapper.map(creditCard, CreditCardDTO.class);
    }
}
