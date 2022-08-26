package com.example.diplom.services.storage;

import com.example.diplom.entities.ApplicationForCreditCard;
import com.example.diplom.entities.User;
import com.example.diplom.repositories.ApplicationForCreditCardRepository;
import com.example.diplom.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationForCreditCardService {

    UserService userService;
    ApplicationForCreditCardRepository repository;

    @Autowired
    public ApplicationForCreditCardService(UserService userService, ApplicationForCreditCardRepository repository) {
        this.userService = userService;
        this.repository = repository;
    }

    public List<ApplicationForCreditCard> findAll(){
        return repository.findAll();
    }

    public void deleteById(Long id){
        repository.deleteById(id);
    }

    public ApplicationForCreditCard findById(Long id){
        return repository.findById(id).orElse(null);
    }

    public ApplicationForCreditCard createApplication(){
        ApplicationForCreditCard applicationForCreditCard = new ApplicationForCreditCard();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        applicationForCreditCard.setUser(user);
        return applicationForCreditCard;
    }

    public void save(ApplicationForCreditCard application){
        repository.save(application);
    }

}
