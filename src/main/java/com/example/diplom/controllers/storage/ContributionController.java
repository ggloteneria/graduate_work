package com.example.diplom.controllers.storage;

import com.example.diplom.dto.CardDTO;
import com.example.diplom.dto.ContributionDTO;
import com.example.diplom.entities.Card;
import com.example.diplom.entities.Contribution;
import com.example.diplom.entities.User;
import com.example.diplom.repositories.storage.ContributionRepository;
import com.example.diplom.services.UserService;
import com.example.diplom.services.storage.CardService;
import com.example.diplom.services.storage.ContributionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/contributions")
public class ContributionController {

    private final ContributionService contributionService;
    private final UserService userService;
    private final CardService cardService;
    private final ModelMapper modelMapper;

    @Autowired
    public ContributionController(ContributionService contributionService, UserService userService, CardService cardService, ModelMapper modelMapper) {
        this.contributionService = contributionService;
        this.userService = userService;
        this.cardService = cardService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public String showContributions(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());

        contributionService.renewalContribution(contributionService.findContributionsByUser(user));

        List<ContributionDTO> contributionDTOList = contributionService.findContributionsByUser(user).stream()
                .map(this::convertToContributionDto)
                .collect(Collectors.toList());
        model.addAttribute("contributions", contributionDTOList);
        return "/contributions/contributions";
    }

    @GetMapping(value = "/type_of_contributions")
    public String showTypeOfContributions(Model model) {
        List<ContributionDTO> contributionDTOList = contributionService.findContributionsByCreatedByAdminIsTrue().stream()
                .map(this::convertToContributionDto)
                .collect(Collectors.toList());
        model.addAttribute("contributions", contributionDTOList);
        return "contributions/type_of_contributions";
    }

    @PostMapping(value = "/type_of_contributions")
    public String chooseContribution() {
        return "contributions/type_of_contributions";
    }

    @GetMapping(value = "/type_of_contributions/open_contribution")
    public String showOpenContribution(@RequestParam Long contribution_id,
                                       Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        model.addAttribute("contribution", contributionService.findById(contribution_id));
        model.addAttribute("cards", cardService.findCardsByUser(user));
        return "contributions/open_contribution";
    }

    @PostMapping(value = "/type_of_contributions/open_contribution")
    public String openContribution(@RequestParam Long new_contribution_id,
                                   @RequestParam String card_number,
                                   @RequestParam double sum_of_open,
                                   Model model) {
        model.addAttribute("contribution", contributionService.findById(new_contribution_id));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        Contribution contribution = contributionService.openContribution(contributionService.findById(new_contribution_id), user);
        Card card = cardService.findCardByFullNumber(card_number);

        if (!contributionService.isCardBalanceEnough(card, sum_of_open)) {
            model.addAttribute("message", "Сумма пополнения превышает баланс карты");
            return "contributions/open_contribution";
        }

        if (contributionService.isCorrectSumForOpeningContribution(contribution, sum_of_open)) {
            contributionService.replenishAndSaveContribution(card, contribution, sum_of_open);
            contribution.setMinSumOfMonth(sum_of_open);
            contributionService.save(contribution);
            model.addAttribute("message", "Вклад успешно открыт и пополнен");
        } else {
            model.addAttribute("message", "Некорректная сумма вклада, проверьте минимальную и максимальную сумму тарифа");
            return "contributions/open_contribution";
        }
        return "contributions/open_contribution";
    }


    @GetMapping(value = "/replenish_contribution")
    public String showReplenishContributionPage(@RequestParam Long contribution_id,
                                                Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        model.addAttribute("contribution", contributionService.findById(contribution_id));
        model.addAttribute("cards", cardService.findCardsByUser(user));
        return "/contributions/replenish_contribution";
    }

    @PostMapping(value = "/replenish_contribution", params = "replenish_contribution")
    public String replenishContribution(@RequestParam Long contribution_id,
                                        @RequestParam String card_number,
                                        @RequestParam double sum_of_replenish,
                                        Model model) {
        Contribution contribution = contributionService.findById(contribution_id);
        Card card = cardService.findCardByFullNumber(card_number);
        if (contributionService.isCardBalanceEnough(card, sum_of_replenish)) {
            contributionService.replenishAndSaveContribution(card, contribution, sum_of_replenish);
            model.addAttribute("message", "Пополнение прошло успешно");
        } else {
            model.addAttribute("message", "На карте недостаточно средств");
            return "/contributions/replenish_contribution";
        }
        return "/contributions/replenish_contribution";
    }

    @GetMapping(value = "/withdraw_from_contribution")
    public String showWithDrawFromContributionPage(@RequestParam Long contribution_id,
                                                   Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        ContributionDTO contributionDTO = convertToContributionDto(contributionService.findById(contribution_id));
        model.addAttribute("contribution", contributionDTO);

        List<CardDTO> cardDTOList = cardService.findCardsByUser(user).stream()
                .map(this::convertToCardDTO)
                .collect(Collectors.toList());
        model.addAttribute("cards", cardDTOList);
        return "/contributions/withdraw_from_contribution";
    }

    @PostMapping(value = "/withdraw_from_contribution", params = "withdraw_from_contribution")
    public String withDrawFromContribution(@RequestParam Long contribution_id,
                                           @RequestParam String card_number,
                                           @RequestParam double sum_of_withdraw,
                                           Model model) {
        Contribution contribution = contributionService.findById(contribution_id);
        model.addAttribute("contribution", contribution);
        Card card = cardService.findCardByFullNumber(card_number);
        if (contributionService.isContributionSumEnough(contribution, sum_of_withdraw)) {
            contributionService.withdraw(card, contribution, sum_of_withdraw);

            contributionService.checkMinAmountOfMonth(contribution);

            model.addAttribute("message", "Снятие денег с вклада прошло успешно");
        } else {
            model.addAttribute("message", "Сумма снятия превышает сумму вклада");
            return "/contributions/withdraw_from_contribution";
        }
        return "/contributions/withdraw_from_contribution";
    }

    @GetMapping(value = "/close_contribution")
    public String showCloseContributionPage(@RequestParam Long contribution_id,
                                            Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        ContributionDTO contributionDTO = convertToContributionDto(contributionService.findById(contribution_id));
        model.addAttribute("contribution", contributionDTO);

        List<CardDTO> cardDTOList = cardService.findCardsByUser(user).stream()
                .map(this::convertToCardDTO)
                .collect(Collectors.toList());
        model.addAttribute("cards", cardDTOList);
        return "/contributions/close_contribution";
    }

    @PostMapping(value = "/close_contribution", params = "close_contribution")
    public String closeContribution(@RequestParam Long contribution_id,
                                    @RequestParam String card_number,
                                    Model model) {
        model.addAttribute("contribution", contributionService.findById(contribution_id));
        Contribution contribution = contributionService.findById(contribution_id);
        Card card = cardService.findCardByFullNumber(card_number);
        contributionService.closeContribution(contribution, card);
        return "redirect:/contributions";
    }

    private ContributionDTO convertToContributionDto(Contribution contribution) {
        return modelMapper.map(contribution, ContributionDTO.class);
    }

    private CardDTO convertToCardDTO(Card card) {
        return modelMapper.map(card, CardDTO.class);
    }
}
