package com.artevseev.filessharing_testmitra.configuration;

import com.artevseev.filessharing_testmitra.web.data.model.User;
import com.artevseev.filessharing_testmitra.web.data.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration{

    final UserRepository userRepository;

    public SecurityConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepo){
        return username -> {
            Optional<User> user = userRepo.findByLogin(username);
            if(user.isPresent()) return user.get();
            throw new UsernameNotFoundException("User " + username + " not found!");
        };
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests()
                .requestMatchers(HttpMethod.GET, "/api/file").hasRole("ADMIN")  // Viewing files
                .requestMatchers(HttpMethod.POST, "api/file/**").hasAnyRole("USER", "ADMIN")    // Saving files
                .requestMatchers(HttpMethod.DELETE, "api/file/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/home/showall").hasRole("ADMIN")
                .requestMatchers("/registration").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/**").hasAnyRole("USER", "ADMIN")
                .and()
                .formLogin().loginPage("/login").defaultSuccessUrl("/home", true)
                .and()
                .logout().logoutSuccessUrl("/login")
                .and()
                .csrf()
                .disable()
                .build();
    }

}
