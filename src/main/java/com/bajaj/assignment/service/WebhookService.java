package com.bajaj.assignment.service;

import com.bajaj.assignment.config.CandidateProperties;
import com.bajaj.assignment.model.FinalQueryPayload;
import com.bajaj.assignment.model.WebhookRequest;
import com.bajaj.assignment.model.WebhookResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class WebhookService {
    private final RestTemplate restTemplate;
    private final CandidateProperties candidateProperties;
    private final String generateWebhookUrl;

    public WebhookService(RestTemplate restTemplate,
                          CandidateProperties candidateProperties,
                          @Value("${api.generateWebhookUrl}") String generateWebhookUrl) {
        this.restTemplate = restTemplate;
        this.candidateProperties = candidateProperties;
        this.generateWebhookUrl = generateWebhookUrl;
    }

    public WebhookResponse generateWebhook() {
        WebhookRequest request = new WebhookRequest(
                candidateProperties.getName(),
                candidateProperties.getRegNo(),
                candidateProperties.getEmail()
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);
        return restTemplate.postForObject(generateWebhookUrl, entity, WebhookResponse.class);
    }

    public String loadFinalSql() throws IOException {
        String sqlPath = candidateProperties.getSqlPath();
        ClassPathResource resource = new ClassPathResource(sqlPath);
        byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }

    public void submitFinalQuery(String webhookUrl, String accessToken, String finalQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, accessToken);
        HttpEntity<FinalQueryPayload> entity = new HttpEntity<>(new FinalQueryPayload(finalQuery), headers);
        restTemplate.postForLocation(webhookUrl, entity);
    }
}


