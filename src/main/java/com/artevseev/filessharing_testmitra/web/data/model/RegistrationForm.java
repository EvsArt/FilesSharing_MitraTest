package com.artevseev.filessharing_testmitra.web.data.model;

import com.artevseev.filessharing_testmitra.web.data.repository.RoleRepository;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
public class RegistrationForm {

    private String login;
    @Size(min = 8, message = "Длина пароля не должна быть меньше 8 символов!")
    private String password;
    private String confirmPassword;

    public User toUser(RoleRepository roleRepository, PasswordEncoder passwordEncoder){
        return new User(login, passwordEncoder.encode(password), roleRepository.findRoleByName("USER"));
    }

}
