package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

@Component
class StartupRunner implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        // First API call
        String firstUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        String requestBody = """
        {
            "name": "John Doe",
            "regNo": "REG12347",
            "email": "john@example.com"
        }
        """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(firstUrl, entity, String.class);

        JsonNode json = mapper.readTree(response.getBody());
        String webhook = json.get("webhook").asText();
        String accessToken = json.get("accessToken").asText();

        System.out.println("Webhook: " + webhook);
        System.out.println("AccessToken: " + accessToken);

        // Submit final SQL query
        String secondUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
        String finalQueryJson = """
        {
            "SELECT 
                p.AMOUNT AS SALARY,
                CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME,
                TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE,
                d.DEPARTMENT_NAME
            FROM PAYMENTS p
            JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID
            JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
            WHERE DAY(p.PAYMENT_TIME) <> 1
            ORDER BY p.AMOUNT DESC
            LIMIT 1;
            "
        }
        """;

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setContentType(MediaType.APPLICATION_JSON);
        headers2.setBearerAuth(accessToken); // JWT token

        HttpEntity<String> entity2 = new HttpEntity<>(finalQueryJson, headers2);
        ResponseEntity<String> response2 = restTemplate.exchange(secondUrl, HttpMethod.POST, entity2, String.class);

        System.out.println("✅ Submission Response: " + response2.getBody());
    }
}
