package com.example.diplom.controllers;

import com.example.diplom.dto.UserDTO;
import com.example.diplom.entities.User;
import com.example.diplom.services.UserService;
import com.example.diplom.services.auth.RegistrationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;


@Controller
public class LoginController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final RegistrationService registrationService;

    @Autowired
    public LoginController(UserService userService, ModelMapper modelMapper, RegistrationService registrationService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.registrationService = registrationService;
    }

    @GetMapping(value = {"/", "/login"})
    public String login() {
        return "auth/login";
    }


    @GetMapping(value = "/registration")
    public String registration(Model model) {
        UserDTO userDTO = convertToUserDTO(new User());
        model.addAttribute("user", userDTO);
        return "auth/registration";
    }

    @PostMapping(value = "/registration")
    public String createNewUser(@Valid User user, BindingResult bindingResult,
                                Model model) {
//        User user = convertToUser(userDTO);
        model.addAttribute("user", user);
        User userExists = userService.findUserByUserName(user.getUserName());
        if (userExists != null) {
            bindingResult
                    .rejectValue("userName", "error.user",
                            "Пользователь с таким ником уже зарегистрирован");
        }
        if (!bindingResult.hasErrors()) {
            registrationService.register(user);
            model.addAttribute("successMessage", "Регистрация прошла успешно");
            return "auth/registration";
        }
        return "auth/registration";
    }

    private UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

//    private User convertToUser(UserDTO userDTO) {
//        return modelMapper.map(userDTO, User.class);
//    }

}
