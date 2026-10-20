package com.voyage.backend.service;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import com.voyage.backend.dto.DestinationResponse;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class DestinationService {
    public DestinationResponse searchDestination(String query){
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url="https://en.wikipedia.org/w/rest.php/v1/search/page?q="+encodedQuery+"&limit=1";
        HttpRequest request=HttpRequest.newBuilder().uri(URI.create(url)).header("Accept","application/json").header("User-Agent", "VoyageTravelPlanner/1.0 (student educational project; contact: argayathri11@gmail.com)").GET().build();
        try {
            HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );
            System.out.println("Wikipedia status: " + response.statusCode());
            System.out.println("Wikipedia response: " + response.body());
            JsonNode root=objectMapper.readTree(response.body());
            JsonNode pages=root.path("pages");
            if (pages.isArray() && !pages.isEmpty()) {
                JsonNode firstPage = pages.get(0);

                String title = firstPage.path("title").asText();
                String description = firstPage.path("description").asText();

                System.out.println("Found destination: " + title);
                System.out.println("Description: " + description);
                return new DestinationResponse(title,description,0.0,0.0,null);
            } 
        }
            catch (Exception e) {
            System.out.println("Wikipedia request failed: " + e.getMessage());
        }
        return new DestinationResponse(query,"Destination search is being tested",10.0889,77.0595,null);
    }
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper=new ObjectMapper();
}