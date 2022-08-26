package com.example.diplom.services.storage;

import com.example.diplom.entities.BankAccount;
import com.example.diplom.entities.User;
import com.example.diplom.repositories.storage.BankAccountRepository;
import com.example.diplom.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private UserService userService;

    public void save(BankAccount bankAccount) {
        bankAccountRepository.save(bankAccount);
    }

    public BankAccount findById(Long id){
        return bankAccountRepository.findById(id).orElse(null);
    }

    public List<BankAccount> findByUser(User user){
        return bankAccountRepository.findBankAccountsByUser(user);
    }

    public BankAccount createBankAccount() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAffiliationNumberOfAccount("427600001111");
        bankAccount.setPersonalNumberOfAccount(Integer.toString((int) (Math.random() * 100_000_000)));
        bankAccount.setFullNumber(bankAccount.getAffiliationNumberOfAccount() + bankAccount.getPersonalNumberOfAccount());
        bankAccount.setBalance(0);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        bankAccount.setUser(user);
        return bankAccount;
    }

}
