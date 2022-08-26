package com.example.diplom.controllers.transfers;

import com.example.diplom.dto.TransferDTO;
import com.example.diplom.entities.Card;
import com.example.diplom.entities.Transfer;
import com.example.diplom.repositories.storage.CardRepository;
import com.example.diplom.services.FrodMonitoringService;
import com.example.diplom.services.TransferService;
import com.example.diplom.services.storage.CardService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/cards")
public class TransferFromCardController {

    private final TransferService transferService;
    private final CardService cardService;
    private final ModelMapper modelMapper;

    @Autowired
    public TransferFromCardController(TransferService transferService, CardService cardService, ModelMapper modelMapper) {
        this.transferService = transferService;
        this.cardService = cardService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{card_id}/transfer")
    public String transfer(@PathVariable("card_id") Long card_id,
                           Model model) {
        TransferDTO transferDTO = convertToTransferDTO(new Transfer());
        model.addAttribute("transfer", transferDTO);
        return "transfers/transfer";
    }

    @PostMapping("/{card_id}/transfer")
    public String doTransfer(@Valid Transfer transfer, BindingResult bindingResult,
                                   @PathVariable("card_id") Long card_id,
                                   Model model) {
        Card card = cardService.findById(card_id);

        if (card.getBalance() - transfer.getSum() < 0){
            model.addAttribute("messageAboutTransfer", "На карте недостаточно средств");
            return "transfers/transfer";
        }

        if (!transferService.isSberMoneyStorage(transfer)){
            model.addAttribute("messageAboutCommission", "Карта/счёт другого банка, будет взята комиссия 10%");
        }

        if (!bindingResult.hasErrors()) {
            transfer = transferService.doTransfer(transfer, card);
            transferService.isPermittedMessage(transfer, model);
            transferService.save(transfer);
        }
        return "transfers/transfer";

    }

    private TransferDTO convertToTransferDTO(Transfer transfer) {
        return modelMapper.map(transfer, TransferDTO.class);
    }
}
