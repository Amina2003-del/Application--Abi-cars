package location_voiture.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import location_voiture.persistence.dto.PaiementDTO;
import location_voiture.persistence.dto.RevenuParVoitureDTO;
import location_voiture.persistence.model.Facture;
import location_voiture.persistence.model.Paiement;
import location_voiture.persistence.model.Réservation;
import location_voiture.persistence.model.StatutPaiement;
import location_voiture.persistence.model.StatutReservation;
import location_voiture.repository.FactureRepository;
import location_voiture.repository.PaiementRepository;
import location_voiture.repository.ReservationRepository;

@Service
public class PaiementService {

    @Autowired
    private PaiementRepository paiementRepository;
    @Autowired
    private FactureRepository factureRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PdfFactureGenerator pdfFactureGenerator;

    @Autowired
    private EmailService emailService;
    /**
     * Crée un paiement avec les détails fournis.
     * @param montant Le montant du paiement
     * @param methode La méthode de paiement (ex. "PayPal", "Cheque")
     * @param reservation La réservation associée
     * @return Le paiement créé
     */
    public Paiement creerPaiement(Double montant, String methode, Réservation reservation) {
        if (montant == null || montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif.");
        }
        if (methode == null || methode.trim().isEmpty()) {
            throw new IllegalArgumentException("La méthode de paiement est requise.");
        }
        if (reservation == null) {
            throw new IllegalArgumentException("Une réservation est requise.");
        }

        Paiement paiement = new Paiement(montant, methode, reservation);
        return paiementRepository.save(paiement);
    }
    public List<Paiement> getAllPaiementes() {
        return paiementRepository.findAll();
    }

    
    public PaiementService(PaiementRepository paiementRepository) {
        this.paiementRepository = paiementRepository;
    }

    public List<RevenuParVoitureDTO> getRevenusParVoiture(Long proprietaireId) {
        return paiementRepository.getRevenusParVoiture(proprietaireId);

    }
    /**
     * Crée un paiement par chèque avec un numéro de chèque.
     * @param montant Le montant du paiement
     * @param reservation La réservation associée
     * @param numeroCheque Le numéro du chèque
     * @return Le paiement créé
     */
   
    /**
     * Confirme un paiement existant.
     * @param paiementId L'ID du paiement à confirmer
     */
    @Transactional
    public void confirmerPaiement(Long paiementId) {
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new IllegalArgumentException("Paiement non trouvé avec ID: " + paiementId));
        
        if (paiement.getStatut() == StatutPaiement.PAYE) {
            System.out.println("Paiement déjà confirmé.");
            return;
        }
        
        if (paiement.getStatut() != StatutPaiement.EN_ATTENTE) {
            throw new IllegalStateException("Le paiement ne peut être confirmé car son statut est : " + paiement.getStatut());
        }
        
        // Confirmer le paiement
        paiement.confirmerPaiement();
        
        // Mettre à jour la réservation
        Réservation reservation = paiement.getReservation();
        if (reservation == null) {
            throw new IllegalStateException("La réservation associée au paiement est introuvable.");
        }
        reservation.setStatut(StatutReservation.CONFIRMEE);
        
        // Sauvegarder la réservation mise à jour
        reservation = reservationRepository.save(reservation);
        System.out.println("Réservation ID " + reservation.getId() + " mise à jour avec statut CONFIRMEE.");
        
        // Mettre à jour la facture liée
        Optional<Facture> factureOpt = factureRepository.findByReservationId(reservation.getId());
        if (factureOpt.isPresent()) {
            Facture facture = factureOpt.get();
            facture.setStatut("payée");
            facture.setReferencePaiement(paiement.getId().toString());
            factureRepository.save(facture);
            System.out.println("Facture ID " + facture.getId() + " mise à jour avec statut PAYEE et référence paiement.");
        } else {
            System.err.println("Aucune facture trouvée pour réservation ID = " + reservation.getId());
            // Optionnel : créer une facture ici si nécessaire
        }
        
