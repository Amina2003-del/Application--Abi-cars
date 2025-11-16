package location_voiture.web.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import location_voiture.service.LitigeService;




@RestController
@RequestMapping("/litiges/files")
public class LitigeClientController {

    private final String uploadDir = "uploads/litiges";

    @GetMapping("/{reservationId}/{filename:.+}")
    public ResponseEntity<UrlResource> getFile(@PathVariable Long reservationId, @PathVariable String filename) {
        try {
            // Construire le chemin complet du fichier
            Path filePath = Paths.get(uploadDir, String.valueOf(reservationId), filename).normalize();

            // Créer l'UrlResource à partir de ce chemin
            UrlResource resource = new UrlResource(filePath.toUri());

            // Vérifier si le fichier existe et est lisible
            if (resource.exists() && resource.isReadable()) {
                // Déterminer le type MIME du fichier
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                // Retourner la réponse avec le fichier en contenu
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        // Inline pour affichage direct, attachment pour téléchargement forcé
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            // URL mal formée (chemin invalide)
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            // Erreur IO (par ex. problème d'accès au fichier)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
