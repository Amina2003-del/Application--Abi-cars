package location_voiture.web.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import location_voiture.persistence.model.DemandePartenariat;
import location_voiture.persistence.model.Document;
import location_voiture.repository.DocumentRepository;
import location_voiture.service.DemandePartenariatService;

@Controller
@RequestMapping("/Partenariat")
public class PartenaireController {
	@Autowired
    private DemandePartenariatService demandeService;
	@Autowired
    private DocumentRepository documentRepository;
	
	@GetMapping("/inscription")
    public String showInscriptionForm() {
        return "Partenariat/DevenirPartenaire"; // Nom de la vue Thymeleaf
    }
	@PostMapping("/inscription")
    public ResponseEntity<String> submitDemande(
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom,
            @RequestParam("email") String email,
            @RequestParam("telephone") String telephone,
            @RequestParam("adresse") String adresse,
            @RequestParam("documents") MultipartFile[] documents) {
        try {
            DemandePartenariat demande = demandeService.submitDemande(nom, prenom, email, telephone, adresse, documents);
            return ResponseEntity.ok("Demande soumise avec succès ! ID: " + demande.getId());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Erreur lors de la soumission: " + e.getMessage());
        }
    }
	@GetMapping("/documents/{id}/download")
	public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) throws MalformedURLException {
	    Document doc = documentRepository.findById(id)
	    		.orElseThrow((Supplier<ResponseStatusException>) () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

	    Path path = Paths.get(doc.getFilePath());
	    Resource resource = new UrlResource(path.toUri());

	    if (!resource.exists() || !resource.isReadable()) {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier non trouvé");
	    }

	    return ResponseEntity.ok()
	            .contentType(MediaType.parseMediaType(doc.getContentType()))
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
	            .body(resource);
	}

}
