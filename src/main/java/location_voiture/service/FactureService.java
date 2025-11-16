package location_voiture.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;

import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Facture;
import location_voiture.persistence.model.Paiement;
import location_voiture.persistence.model.Propritaire;
import location_voiture.persistence.model.Réservation;
import location_voiture.repository.FactureRepository;
import location_voiture.repository.PaiementRepository;
import location_voiture.repository.ReservationRepository;
@Service
public class FactureService {
	 @Autowired
	    private FactureRepository factureRepository;
	 @Autowired
	    private ReservationRepository reservationRepository;
	 @Autowired
	    private PdfFactureGenerator pdfGeneratorService; 
	 @Autowired

	  private ProprietaireFileService proprietaireFileService; 

	 @Autowired
	    private PaiementRepository paiementRepository;
	    @Autowired
	    private PdfFactureGenerator pdfFactureGenerator;

	    public Optional<Facture> findById(Long id) {
	        return factureRepository.findById(id);
	    }

	    public Facture save(Facture facture) {
	        return factureRepository.save(facture);
	    }

	    public void delete(Long id) {
	        factureRepository.deleteById(id);
	    }


	    public Facture findByReservationId(Long reservationId) {
	        System.out.println("Recherche facture avec reservation id = " + reservationId);
	        return factureRepository.findByReservationId(reservationId)
	                .orElse(null);
	    }
	  
	       // Pour sauvegarde physique

	       

	        public Facture creerFactureDepuisReservation(Réservation reservation) throws DocumentException, IOException {
	            LocalDate dateEmissionLocal = reservation.getDateDebut();  // Déplacé en dehors pour utilisation globale

	            Optional<Facture> optFacture = factureRepository.findByReservationId(reservation.getId());
	            Facture facture;

	            if (optFacture.isPresent()) {
	                facture = optFacture.get();
	                System.out.println("✅ Facture existante trouvée : " + facture.getId());
	            } else {
	                System.out.println("❌ Aucune facture trouvée pour réservation ID = " + reservation.getId());

	                facture = new Facture();
	                facture.setReservation(reservation);
	                facture.setClient(reservation.getUtilisateur());

	                Date dateEmission = Date.from(dateEmissionLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
	                facture.setDateEmission(dateEmission);

	                Date dateLimite = Date.from(dateEmissionLocal.plusDays(30).atStartOfDay(ZoneId.systemDefault()).toInstant());
	                facture.setDateLimite(dateLimite);

	                // On génère une référence paiement unique, ex. un UUID court ou un code personnalisé
	                String referencePaiementGeneree = "FP-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
	                facture.setReferencePaiement(referencePaiementGeneree);

	                // On récupère le mode de paiement si existant
	                Optional<Paiement> paiementOpt = paiementRepository.findByReservation(reservation);
	                if (paiementOpt.isPresent()) {
	                    Paiement paiement = paiementOpt.get();
	                    facture.setModePaiement(paiement.getMethode());
	                    facture.setStatut("payée");
	                    facture.setPaiement(paiement);  // Lie le paiement à la facture
	                } else {
	                    facture.setModePaiement("À définir");
	                    facture.setStatut("non payée");
	                }

	                // Sauvegarde temporaire en BD pour avoir un ID avant génération PDF
	                facture = factureRepository.save(facture);
	            }

	            // Générer PDF
	            byte[] pdfBytes = pdfFactureGenerator.genererFacturePDF(facture);
	            if (pdfBytes != null && pdfBytes.length > 0) {
	                facture.setFacturePdf(pdfBytes);
	                System.out.println("✅ PDF de facture généré et attaché (en BD).");

	                // Intégration sauvegarde physique (sans nouveau champ en BD)
	                try {
	                    // Récupérer l'ID du propriétaire via la réservation/voiture avec checks null renforcés
	                    Car voiture = reservation.getVoiture();
	                    if (voiture == null) {
	                        System.err.println("❌ Erreur sauvegarde physique PDF : Voiture non associée à la réservation (null)");
	                    } else {
	                        Propritaire proprietaire = voiture.getProprietaire();  // Entité Proprietaire séparée
	                        if (proprietaire == null) {
	                            System.err.println("❌ Erreur sauvegarde physique PDF : Propriétaire non associé à la voiture (null)");
	                        } else {
	                            Long proprietaireId = proprietaire.getId();
	                            if (proprietaireId == null) {
	                                System.err.println("❌ Erreur sauvegarde physique PDF : ID propriétaire null");
	                            } else {
	                                // Année et mois basés sur la date d'émission
	                                String annee = String.valueOf(dateEmissionLocal.getYear());
	                                String mois = String.format("%02d", dateEmissionLocal.getMonthValue());

	                                // Initialiser les dossiers si besoin
	                                proprietaireFileService.initProprietaireFolders(proprietaireId);

	                                // Sauvegarder le PDF physiquement dans /Static/uploads/proprietaire_ID/factures/annee/mois/facture_ID.pdf
	                                // (Le chemin n'est pas stocké en BD ; il peut être reconstruit via ID, dateEmission, etc.)
	                                String pdfPath = proprietaireFileService.saveGeneratedFacture(pdfBytes, proprietaireId, annee, mois, "facture_" + facture.getId() + ".pdf");
	                                System.out.println("✅ PDF sauvegardé physiquement : " + pdfPath + " (chemin reconstruit si besoin via ID et date).");
	                            }
	                        }
	                    }
	                } catch (Exception e) {
	                    System.err.println("❌ Erreur sauvegarde physique PDF : " + (e.getMessage() != null ? e.getMessage() : "Exception sans message (NPE probable)"));
	                    e.printStackTrace();  // Log stack pour debug détaillé
	                    // Continue sans crash ; le blob en BD est toujours là
	                }
	            } else {
	                System.err.println("❌ PDF non généré !");
	            }

	            System.out.println("Référence paiement : " + facture.getReferencePaiement());
	            return factureRepository.save(facture);  // Sauvegarde finale
	        }
	        }