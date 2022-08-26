package com.example.diplom.services;

import com.example.diplom.entities.*;
import com.example.diplom.repositories.*;
import com.example.diplom.repositories.storage.BankAccountRepository;
import com.example.diplom.repositories.storage.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final UserService userService;
    private final CardRepository cardRepository;
    private final BankAccountRepository bankAccountRepository;
    private final FrodMonitoringService frodMonitoringService;

    @Autowired
    public TransferService(TransferRepository transferRepository, UserService userService, CardRepository cardRepository, BankAccountRepository bankAccountRepository, FrodMonitoringService frodMonitoringService) {
        this.transferRepository = transferRepository;
        this.userService = userService;
        this.cardRepository = cardRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.frodMonitoringService = frodMonitoringService;
    }

    public List<Transfer> findTransfersByUserAndCardNotNull(User user){
        return transferRepository.findTransfersByUserAndCardNotNull(user);
    }

    public List<Transfer> findTransfersByUserAndBankAccountNotNull(User user){
        return transferRepository.findTransfersByUserAndBankAccountNotNull(user);
    }

    public List<Transfer> findTransfersFromAccountByNeedToCheck(){
        return transferRepository.findTransfersFromAccountByNeedToCheck();
    }

    public List<Transfer> findTransfersFromCardByNeedToCheck(){
        return transferRepository.findTransfersFromCardByNeedToCheck();
    }

    public Transfer findById(Long id){
        return transferRepository.findById(id).orElse(null);
    }

    public void save(Transfer transfer) {
        transferRepository.save(transfer);
    }

    public void deleteById(Long id){
        transferRepository.deleteById(id);
    }

    public Transfer doTransfer(Transfer transfer, MoneyStorage moneyStorage) {
        transfer.setLocalDateTime(LocalDateTime.now());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        transfer.setUser(user);

        chooseMoneyStorageAndSetInTransfer(moneyStorage, transfer);

        if (!frodMonitoringService.isPermittedTransfer(transfer)) {
            transfer.setNeedToCheck(true);
        } else {
            changeBalanceAndSetNeedToCheckToFalse(transfer, moneyStorage);
        }

        return transfer;
    }

    private void chooseMoneyStorageAndSetInTransfer(MoneyStorage moneyStorage, Transfer transfer){
        if (moneyStorage instanceof Card) {
            transfer.setCard((Card) moneyStorage);
        } else if (moneyStorage instanceof BankAccount) {
            transfer.setBankAccount((BankAccount) moneyStorage);
        } else if (moneyStorage instanceof CreditCard) {
            transfer.setCreditCard((CreditCard) moneyStorage);
        }
    }

    public void changeBalanceAndSetNeedToCheckToFalse(Transfer transfer, MoneyStorage moneyStorage) {
        double commission = calcCommission(transfer);
        moneyStorage.setBalance(moneyStorage.getBalance() - (transfer.getSum() + commission));
        transfer.setNeedToCheck(false);

        if ((cardRepository.findCardByFullNumber(transfer.getNumberForTransfer()) != null)) {
            Card cardForReplenishment = cardRepository.findCardByFullNumber(transfer.getNumberForTransfer());
            cardForReplenishment.setBalance(cardForReplenishment.getBalance() + transfer.getSum());
        } else if ((bankAccountRepository.findBankAccountsByFullNumber(transfer.getNumberForTransfer()) != null)) {
            BankAccount bankAccountForReplenishment = bankAccountRepository.findBankAccountsByFullNumber(transfer.getNumberForTransfer());
            bankAccountForReplenishment.setBalance(bankAccountForReplenishment.getBalance() + transfer.getSum());
        }
    }

    public double calcCommission(Transfer transfer) {
        double commission = 0;
        if (!(transfer.getNumberForTransfer().startsWith("427600") || transfer.getNumberForTransfer().startsWith("427611"))) {
            commission = (transfer.getSum() * 0.1);
        }
        return commission;
    }

    public boolean isSberMoneyStorage(Transfer transfer){
        return (transfer.getNumberForTransfer().startsWith("427600") || transfer.getNumberForTransfer().startsWith("427611"));
    }

    public void isPermittedMessage(Transfer transfer, Model model) {
        if (frodMonitoringService.isPermittedTransfer(transfer)) {
            model.addAttribute("messageAboutTransfer", "Перевод успешно совершён");
        } else {
            model.addAttribute("messageAboutTransfer", "Перевод отправлен на проверку");
        }
    }

}
