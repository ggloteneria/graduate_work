package com.example.diplom.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    @NotEmpty(message = "*Введите имя")
    private String firstName;

    @Column(name = "last_name")
    @NotEmpty(message = "*Введите фамилию")
    private String lastName;

    @Column(name = "user_name")
    @Length(min = 5, message = "*Никнейм должен состоять минимум из 5 символов")
    @NotEmpty(message = "*Введите никнейм")
    private String userName;

    @Column(name = "password")
    @Length(min = 5, message = "*Пароль должен состоять минимум из 5 символов")
    @NotEmpty(message = "*Введите паспорт")
    private String password;

    @Column(name = "active")
    private Boolean active;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Card> cards;

    @OneToMany(mappedBy = "user")
    private List<BankAccount> bankAccounts;

    @OneToMany(mappedBy = "user")
    private List<CreditCard> creditCards;

    @OneToMany(mappedBy = "user")
    private List<Transfer> transfers;

    @OneToMany(mappedBy = "user")
    private List<ApplicationForCreditCard> applicationsForCreditCard;

    @OneToMany(mappedBy = "user")
    private List<Contribution> contributions;
}
