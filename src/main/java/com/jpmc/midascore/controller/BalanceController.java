package com.jpmc.midascore.controller;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRecordRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {

    private final UserRecordRepository userRepository;

    public BalanceController(UserRecordRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") long userId) {
        // Find user by ID
        UserRecord user = userRepository.findById(userId).orElse(null);

        // If user doesn't exist, return balance 0
        if (user == null) {
            return new Balance(0.0f);
        }

        // Return the user's balance
        return new Balance(user.getBalance());
    }
}