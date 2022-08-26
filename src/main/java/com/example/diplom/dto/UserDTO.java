package com.example.diplom.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class UserDTO {

    @NotEmpty(message = "*Введите имя")
    private String firstName;

    @NotEmpty(message = "*Введите фамилию")
    private String lastName;

    @Length(min = 5, message = "*Никнейм должен состоять минимум из 5 символов")
    @NotEmpty(message = "*Введите никнейм")
    private String userName;

    @Length(min = 5, message = "*Пароль должен состоять минимум из 5 символов")
    @NotEmpty(message = "*Введите паспорт")
    private String password;

}
