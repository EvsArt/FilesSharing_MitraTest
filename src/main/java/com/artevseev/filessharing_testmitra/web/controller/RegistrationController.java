package com.artevseev.filessharing_testmitra.web.controller;

import com.artevseev.filessharing_testmitra.web.data.model.RegistrationForm;
import com.artevseev.filessharing_testmitra.web.data.model.User;
import com.artevseev.filessharing_testmitra.web.data.repository.RoleRepository;
import com.artevseev.filessharing_testmitra.web.data.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

import java.util.Optional;

@Controller
@RequestMapping("/registration")
@Slf4j
public class RegistrationController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    RegistrationController(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String getRegPage(Model model){
        model.addAttribute("registrationForm", new RegistrationForm());
        return "registrationPage";
    }

    @PostMapping
    public String processRegistration(@ModelAttribute("registrationForm") @Valid RegistrationForm form,
                                      Errors errors, SessionStatus sessionStatus){

        Optional<User> userOptional = userRepository.findByLogin(form.getLogin());
        if(userOptional.isPresent()){
            errors.rejectValue("login", "loginIsProhibited", "Данное имя пользователя уже занято!");
        }
        if(!form.getPassword().equals(form.getConfirmPassword())) {
            errors.rejectValue("confirmPassword","confirmPassword", "Пароли не совпадают!");
        }
        if (errors.hasErrors()){
            return "registrationPage";
        }
        User user = form.toUser(roleRepository, passwordEncoder);

        userRepository.save(user);
        log.info("New user has joined to us: {}", user.getLogin());
        sessionStatus.setComplete();
        return "redirect:/login";

    }

}
