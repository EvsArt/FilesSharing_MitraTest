package com.artevseev.filessharing_testmitra.configuration;

import com.artevseev.filessharing_testmitra.web.data.model.Role;
import com.artevseev.filessharing_testmitra.web.data.model.User;
import com.artevseev.filessharing_testmitra.web.data.repository.RoleRepository;
import com.artevseev.filessharing_testmitra.web.data.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Properties;

@Configuration
public class StartData implements CommandLineRunner{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public StartData(UserRepository userRepository,
                     RoleRepository roleRepository,
                     PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args){

        if(!roleRepository.existsByName("USER")){
            Role role = new Role();
            role.setName("USER");
            roleRepository.save(role);
        }
        if(!roleRepository.existsByName("ADMIN")){
            Role role = new Role();
            role.setName("ADMIN");
            roleRepository.save(role);
        }

        if(!userRepository.existsByLogin("user1")){
            User user = new User();
            user.setRole(roleRepository.findRoleByName("USER"));
            user.setLogin("user1");
            user.setPassword(passwordEncoder.encode("12345678"));
            userRepository.save(user);
        }
        if(!userRepository.existsByLogin("user2")){
            User user = new User();
            user.setRole(roleRepository.findRoleByName("USER"));
            user.setLogin("user2");
            user.setPassword(passwordEncoder.encode("12345678"));
            userRepository.save(user);
        }
        if(!userRepository.existsByLogin("admin")){
            User user = new User();
            user.setRole(roleRepository.findRoleByName("ADMIN"));
            user.setLogin("admin");
            user.setPassword(passwordEncoder.encode("12345678"));
            userRepository.save(user);
        }
    }

}
