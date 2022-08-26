package com.example.diplom.repositories;

import com.example.diplom.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRole(String role);

}