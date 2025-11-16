package location_voiture.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeoCodingService {

    public Double[] getLatLong(String address) throws Exception {
        String url = "https://nominatim.openstreetmap.org/search?q=" + 
            java.net.URLEncoder.encode(address, "UTF-8") + 
            "&format=json&limit=1";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("User-Agent", "Java App") // Obligatoire pour Nominatim
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode array = mapper.readTree(response.body());

        if (array.isArray() && array.size() > 0) {
            double lat = array.get(0).get("lat").asDouble();
            double lon = array.get(0).get("lon").asDouble();
            return new Double[]{lat, lon};
        }
        return null; // Pas trouvé
    }}