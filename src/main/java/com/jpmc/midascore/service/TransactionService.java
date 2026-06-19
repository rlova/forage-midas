package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final UserRecordRepository userRepository;
    private final TransactionRecordRepository transactionRepository;
    private final IncentiveService incentiveService;

    public TransactionService(UserRecordRepository userRepository,
                              TransactionRecordRepository transactionRepository,
                              IncentiveService incentiveService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.incentiveService = incentiveService;
    }

    @Transactional
    public boolean processTransaction(Transaction transaction) {
        // 1. Check if sender exists (find by numeric ID)
        long senderId = transaction.getSenderId();
        UserRecord sender = userRepository.findById(senderId)
                .orElse(null);
        if (sender == null) {
            logger.warn("Sender not found: {}", senderId);
            return false;
        }

        // 2. Check if recipient exists (find by numeric ID)
        long recipientId = transaction.getRecipientId();
        UserRecord recipient = userRepository.findById(recipientId)
                .orElse(null);
        if (recipient == null) {
            logger.warn("Recipient not found: {}", recipientId);
            return false;
        }

        // 3. Check if sender has enough money
        float amountFloat = transaction.getAmount();
        float senderBalance = sender.getBalance();

        if (senderBalance < amountFloat) {
            logger.warn("Insufficient balance: {} has {}, needs {}",
                    sender.getName(), senderBalance, amountFloat);
            return false;
        }

        // 4. Get incentive from API
        float incentiveFloat = incentiveService.getIncentive(transaction);

        // 5. Update balances
        sender.setBalance(senderBalance - amountFloat);
        recipient.setBalance(recipient.getBalance() + amountFloat + incentiveFloat);

        // 6. Save the updated users
        userRepository.save(sender);
        userRepository.save(recipient);

        // 7. Save the transaction record
        BigDecimal amount = BigDecimal.valueOf(amountFloat);
        BigDecimal incentive = BigDecimal.valueOf(incentiveFloat);
        TransactionRecord record = new TransactionRecord(sender, recipient, amount,incentive);
        transactionRepository.save(record);

        logger.info("Transaction processed: {} -> {} (amount: {})",
                sender.getName(), recipient.getName(), amount);

        // After saving the transaction record
        UserRecord waldorf = userRepository.findByName("waldorf").orElse(null);
        if (waldorf != null) {
            logger.info("BALANCE: {}", waldorf.getBalance());
        }

        return true;
    }
}