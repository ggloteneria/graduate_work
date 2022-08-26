package com.example.diplom.controllers.transfers;

import com.example.diplom.dto.TransferDTO;
import com.example.diplom.entities.Transfer;
import com.example.diplom.entities.User;
import com.example.diplom.repositories.TransferRepository;
import com.example.diplom.services.TransferService;
import com.example.diplom.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/history")
public class HistoryOfMoneyTransfersController {

    private final TransferService transferService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public HistoryOfMoneyTransfersController(TransferService transferService,
                                             UserService userService,
                                             ModelMapper modelMapper) {
        this.transferService = transferService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public String showHistory(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());

        List<TransferDTO> transfersFromCard = transferService.findTransfersByUserAndCardNotNull(user).stream()
                .map(this::convertToTransferDTO)
                .collect(Collectors.toList());
        model.addAttribute("transfersFromCard", transfersFromCard);

        List<TransferDTO> transfersFromAccount = transferService.findTransfersByUserAndBankAccountNotNull(user).stream()
                .map(this::convertToTransferDTO)
                .collect(Collectors.toList());
        model.addAttribute("transfersFromAccount", transfersFromAccount);
        return "/transfers/history_of_money_transfers";
    }

    private TransferDTO convertToTransferDTO(Transfer transfer) {
        return modelMapper.map(transfer, TransferDTO.class);
    }
}
