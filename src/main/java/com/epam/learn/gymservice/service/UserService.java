package com.epam.learn.gymservice.service;

import com.epam.learn.gymservice.dao.UserRepository;
import com.epam.learn.gymservice.entity.User;
import com.epam.learn.gymservice.exception.TrainerNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Password generation utility
    public String generateRandomPassword() {
        //return RandomStringUtils.secure().next(10);
        return RandomStringUtils.randomAlphanumeric(10);
    }

    public String calculateUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        int count = 1;
        String finalUsername = baseUsername;
        while (userRepository.findByUsername(finalUsername).isPresent()) {
            finalUsername = baseUsername + count;
            count++;
        }
        return finalUsername;
    }

    public void changePassword(String username, String newPassword) {
        log.info("Changing password for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("User " + username + " not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password for user {} changed", username);
    }

}
