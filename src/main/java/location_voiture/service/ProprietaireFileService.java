package location_voiture.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

@Service
public class ProprietaireFileService {
	

	    @Value("${file.upload-dir}")
	    private String uploadDir;

	   
	    private static final Logger logger = LoggerFactory.getLogger(ProprietaireFileService.class);
	    // Création automatique des dossiers pour un propriétaire
	 // Dans ProprietaireFileService.java – ajoute cette méthode pour les attachments litiges
	    public List<String> saveLitigeAttachments(MultipartFile[] attachments, Long proprietaireId, String reservationId) throws IOException {
	        List<String> attachmentsPaths = new ArrayList<>();
	        if (attachments != null && attachments.length > 0) {
	            Path litigesDir = Paths.get(uploadDir, "proprietaire_" + proprietaireId, "litiges");
	            if (!Files.exists(litigesDir)) {
	                Files.createDirectories(litigesDir);
	            }

	            Path reservationDir = litigesDir.resolve(reservationId);
	            if (!Files.exists(reservationDir)) {
	                Files.createDirectories(reservationDir);
	            }

	            for (MultipartFile file : attachments) {
	                if (!file.isEmpty()) {
	                    String originalFilename = file.getOriginalFilename();
	                    String extension = "";
	                    if (originalFilename != null && originalFilename.contains(".")) {
	                        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
	                    }
	                    String filename = UUID.randomUUID().toString() + extension;
	                    Path filePath = reservationDir.resolve(filename);
	                    try (var inputStream = file.getInputStream()) {
	                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
	                    }
	                    // Retourne le chemin relatif (ex. : "uploads/proprietaire_1/litiges/15/uuid.ext")
	                    attachmentsPaths.add("uploads/proprietaire_" + proprietaireId + "/litiges/" + reservationId + "/" + filename);
	                }
	            }
	        }
	        return attachmentsPaths;
	    }

	    // Mise à jour de initProprietaireFolders pour inclure litiges
	    public void initProprietaireFolders(Long proprietaireId) throws IOException {
	        Path baseDir = Paths.get(uploadDir, "proprietaire_" + proprietaireId);

	        // Dossier images
	        Path imagesDir = Paths.get(uploadDir, "proprietaire_" + proprietaireId, "images");
	        if (!Files.exists(imagesDir)) {
	            Files.createDirectories(imagesDir);
	        }
	        Path logoDir = baseDir.resolve("logo");
	        Files.createDirectories(logoDir);

	        // Dossier factures
	        Path facturesDir = Paths.get(uploadDir, "proprietaire_" + proprietaireId, "factures");
	        if (!Files.exists(facturesDir)) {
	            Files.createDirectories(facturesDir);
	        }

	        // Nouveau : Dossier litiges
	        Path litigesDir = Paths.get(uploadDir, "proprietaire_" + proprietaireId, "litiges");
	        if (!Files.exists(litigesDir)) {
	            Files.createDirectories(litigesDir);
	        }

	        System.out.println("Dossiers créés pour le propriétaire " + proprietaireId);
	    }
	    // Upload image existant
	    public String saveImage(MultipartFile image, Long proprietaireId) throws IOException {
	        Path ownerImagesDir = Paths.get(uploadDir, "proprietaire_" + proprietaireId, "images");
	        if (!Files.exists(ownerImagesDir)) {
	            Files.createDirectories(ownerImagesDir);
	        }

	        String originalFileName = StringUtils.cleanPath(image.getOriginalFilename());
	        String newFileName = System.currentTimeMillis() + "_" + originalFileName;
	        Path targetPath = ownerImagesDir.resolve(newFileName);
	        try (var inputStream = image.getInputStream()) {
	            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
	        }

	        return newFileName;
	    }

	    // Upload facture existant
	    public String saveFacture(MultipartFile facture, Long proprietaireId, String annee, String mois) throws IOException {
	        // Chemin avec sous-dossiers année/mois
	        Path invoiceDir = Paths.get(uploadDir, "proprietaire_" + proprietaireId, "factures", annee, mois);
	        Path fallbackDir = Paths.get(uploadDir, "proprietaire_" + proprietaireId, "factures");  // Fallback sans sous-dossiers

	        try {
	            if (!Files.exists(invoiceDir)) {
	                Files.createDirectories(invoiceDir);
	            }
	            String originalFileName = StringUtils.cleanPath(facture.getOriginalFilename());
	            String newFileName = System.currentTimeMillis() + "_" + originalFileName;
	            Path targetPath = invoiceDir.resolve(newFileName);
	            try (var inputStream = facture.getInputStream()) {
	                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
	            }
	            // Retourne chemin avec sous-dossiers
	            return "uploads/proprietaire_" + proprietaireId + "/factures/" + annee + "/" + mois + "/" + newFileName;
	        } catch (IOException e) {
	            System.err.println("❌ Erreur création sous-dossiers année/mois pour facture upload ; fallback à factures/ : " + e.getMessage());
	            // Fallback : tente sans sous-dossiers
	            try {
	                if (!Files.exists(fallbackDir)) {
	                    Files.createDirectories(fallbackDir);
	                }
	                String originalFileName = StringUtils.cleanPath(facture.getOriginalFilename());
	                String newFileName = System.currentTimeMillis() + "_" + originalFileName;
	                Path targetPath = fallbackDir.resolve(newFileName);
	                try (var inputStream = facture.getInputStream()) {
	                    Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
	                }
	                // Retourne chemin fallback
	                return "uploads/proprietaire_" + proprietaireId + "/factures/" + newFileName;
	            } catch (IOException fallbackEx) {
	                throw new IOException("Échec upload facture même en fallback : " + fallbackEx.getMessage(), fallbackEx);
	            }
	        }
	    }