        // Sauvegarder le paiement
        paiementRepository.save(paiement);
        System.out.println("Paiement ID " + paiement.getId() + " confirmé et sauvegardé.");
    }



    /*public List<PaiementDTO> getAllPaiementsWithDetails() {
        return paiementRepository.findAllPaiementsWithDetails();
    }
  */
    public void annulerPaiement(Long paiementId) {
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new IllegalArgumentException("Paiement non trouvé avec ID: " + paiementId));
        if (paiement.getStatut() != StatutPaiement.EN_ATTENTE) {
            throw new IllegalStateException("Le paiement ne peut être annulé car son statut est : " + paiement.getStatut());
        }
        paiement.annulerPaiement();
        paiementRepository.save(paiement);
    }

public Paiement findById(Long id) {
    return paiementRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Paiement introuvable avec l'ID : " + id));
}

public double getTotalRevenueForCurrentMonth() {
    YearMonth currentMonth = YearMonth.now(); // Mois courant (par exemple, mai 2025)
    LocalDate startOfMonth = currentMonth.atDay(1);
    LocalDate endOfMonth = currentMonth.atEndOfMonth();

    List<Paiement> paiements = paiementRepository.findByStatutAndDateBetween(
            StatutPaiement.VALIDE, startOfMonth, endOfMonth);

    return paiements.stream()
            .mapToDouble(Paiement::getMontant)
            .sum();
}

public void processPayment(String carId, int days, double totalPrice) {
	// TODO Auto-generated method stub
	
}
public Paiement save(Paiement paiement) {
    return paiementRepository.save(paiement);
}
public Object getTotalSpent() {
	// TODO Auto-generated method stub
	return null;
}

public List<Paiement> getPaiementsByProprietaireId(Long proprietaireId) {
    return paiementRepository.findByReservationVoitureProprietaireId(proprietaireId);
}
public List<Paiement> findAll() {
    return paiementRepository.findAll();
}


public void refundPaiement(Long id) {
    Paiement paiement = paiementRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Paiement non trouvé"));
    if (paiement.getStatut() == StatutPaiement.PAYE) {
        paiement.setStatut(StatutPaiement.REMBOURSE);
        paiementRepository.save(paiement);
    } else {
        throw new IllegalStateException("Le paiement ne peut pas être remboursé");
    }
}
public PaiementDTO toDto(Paiement paiement) {
    if (paiement == null) return null;

    PaiementDTO dto = new PaiementDTO();

    dto.setId(paiement.getId());
    dto.setMontant(paiement.getMontant());
    dto.setMethode(paiement.getMethode());

    if (paiement.getDate() != null) {
        dto.setDate(paiement.getDate().toString());  // Ou formatter selon besoin
    } else {
        dto.setDate("Date inconnue");
    }

    dto.setStatut(paiement.getStatut() != null ? paiement.getStatut().name() : "INCONNU");

    if (paiement.getReservation() != null) {
        var reservation = paiement.getReservation();

        // Récupération nom complet client
        if (reservation.getUtilisateur() != null) {
            String firstName = reservation.getUtilisateur().getFirstName();
            String lastName = reservation.getUtilisateur().getLastName();
            String clientNom = ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
            if(clientNom.isEmpty()) clientNom = "Client inconnu";
            dto.setClientNom(clientNom);
        } else {
            dto.setClientNom("Client inconnu");
        }

        // Récupération marque + modèle voiture
        if (reservation.getVoiture() != null) {
            String marque = reservation.getVoiture().getMarque();
            String modele = reservation.getVoiture().getModele();
            String voitureMarque = ((marque != null ? marque : "") + " " + (modele != null ? modele : "")).trim();
            if(voitureMarque.isEmpty()) voitureMarque = "Voiture inconnue";
            dto.setVoitureMarque(voitureMarque);
        } else {
            dto.setVoitureMarque("Voiture inconnue");
        }
    } else {
        dto.setClientNom("Client inconnu");
        dto.setVoitureMarque("Voiture inconnue");
    }

    return dto;


        // Si tu souhaites, tu peux aussi convertir la réservation complète en DTO:
        // dto.setReservation(ReservationDTO.fromEntity(reservation));
  
}


public boolean retryPaiement(Long id) {
    // Ta logique ici : appel API paiement, mise à jour statut, etc.
    return true;
}

public boolean rembourserPaiement(Long id) {
    // Ta logique ici : remboursement, mise à jour statut, etc.
    return true;
}

}