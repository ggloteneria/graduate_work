package com.example.diplom.controllers.transfers;

import com.example.diplom.dto.TransferDTO;
import com.example.diplom.entities.BankAccount;
import com.example.diplom.entities.Transfer;
import com.example.diplom.repositories.storage.BankAccountRepository;
import com.example.diplom.services.FrodMonitoringService;
import com.example.diplom.services.TransferService;
import com.example.diplom.services.storage.BankAccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/accounts")
public class TransferFromBankAccountController {

    private final TransferService transferService;
    private final BankAccountService bankAccountService;
    private final ModelMapper modelMapper;

    @Autowired
    public TransferFromBankAccountController(TransferService transferService, BankAccountService bankAccountService, ModelMapper modelMapper) {
        this.transferService = transferService;
        this.bankAccountService = bankAccountService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{account_id}/transfer")
    public String transfer(@PathVariable("account_id") Long accounts_id,
                           Model model) {
        TransferDTO transferDTO = convertToTransferDTO(new Transfer());
        model.addAttribute("transfer", transferDTO);
        return "transfers/transfer";
    }

    @PostMapping("/{account_id}/transfer")
    public String doTransfer(@Valid Transfer transfer, BindingResult bindingResult,
                             @PathVariable("account_id") Long account_id,
                             Model model) {

        BankAccount bankAccount = bankAccountService.findById(account_id);

        if (bankAccount.getBalance() - transfer.getSum() < 0){
            model.addAttribute("messageAboutTransfer", "На счету недостаточно средств");
            return "transfers/transfer";
        }

        if (!transferService.isSberMoneyStorage(transfer)){
            model.addAttribute("messageAboutCommission", "Карта/счёт другого банка, будет взята комиссия 10%");
        }

        if (!bindingResult.hasErrors()) {
            transfer = transferService.doTransfer(transfer, bankAccount);
            transferService.isPermittedMessage(transfer, model);
            transferService.save(transfer);
        }
        return "transfers/transfer";
    }

    private TransferDTO convertToTransferDTO(Transfer transfer) {
        return modelMapper.map(transfer, TransferDTO.class);
    }
}
