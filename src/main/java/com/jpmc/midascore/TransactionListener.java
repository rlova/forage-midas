package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    // Logger for printing information to the console
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    // Counter for the number of transactions received
    private int transactionCount = 0;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core")
    public void listen(Transaction transaction) {
        transactionCount++;
        // Log the received transaction details
        logger.info("Received transaction #{}: {}", transactionCount, transaction);
        // Log the transaction amount details
        logger.info("Transaction amount: {}", transaction.getAmount());
    }
}
