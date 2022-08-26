package com.example.diplom.controllers.admin;

import com.example.diplom.entities.CreditCard;
import com.example.diplom.entities.User;
import com.example.diplom.services.storage.ApplicationForCreditCardService;
import com.example.diplom.services.storage.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/applications")
public class AdminApplicationForCreditCardController {

    private final ApplicationForCreditCardService applicationForCreditCardService;
    private final CreditCardService creditCardService;

    @Autowired
    public AdminApplicationForCreditCardController(ApplicationForCreditCardService applicationForCreditCardService,
                                                   CreditCardService creditCardService) {
        this.applicationForCreditCardService = applicationForCreditCardService;
        this.creditCardService = creditCardService;
    }

    @GetMapping
    public String showApplications(Model model) {
        model.addAttribute("applications", applicationForCreditCardService.findAll());
        return "/admin/applications_for_credit_card";
    }

    @GetMapping(value = "/create_credit_card", params = "success_button")
    public String allowCreditCard() {
        return "/admin/create_credit_card";
    }

    @PostMapping(params = "block_button")
    public String blockCreditCard(@RequestParam Long application_id) {
        applicationForCreditCardService.deleteById(application_id);
        return "/admin/applications_for_credit_card";
    }

    @PostMapping(value = "/create_credit_card", params = "send_offer")
    public String createCreditCard(@RequestParam Long application_id, @RequestParam double percent,
                                   @RequestParam double limitOfCard) {
        User user = applicationForCreditCardService.findById(application_id).getUser();
        LocalDate validityPeriod = LocalDate.now().plusYears(2);
        CreditCard creditCard = creditCardService.createCreditCard(user, limitOfCard, percent, validityPeriod);
        creditCardService.saveCreditCard(creditCard);
        applicationForCreditCardService.deleteById(application_id);
        return "redirect:/admin/applications";
    }
}
