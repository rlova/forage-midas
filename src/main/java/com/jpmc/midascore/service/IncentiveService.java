package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IncentiveService {

    private static final Logger logger = LoggerFactory.getLogger(IncentiveService.class);

    private final RestTemplate restTemplate;
    private final String incentiveUrl = "http://localhost:8080/incentive";

    public IncentiveService() {
        this.restTemplate = new RestTemplate();
    }

    public float getIncentive(Transaction transaction) {
        try {
            Incentive incentive = restTemplate.postForObject(incentiveUrl, transaction, Incentive.class);
            if (incentive != null && incentive.getAmount() >= 0) {
                logger.info("Incentive received: {}", incentive.getAmount());
                return incentive.getAmount();
            }
        } catch (Exception e) {
            logger.warn("Failed to get incentive: {}", e.getMessage());
        }
        return 0.0f;
    }
}
