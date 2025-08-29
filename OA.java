import java.net.http.*;
import java.net.URI;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AppStartup {
    public static void main(String[] args) throws Exception {
        // Create HTTP client
        HttpClient client = HttpClient.newHttpClient();

        // JSON body
        String jsonBody = """
        {
            "name": "John Doe",
            "regNo": "REG12347",
            "email": "john@example.com"
        }
        """;

        // Build POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA"))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(jsonBody))
                .build();

        // Send request
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        // Parse response JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(response.body());

        String webhook = jsonResponse.get("webhook").asText();
        String accessToken = jsonResponse.get("accessToken").asText();

        System.out.println("Webhook URL: " + webhook);
        System.out.println("Access Token: " + accessToken);
    }
}
