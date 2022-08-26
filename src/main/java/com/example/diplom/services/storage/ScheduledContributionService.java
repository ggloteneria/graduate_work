package com.example.diplom.services.storage;

import com.example.diplom.entities.Contribution;
import com.example.diplom.repositories.storage.ContributionRepository;
import com.example.diplom.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ScheduledContributionService {

    private final long DAY_IN_MILI_SECONDS = 86_400_000;
    private final long MONTH_IN_MILI_SECONDS = 2_592_000_000L;

    private final double PERCENTAGE_MULTIPLIER = 0.01;


    @Autowired
    UserService userService;

    @Autowired
    ContributionRepository contributionRepository;

    @Scheduled(fixedRate = MONTH_IN_MILI_SECONDS)
    public void calculateContributionInterest() {
        for (Contribution contribution : contributionRepository.findContributionsByCreatedByAdminIsFalse()) {
            if (contribution.getDurationOfContribution().isAfter(LocalDate.now())) {
                double interestAccrualAmount = contribution.getMinSumOfMonth() * (contribution.getInterestRate() * PERCENTAGE_MULTIPLIER);
                contribution.setSumOfContribution(contribution.getMinSumOfMonth() + interestAccrualAmount);
                contribution.setMinSumOfMonth(contribution.getSumOfContribution());
                contributionRepository.save(contribution);
            }
        }
    }

}
