package com.bajaj.assignment.runner;

import com.bajaj.assignment.service.WebhookService;
import com.bajaj.assignment.model.WebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(StartupRunner.class);

    private final WebhookService webhookService;

    public StartupRunner(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Generating webhook...");
        WebhookResponse response = webhookService.generateWebhook();
        if (response == null || response.getWebhook() == null || response.getAccessToken() == null) {
            log.error("Invalid response from generateWebhook API.");
            return;
        }
        String finalSql = webhookService.loadFinalSql();
        log.info("Submitting final SQL to webhook...");
        webhookService.submitFinalQuery(response.getWebhook(), response.getAccessToken(), finalSql);
        log.info("Submission complete.");
    }
}


