package com.example.diplom.services.storage;

import com.example.diplom.entities.Card;
import com.example.diplom.entities.Contribution;
import com.example.diplom.entities.User;
import com.example.diplom.repositories.storage.CardRepository;
import com.example.diplom.repositories.storage.ContributionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ContributionService {

    @Autowired
    CardRepository cardRepository;

    @Autowired
    ContributionRepository contributionRepository;

    public void save(Contribution contribution){
        contributionRepository.save(contribution);
    }

    public Contribution findById(Long id){
        return contributionRepository.findById(id).orElse(null);
    }

    public List<Contribution> findContributionsByCreatedByAdminIsTrue(){
        return contributionRepository.findContributionsByCreatedByAdminIsTrue();
    }

    public void deleteById(Long id){
        contributionRepository.deleteById(id);
    }

    public List<Contribution> findContributionsByUser(User user){
        return contributionRepository.findContributionsByUser(user);
    }

    public void updateContribution(Long updatedContributionId, Contribution updatedContribution){
        updatedContribution.setCreatedByAdmin(true);
        updatedContribution.setId(updatedContributionId);
        contributionRepository.save(updatedContribution);
    }

    public Contribution openContribution(Contribution typeOfContribution, User user){
        Contribution newContribution = new Contribution();
        newContribution.setDurationOfContributionInDays(typeOfContribution.getDurationOfContributionInDays());
        newContribution.setCapitalize(typeOfContribution.isCapitalize());
        newContribution.setClosing(typeOfContribution.isClosing());
        newContribution.setMaxOfSum(typeOfContribution.getMaxOfSum());
        newContribution.setMinOfSum(typeOfContribution.getMinOfSum());
        newContribution.setPossibleWithdrawFunds(typeOfContribution.isPossibleWithdrawFunds());
        newContribution.setReplenished(typeOfContribution.isReplenished());
        newContribution.setInterestRate(typeOfContribution.getInterestRate());
        newContribution.setCreatedByAdmin(false);
        newContribution.setUser(user);
        newContribution.setDurationOfContribution(LocalDate.now().plusDays(typeOfContribution.getDurationOfContributionInDays()));
        newContribution.setMinSumOfMonth(newContribution.getSumOfContribution());
        return newContribution;
    }

    public void replenishAndSaveContribution(Card card, Contribution contribution, double sum){
        card.setBalance(card.getBalance() - sum);
        contribution.setSumOfContribution(contribution.getSumOfContribution() + sum);
        cardRepository.save(card);
        contributionRepository.save(contribution);
    }

    public boolean isCardBalanceEnough(Card card, double sum){
        return card.getBalance() >= sum;
    }

    public void withdraw(Card card, Contribution contribution, double sum){
        card.setBalance(card.getBalance() + sum);
        contribution.setSumOfContribution(contribution.getSumOfContribution() - sum);
        cardRepository.save(card);
        contributionRepository.save(contribution);
    }

    public boolean isContributionSumEnough(Contribution contribution, double sum){
        return contribution.getSumOfContribution() >= sum;
    }

    public void closeContribution(Contribution contribution, Card card){
        card.setBalance(card.getBalance() + contribution.getSumOfContribution());
        contributionRepository.delete(contribution);
    }

    public void checkMinAmountOfMonth(Contribution contribution){
        if (contribution.getSumOfContribution() < contribution.getMinSumOfMonth()){
            contribution.setMinSumOfMonth(contribution.getSumOfContribution());
        }
        contributionRepository.save(contribution);
    }

    public boolean isCorrectSumForOpeningContribution(Contribution contribution, double sum) {
        return (contribution.getMinOfSum() <= sum && contribution.getMaxOfSum() >= sum);
    }

    public void renewalContribution(List<Contribution> contributionList){
        for (Contribution contribution : contributionList){
            LocalDate durationOfContribution = contribution.getDurationOfContribution();
            if (durationOfContribution.isBefore(LocalDate.now())){
                contribution.setDurationOfContribution(durationOfContribution.plusDays(contribution.getDurationOfContributionInDays()));
                contributionRepository.save(contribution);
            }
        }
    }

}
