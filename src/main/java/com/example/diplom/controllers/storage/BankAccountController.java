package com.example.diplom.controllers.storage;

import com.example.diplom.dto.BankAccountDTO;
import com.example.diplom.entities.BankAccount;
import com.example.diplom.entities.User;
import com.example.diplom.repositories.storage.BankAccountRepository;
import com.example.diplom.services.storage.BankAccountService;
import com.example.diplom.services.UserService;
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
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/accounts")
public class BankAccountController {

    private final UserService userService;
    private final BankAccountService bankAccountService;

    private final ModelMapper modelMapper;

    public BankAccountController(UserService userService, BankAccountService bankAccountService, ModelMapper modelMapper) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public String createNewAccount() {
        BankAccount bankAccount = bankAccountService.createBankAccount();
        bankAccountService.save(bankAccount);
        return "redirect:/accounts";
    }

    @GetMapping
    public String showInfoAboutAccounts(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        List<BankAccountDTO> bankAccountDTOList = bankAccountService.findByUser(user).stream()
                .map(this::convertToBankAccountDTO)
                .collect(Collectors.toList());
        model.addAttribute("accounts", bankAccountDTOList);
        return "/bankAccounts/bank_accounts_info";
    }

    @GetMapping("/{account_id}")
    public String getAccountById(@PathVariable("account_id") Long account_id,
                                 Model model) {
        BankAccountDTO bankAccountDTO = convertToBankAccountDTO(bankAccountService.findById(account_id));
        model.addAttribute("bankAccount", bankAccountDTO);
        return "bankAccounts/bank_account";
    }

    private BankAccountDTO convertToBankAccountDTO(BankAccount bankAccount) {
        return modelMapper.map(bankAccount, BankAccountDTO.class);
    }

}
