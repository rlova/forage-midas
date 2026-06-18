package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    // Logger for printing information to the console
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    // The Kafka topic name from application.yml
    @Value("${general.kafka-topic}")
    private String topic;

    // Counter for the number of transactions received
    private int transactionCount = 0;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core")
    public void listen(String message) {
        // There are still a lot of errors, so this is just a test
        try {
            // Convert the JSON string to a Transaction object
            Transaction transaction = objectMapper.readValue(message, Transaction.class);
            transactionCount++;
            // Log the received transaction details
            logger.info("Received transaction #{}: {}", transactionCount, transaction);
            // Log the transaction amount details
            logger.info("Transaction amount: {}", transaction.getAmount());
        } catch (Exception e) {
            logger.error("Failed to parse transaction: {}", message, e);
        }
    }
}
