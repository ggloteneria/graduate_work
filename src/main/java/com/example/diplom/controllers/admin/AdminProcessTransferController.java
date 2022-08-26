package com.example.diplom.controllers.admin;

import com.example.diplom.dto.ContributionDTO;
import com.example.diplom.dto.TransferDTO;
import com.example.diplom.entities.Contribution;
import com.example.diplom.entities.Transfer;
import com.example.diplom.repositories.TransferRepository;
import com.example.diplom.services.TransferService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/transfers")
public class AdminProcessTransferController {

    private final TransferRepository transferRepository;
    private final TransferService transferService;
    private final ModelMapper modelMapper;

    @Autowired
    public AdminProcessTransferController(TransferRepository transferRepository, TransferService transferService, ModelMapper modelMapper) {
        this.transferRepository = transferRepository;
        this.transferService = transferService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public String showTransfers(Model model) {
        List<TransferDTO> transfersFromAccount = transferService.findTransfersFromAccountByNeedToCheck().stream()
                .map(this::convertToTransferDTO)
                .collect(Collectors.toList());
        model.addAttribute("transfersFromAccount", transfersFromAccount);

        List<TransferDTO> transfersFromCard = transferService.findTransfersFromCardByNeedToCheck().stream()
                .map(this::convertToTransferDTO)
                .collect(Collectors.toList());
        model.addAttribute("transfersFromCard", transfersFromCard);
        return "/admin/transfer_processing";
    }

    @PostMapping(params = "success_button1")
    public String allowTransferFromCard(@RequestParam Long transfer_id) {
        Transfer transfer = transferService.findById(transfer_id);
        transferService.changeBalanceAndSetNeedToCheckToFalse(transfer, transfer.getCard());
        transferService.save(transfer);
        return "/admin/transfer_processing";
    }

    @PostMapping(params = "block_button1")
    public String blockTransferFromCard(@RequestParam Long transfer_id) {
        transferRepository.deleteById(transfer_id);
        return "/admin/transfer_processing";
    }

    @PostMapping(params = "success_button2")
    public String allowTransferFromAccount(@RequestParam Long transfer_id) {
        Transfer transfer = transferRepository.getById(transfer_id);
        transferService.changeBalanceAndSetNeedToCheckToFalse(transfer, transfer.getBankAccount());
        transferService.save(transfer);
        return "/admin/transfer_processing";
    }

    @PostMapping(params = "block_button2")
    public String blockTransferFromAccount(@RequestParam Long transfer_id) {
        transferService.deleteById(transfer_id);
        return "/admin/transfer_processing";
    }

    private TransferDTO convertToTransferDTO(Transfer transfer) {
        return modelMapper.map(transfer, TransferDTO.class);
    }
}
