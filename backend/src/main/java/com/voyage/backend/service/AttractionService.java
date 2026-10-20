package com.voyage.backend.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.voyage.backend.dto.AttractionResponse;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class AttractionService {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private String getCategory(String name) {
        if (name.matches(".*(museum|gallery|memorial).*"))
            return "Museum";

        if (name.matches(".*(temple|church|mosque|monastery|stupa|gompa).*"))
            return "Religious";

        if (name.matches(".*(fort|palace|monument).*"))
            return "Historical";

        if (name.matches(".*(national park|sanctuary|reserve|zoo).*"))
            return "Wildlife";

        if (name.matches(".*(waterfall|falls|lake|dam|beach).*"))
            return "Nature";

        if (name.matches(".*(viewpoint|peak|pass|valley|glacier).*"))
            return "Viewpoint";

        if (name.matches(".*(garden|park).*"))
            return "Park";

        if (name.matches(".*(cave|trek).*"))
            return "Adventure";
        
        return "None";
    }

    public List<AttractionResponse> getAttractions(String destination) {
        List<AttractionResponse> attractions = new ArrayList<>();
        String encoded = URLEncoder.encode(
        destination, StandardCharsets.UTF_8
        );
        String url = "https://nominatim.openstreetmap.org/search?q="
        + encoded + "&format=jsonv2&limit=1";
        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("User-Agent", "VoyageTravelPlanner/1.0 (contact: argayathri11@gmail.com")
        .header("Accept", "application/json")
        .GET()
        .build();
        try {
            HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );
            if (response.statusCode() == 200) {
                    JsonNode locations = objectMapper.readTree(response.body());
                if (locations.isArray() && !locations.isEmpty()) {

                    JsonNode place = locations.get(0);

                    double latitude = Double.parseDouble(
                        place.get("lat").asText()
                    );

                    double longitude = Double.parseDouble(
                        place.get("lon").asText()
                    );
                    
                    System.out.println("Latitude: " + latitude);
                    System.out.println("Longitude: " + longitude);
                    String geoUrl = "https://en.wikipedia.org/w/api.php"
                    + "?action=query"
                    + "&list=geosearch"
                    + "&gscoord=" + latitude + "%7C" + longitude
                    + "&gsradius=10000"
                    + "&gslimit=50"
                    + "&format=json";
                    HttpRequest geoRequest = HttpRequest.newBuilder()
                    .uri(URI.create(geoUrl))
                    .header("User-Agent",
                        request.headers().firstValue("User-Agent").orElse("VoyageTravelPlanner/1.0"))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

                    HttpResponse<String> geoResponse = httpClient.send(
                        geoRequest,
                        HttpResponse.BodyHandlers.ofString()
                    );

                    System.out.println("Geosearch status: " + geoResponse.statusCode());

                    if (geoResponse.statusCode() == 200) {

                        JsonNode pages = objectMapper.readTree(geoResponse.body())
                            .path("query")
                            .path("geosearch");

                        String keywords =
                        "chapora|park|zoo|fort|palace|museum|temple|church|mosque|"
                        + "waterfall|falls|lake|dam|garden|beach|"
                        + "monument|viewpoint|sanctuary|reserve|"
                        + "tso|monastery|stupa|gompa|"
                        + "pass|glacier|valley|memorial|"
                        + "peak|cave";

                        for (JsonNode page : pages) {

                            String name = page.path("title").asText();
                            String lowerName = name.toLowerCase(Locale.ROOT);
                            String excluded =
                            "meghalaya|institute|kerala|district|state|university|college|school|"
                            + "cantonment|archdiocese|municipality|"
                            + "administration|constituency|airport|"
                            + "railway|hospital|headquarters";

                            boolean irrelevant = false;

                            for (String word : excluded.split("\\|")) {
                                if (lowerName.contains(word)) {
                                    irrelevant = true;
                                    break;
                                }
                            }

                            if (irrelevant) {
                                continue;
                            }
                            boolean isAttraction = false;

                            for (String keyword : keywords.split("\\|")) {
                                if (lowerName.contains(keyword)) {
                                    isAttraction = true;
                                    break;
                                }
                            }

                            if (!isAttraction) {
                                continue;
                            }

                            String category = getCategory(lowerName);
                            
                            attractions.add(new AttractionResponse(
                                name,
                                "Nearby place listed on Wikipedia",
                                page.path("lat").asDouble(),
                                page.path("lon").asDouble(),
                                category,
                                null
                            ));

                            if (attractions.size() == 15) {
                                break;
                            }
                        }
                    }
                }
            }
            System.out.println("Nominatim status: " + response.statusCode());
            System.out.println("Location data: " + response.body());
        } 
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Location request interrupted");
        } 
        catch (java.io.IOException e) {
            System.out.println("Location request failed: " + e.getMessage());
        }
        return attractions;
    }
}