	    public String saveGeneratedFacture(byte[] pdfBytes, Long proprietaireId, String annee, String mois, String fileName) throws IOException {
	        if (pdfBytes == null || pdfBytes.length == 0) {
	            throw new IOException("Contenu PDF vide");
	        }

	        // Chemin avec sous-dossiers année/mois
	        Path invoiceDir = Paths.get(uploadDir, "proprietaire_" + proprietaireId, "factures", annee, mois);
	        Path fallbackDir = Paths.get(uploadDir, "proprietaire_" + proprietaireId, "factures");  // Fallback sans sous-dossiers

	        try {
	            if (!Files.exists(invoiceDir)) {
	                Files.createDirectories(invoiceDir);
	            }
	            // Nettoie le nom de fichier (ex. : "facture_123.pdf")
	            String cleanFileName = StringUtils.cleanPath(fileName);
	            if (!cleanFileName.toLowerCase().endsWith(".pdf")) {
	                cleanFileName += ".pdf";
	            }
	            Path targetPath = invoiceDir.resolve(cleanFileName);

	            // Écrit les bytes directement dans le fichier
	            Files.write(targetPath, pdfBytes);

	            // Retourne le chemin relatif pour l'URL (ex. : "uploads/proprietaire_1/factures/2025/11/facture_123.pdf")
	            return "uploads/proprietaire_" + proprietaireId + "/factures/" + annee + "/" + mois + "/" + cleanFileName;
	        } catch (IOException e) {
	            System.err.println("❌ Erreur création sous-dossiers année/mois pour facture générée ; fallback à factures/ : " + e.getMessage());
	            // Fallback : tente sans sous-dossiers
	            try {
	                if (!Files.exists(fallbackDir)) {
	                    Files.createDirectories(fallbackDir);
	                }
	                // Nettoie le nom de fichier
	                String cleanFileName = StringUtils.cleanPath(fileName);
	                if (!cleanFileName.toLowerCase().endsWith(".pdf")) {
	                    cleanFileName += ".pdf";
	                }
	                Path targetPath = fallbackDir.resolve(cleanFileName);

	                // Écrit les bytes directement dans le fichier
	                Files.write(targetPath, pdfBytes);

	                // Retourne le chemin relatif fallback
	                return "uploads/proprietaire_" + proprietaireId + "/factures/" + cleanFileName;
	            } catch (IOException fallbackEx) {
	                throw new IOException("Échec sauvegarde PDF générée même en fallback : " + fallbackEx.getMessage(), fallbackEx);
	            }
	        }}
	    
	 // Dans ProprietaireFileService
	    public Resource getAttachmentResource(Long proprietaireId, String reservationId, String filename) throws IOException {
	        Path litigesDir = Paths.get(uploadDir, "proprietaire_" + proprietaireId, "litiges");
	        Path reservationDir = litigesDir.resolve(reservationId);
	        Path fullPath = reservationDir.resolve(filename);
	        
	        if (!Files.exists(fullPath)) {
	            logger.warn("Attachment non trouvé : " + fullPath.toAbsolutePath());
	            return null;
	        }
	        
	        long fileSize = Files.size(fullPath);
	        logger.info("Attachment servi : {} (taille: {} bytes) pour proprio {} / résa {}", filename, fileSize, proprietaireId, reservationId);
	        
	        return new FileSystemResource(fullPath);
	    }

	    public String getAttachmentContentType(Long proprietaireId, String reservationId, String filename) throws IOException {
	        Path litigesDir = Paths.get(uploadDir, "proprietaire_" + proprietaireId, "litiges");
	        Path reservationDir = litigesDir.resolve(reservationId);
	        Path fullPath = reservationDir.resolve(filename);
	        
	        String contentType = Files.probeContentType(fullPath);
	        if (contentType == null) {
	            // Fallback sur extension
	            String lowerFilename = filename.toLowerCase();
	            if (lowerFilename.endsWith(".pdf")) {
	                contentType = "application/pdf";
	            } else if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
	                contentType = "image/jpeg";
	            } else if (lowerFilename.endsWith(".png")) {
	                contentType = "image/png";
	            } else {
	                contentType = "application/octet-stream"; // Default
	            }
	        }
	        return contentType;
	    }
	    public String saveLogo(MultipartFile logo, Long proprietaireId) throws IOException {
	        Path logoDir = Paths.get(uploadDir, "proprietaire_" + proprietaireId, "logo");
	        Files.createDirectories(logoDir);

	        String originalFileName = StringUtils.cleanPath(logo.getOriginalFilename());
	        String newFileName = System.currentTimeMillis() + "_" + originalFileName;

	        Path targetPath = logoDir.resolve(newFileName);
	        try (var inputStream = logo.getInputStream()) {
	            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
	        }

	        return newFileName; // ⚠ On retourne seulement le nom → à stocker dans la BD
	    }

}
