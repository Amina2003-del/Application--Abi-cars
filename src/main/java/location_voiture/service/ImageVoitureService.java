package location_voiture.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Gallery;
import location_voiture.repository.ImageVoitureRepository;

@Service
public class ImageVoitureService {

    private final ImageVoitureRepository imageVoitureRepository;
    private final Path fileStorageLocation;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB max
    private static final String[] ALLOWED_FILE_TYPES = {"image/jpeg", "image/png", "image/gif"};

    @Autowired
    public ImageVoitureService(ImageVoitureRepository imageVoitureRepository) {
        this.imageVoitureRepository = imageVoitureRepository;
        this.fileStorageLocation = Paths.get("src/main/resources/static/uploads")
.toAbsolutePath()
                .normalize();

       
    }

    // Stockage sur disque
    public Gallery storeImageToDisk(MultipartFile file) {
        validateFile(file);

        try {
            // 🧼 Nettoyer le nom du fichier (pour éviter les espaces, accents, caractères spéciaux)
            String originalName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileName = UUID.randomUUID().toString() + "_" + originalName
                    .replaceAll("\\s+", "_")
                    .replaceAll("[^a-zA-Z0-9._-]", "")
                    .toLowerCase();

            // 📁 Chemin réel : src/main/resources/static/uploads
            Path targetLocation = this.fileStorageLocation.resolve(fileName);

            // ✅ Remplacer si le fichier existe déjà
            Files.copy(file.getInputStream(), targetLocation, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // ✅ Création de l'objet Gallery
            Gallery image = new Gallery();
            image.setFileName(fileName);
            image.setUrl("/uploads/" + fileName); // correspond à l'accès public

            return image;

        } catch (IOException ex) {
            throw new RuntimeException("Impossible de stocker le fichier " + file.getOriginalFilename(), ex);
        }
    }


    // Stockage en base de données avec association à une voiture
    public Gallery storeImageToDatabase(MultipartFile file, Car car) throws IOException {
        if (car == null || car.getId() == null) {
            throw new IllegalArgumentException("Une voiture valide doit être associée à l'image");
        }

        validateFile(file);

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        Gallery imageVoiture = new Gallery();
        imageVoiture.setNom(fileName);
        imageVoiture.setType(file.getContentType());
        imageVoiture.setData(file.getBytes());
        imageVoiture.setCar(car);

        return imageVoitureRepository.save(imageVoiture);
    }

    // Validation des fichiers
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Le fichier est trop volumineux. La taille maximale est de 5MB.");
        }

        boolean validFileType = false;
        for (String allowedType : ALLOWED_FILE_TYPES) {
            if (file.getContentType().equals(allowedType)) {
                validFileType = true;
                break;
            }
        }

        if (!validFileType) {
            throw new IllegalArgumentException("Type de fichier non autorisé. Seules les images (JPEG, PNG, GIF) sont acceptées.");
        }
    }

    public Gallery saveImage(Gallery imageVoiture) {
        return imageVoitureRepository.save(imageVoiture);
    }
}
