package location_voiture.service;

import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Entretien;
import location_voiture.persistence.model.Propritaire;
import location_voiture.repository.CarRepository;
import location_voiture.repository.EntretienRepository;
import location_voiture.persistence.model.StatutEntretien;
import location_voiture.persistence.model.StatutTechnique;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EntretienService {

    @Autowired
    private EntretienRepository entretienRepository;

    @Autowired
    private CarRepository carRepository;
    // Ajouter un entretien
    public Entretien saveEntretien(Entretien entretien) {
        return entretienRepository.save(entretien);
    }

    // Lister les entretiens d'une voiture
    public List<Entretien> getEntretiensByCar(Car car) {
        return entretienRepository.findByCar(car);
    }

    // Trouver un entretien par ID
    public Optional<Entretien> getEntretienById(Long id) {
        return entretienRepository.findById(id);
    }

    // Supprimer un entretien
    public void deleteEntretien(Long id) {
        entretienRepository.deleteById(id);
    }
    public void ajouterEntretien(Long carId, String type, String dateDebutStr, String dateFin, Double cout,
            String observations, Boolean periodique, Integer prochainKmEstime,
            String prochaineDateEstimee, MultipartFile invoice) throws IOException {

// 🔹 1. Récupérer la voiture
Car car = carRepository.findById(carId)
.orElseThrow(() -> new IllegalArgumentException("Voiture non trouvée avec l'ID : " + carId));

// 🔹 2. Créer un nouvel entretien
Entretien entretien = new Entretien();
entretien.setCar(car);
entretien.setType(type);

if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
entretien.setDateDebut(LocalDate.parse(dateDebutStr));
}

if (dateFin != null && !dateFin.isEmpty()) {
entretien.setDateFin(LocalDate.parse(dateFin));
}

entretien.setCout(cout);
entretien.setObservations(observations);
entretien.setPeriodique(periodique != null ? periodique : false);
entretien.setProchainKmEstime(prochainKmEstime);

if (prochaineDateEstimee != null && !prochaineDateEstimee.isEmpty()) {
entretien.setProchaineDateEstimee(LocalDate.parse(prochaineDateEstimee));
}

// 🔹 3. Gestion du fichier facture
if (invoice != null && !invoice.isEmpty()) {
String fileName = System.currentTimeMillis() + "_" + invoice.getOriginalFilename();
Path filePath = Paths.get("uploads/factures", fileName);
Files.createDirectories(filePath.getParent());
Files.write(filePath, invoice.getBytes());
entretien.setFichierFacturePath(filePath.toString());
}

// 🔹 4. Calculer le statut automatiquement
StatutEntretien statut = calculerStatut(entretien);
entretien.setStatut(statut);

// 🔹 5. Enregistrer l’entretien
entretienRepository.save(entretien);

// 🔹 6. Mettre à jour le statut technique de la voiture selon le statut calculé
if (statut == StatutEntretien.EN_RETARD || statut == StatutEntretien.A_VENIR) {
car.setStatutTechnique(StatutTechnique.EN_ENTRETIEN);
} else if (statut == StatutEntretien.TERMINE) {
car.setStatutTechnique(StatutTechnique.DISPONIBLE);
}

carRepository.save(car);

System.out.println("✅ Entretien ajouté avec statut : " + statut +
" | Voiture mise à jour avec statut technique : " + car.getStatutTechnique());
}


    public StatutEntretien calculerStatut(Entretien entretien) {
        LocalDate aujourdhui = LocalDate.now();

        if (entretien.getDateFin() != null) {
            // Entretien terminé
            return StatutEntretien.TERMINE;
        }

        if (entretien.getDateDebut().isBefore(aujourdhui)) {
            // Date prévue est passée, entretien non terminé → en retard
            return StatutEntretien.EN_RETARD;
        }

        // Sinon, entretien à venir
        return StatutEntretien.A_VENIR;
    }

	
	

	 public List<Entretien> getEntretiensByProprietaire(Long proprietaireId) {
	        return entretienRepository.findByVoitureProprietaireId(proprietaireId);
	    }
	    


}
