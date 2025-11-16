package location_voiture.persistence.model;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import location_voiture.repository.VilleRepository;

@Component
public class SetupDataLoaders implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private VilleRepository villeRepository;

    @Autowired
    @Qualifier("webRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        loadAndPopulateCities();
    }

    private void loadAndPopulateCities() {
        try {
            ClassPathResource resource = new ClassPathResource("villes-maroc.json");
            JsonNode rootNode = objectMapper.readTree(resource.getInputStream());
            Iterator<JsonNode> elements = rootNode.elements();

            while (elements.hasNext()) {
                JsonNode cityNode = elements.next();
                String nom = cityNode.get("nom").asText();
                String region = cityNode.get("region").asText();
                String pays = cityNode.get("pays").asText();

                List<Localisation> existingLoc = villeRepository.findByNom(nom);
                if (existingLoc == null) {
                    Localisation loc = new Localisation(nom, region, pays);
                    villeRepository.save(loc); // Sauvegarde initiale sans coordonnées
                }

                fetchAndSetCoordinates(nom, region, pays);
            }
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement du fichier JSON : " + e.getMessage());
        }
    }

    private void fetchAndSetCoordinates(String nom, String region, String pays) {
        try {
            // Utiliser uniquement le nom pour simplifier la requête
            String query = URLEncoder.encode(nom, StandardCharsets.UTF_8);
            String apiUrl = "https://nominatim.openstreetmap.org/search?q=" + query + "&format=json&limit=1&addressdetails=1";
            
            // Ajouter un User-Agent personnalisé avec un email valide
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "LocationVoitureApp/1.0 (contact@aladintours.com)"); // Remplacez par votre email
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

            String responseBody = response.getBody();

            if (responseBody != null && !responseBody.isEmpty()) {
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                if (jsonNode.isArray() && jsonNode.size() > 0) {
                    JsonNode location = jsonNode.get(0);
                    if (location.has("lat") && location.has("lon")) {
                        double lat = location.get("lat").asDouble();
                        double lon = location.get("lon").asDouble();
                        List<Localisation> locs = villeRepository.findByNom(nom); // Gère les doublons
                        if (!locs.isEmpty()) {
                            for (Localisation loc : locs) {
                                loc.setLatitude(lat);
                                loc.setLongitude(lon);
                                villeRepository.save(loc);
                            }
                        } else {
                            System.out.println("Aucune localisation trouvée pour " + nom + " à mettre à jour.");
                        }
                    } else {
                        System.out.println("Champs lat ou lon absents dans la réponse pour " + nom);
                    }
                } else {
                    System.out.println("Aucune donnée dans la réponse JSON pour " + nom + ". Tentative avec recherche approximative.");
                    // Recherche approximative si la requête initiale échoue
                    String altQuery = URLEncoder.encode(nom.split(",")[0].trim().replaceAll("[^a-zA-Z0-9]", ""), StandardCharsets.UTF_8);
                    String altApiUrl = "https://nominatim.openstreetmap.org/search?q=" + altQuery + "&format=json&limit=1&addressdetails=1";
                    ResponseEntity<String> altResponse = restTemplate.exchange(altApiUrl, HttpMethod.GET, entity, String.class);
                    String altResponseBody = altResponse.getBody();
                    JsonNode altJsonNode = objectMapper.readTree(altResponseBody);
                    if (altJsonNode.isArray() && altJsonNode.size() > 0) {
                        JsonNode altLocation = altJsonNode.get(0);
                        if (altLocation.has("lat") && altLocation.has("lon")) {
                            double lat = altLocation.get("lat").asDouble();
                            double lon = altLocation.get("lon").asDouble();
                            List<Localisation> locs = villeRepository.findByNom(nom);
                            if (!locs.isEmpty()) {
                                for (Localisation loc : locs) {
                                    loc.setLatitude(lat);
                                    loc.setLongitude(lon);
                                    villeRepository.save(loc);
                                }
                            }
                        }
                    }
                }
            } else {
                System.out.println("Réponse vide de Nominatim pour " + nom);
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Erreur HTTP pour " + nom + ": " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("Erreur générale pour " + nom + ": " + e.getMessage());
        }
        try {
            Thread.sleep(2000); // Augmenter à 2 secondes pour respecter les limites
        } catch (InterruptedException e) {
            System.out.println("Interruption de la temporisation pour " + nom + ": " + e.getMessage());
        }
    }}