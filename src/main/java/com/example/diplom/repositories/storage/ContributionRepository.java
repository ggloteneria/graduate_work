package com.example.diplom.repositories.storage;

import com.example.diplom.entities.Contribution;
import com.example.diplom.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {

    @Query("SELECT c FROM Contribution c WHERE c.isCreatedByAdmin = true")
    List<Contribution> findContributionsByCreatedByAdminIsTrue();

    @Query("SELECT c FROM Contribution c WHERE c.isCreatedByAdmin = false")
    List<Contribution> findContributionsByCreatedByAdminIsFalse();

    List<Contribution> findContributionsByUser(User user);


}
