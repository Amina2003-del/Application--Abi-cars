package location_voiture.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import location_voiture.persistence.dto.DocumentDTO;
import location_voiture.persistence.dto.HistoriqueDTO;
import location_voiture.persistence.dto.LitigeDTO;
import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Litige;
import location_voiture.persistence.model.Propritaire;
import location_voiture.persistence.model.Réservation;
import location_voiture.persistence.model.StatutLitige;
import location_voiture.persistence.model.TypeLitige;
import location_voiture.repository.LitigeRepository;
import location_voiture.repository.ReservationRepository;
import ma.abisoft.persistence.dao.UserRepository;
import ma.abisoft.persistence.model.User;
@Service
public class LitigeService {

	 private final LitigeRepository litigeRepository;
	    private final ReservationRepository reservationRepository;
	    private final UserRepository userRepository;
	  
	    @Autowired

		  private ProprietaireFileService proprietaireFileService; 
	   
	    // Dossier pour enregistrer les fichiers localement (à adapter)

	    public LitigeService(LitigeRepository litigeRepository,
	                         ReservationRepository reservationRepository,
	                         UserRepository userRepository) {
	        this.litigeRepository = litigeRepository;
	        this.reservationRepository = reservationRepository;
	        this.userRepository = userRepository;
	    }
	    public Litige creerLitige(Long reservationId, TypeLitige type, String description,
	            MultipartFile[] attachments, Long userId) throws Exception {

	        // Récupération de la réservation
	        Réservation reservation = reservationRepository.findById(reservationId)
	            .orElseThrow(() -> new Exception("Réservation non trouvée"));

	        // Récupération de l'utilisateur qui crée le litige
	        User utilisateur = userRepository.findById(userId)
	            .orElseThrow(() -> new Exception("Utilisateur non trouvé"));

	        // Récupération du propriétaire à partir de la voiture liée à la réservation
	        Propritaire proprietaire = reservation.getVoiture().getProprietaire();
	        if (proprietaire != null && proprietaire.getUser() != null) {
	            User userProprietaire = proprietaire.getUser();
	            // Maintenant tu peux accéder à l'email ou autres infos de User
	            String email = userProprietaire.getEmail();
	        }
	        if (proprietaire == null) {
	            throw new Exception("Propriétaire de la voiture introuvable");
	        }
	        String emailProprietaire = proprietaire.getUser().getEmail();
	        if (emailProprietaire == null || emailProprietaire.trim().isEmpty()) {
	            throw new Exception("Email du propriétaire est vide ou introuvable");
	        }

	        // Création du litige
	        Litige litige = new Litige();
	        litige.setReservation(reservation);
	        litige.setType(type);
	        litige.setDescription(description);
	        litige.setDateCreation(LocalDateTime.now());
	        litige.setStatut(StatutLitige.OUVERT);
	        litige.setUtilisateur(utilisateur);

	        // Traitement des pièces jointes
	        List<String> attachmentsPaths = new ArrayList<>();
	        if (attachments != null && attachments.length > 0) {
	            Long proprietaireId = reservation.getVoiture().getProprietaire().getId();  // Récupère ID proprio via réservation/voiture
	            if (proprietaireId == null) {
	                System.err.println("❌ Erreur : Propriétaire non associé à la réservation pour litiges");
	            } else {
	                attachmentsPaths = proprietaireFileService.saveLitigeAttachments(attachments, proprietaireId, reservation.getId().toString());
	            }
	        }

	        if (!attachmentsPaths.isEmpty()) {
	            litige.setAttachmentPath(String.join(";", attachmentsPaths));
	        }

	        // Sauvegarde du litige en base
	        Litige litigeSauvegarde = litigeRepository.save(litige);

	        // Log pour vérification
	        System.out.println("Litige destiné au propriétaire : " + emailProprietaire);

	        // Envoi du mail au propriétaire (à décommenter quand mailService est prêt)
	        // mailService.envoyerNotificationLitige(emailProprietaire, litigeSauvegarde);

	        return litigeSauvegarde;
	    }


	    public List<Litige> getLitigesByUtilisateur(User utilisateur) {
	        return litigeRepository.findByReservationUtilisateur(utilisateur);
	    }

	    public Optional<Litige> getLitigeById(Long id) {
	        return litigeRepository.findById(id);
	    }
		
		
	    public Map<Integer, Integer> getLitigesParMois() {
	        int anneeActuelle = Year.now().getValue();
	        List<Object[]> results = litigeRepository.countLitigesParMois(anneeActuelle);

	        Map<Integer, Integer> litigesParMois = new HashMap<>();
	        for (Object[] row : results) {
	            Integer mois = (Integer) row[0];
	            Long count = (Long) row[1];
	            litigesParMois.put(mois, count.intValue());
	        }

	        return litigesParMois;
	    }
		 public LitigeDTO getLitigeDetails(Long id) {
        Litige litige = litigeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Litige introuvable"));

        LitigeDTO dto = new LitigeDTO(litige);
        dto.setId(litige.getId());
        dto.setDescription(litige.getDescription());
        dto.setStatut(litige.getStatut());

        // Historique fictif pour l'exemple
        List<HistoriqueDTO> historique = List.of(
                new HistoriqueDTO("2024-12-01", "Création du litige"),
                new HistoriqueDTO("2024-12-03", "Réponse du support")
        );
        dto.setHistorique(historique);

        // Documents fictifs (à adapter à vos entités réelles)
        List<DocumentDTO> documents = litige.getDocuments().stream().map(doc -> {
            DocumentDTO d = new DocumentDTO();
            d.setNomFichier(doc.getNom());
            return d;
        }).collect(Collectors.toList());
        dto.setDocuments(documents);

        return dto;
    }
		public void notifyClient(Long id) {
			// TODO Auto-generated method stub
			
		}
		public void uploadDocument(Long id, MultipartFile file) {
			// TODO Auto-generated method stub
			
		}
		public void resolveLitige(Long id) {
			// TODO Auto-generated method stub
			
		}
		public void addNote(Long id, Object note, Object statut) {
			// TODO Auto-generated method stub
			
		}
		
		 public void addNoteAndUpdateStatus(Long id, String note, String statutStr) {
			    Litige litige = litigeRepository.findById(id)
			        .orElseThrow(() -> new EntityNotFoundException("Litige introuvable"));

			    // Ajouter la note
			    litige.addNote(note);

			    // Convertir String en enum StatutLitige (en majuscule)
			    StatutLitige statut;
			    try {
			        statut = StatutLitige.valueOf(statutStr.toUpperCase());
			    } catch (IllegalArgumentException e) {
			        throw new RuntimeException("Statut invalide : " + statutStr);
			    }

			    // Mettre à jour le statut
			    litige.setStatut(statut);

			    litigeRepository.save(litige);
			}
		public void addNoteAndUpdateStatus(Long id, String note, StatutLitige nouveauStatut) {
			// TODO Auto-generated method stub
			
		}
		

		public Litige save(Litige litige) {
		    return litigeRepository.save(litige);
		}
		public List<Litige> findFiltered(String search, String statut, LocalDate dateStart, LocalDate dateEnd) {
		    List<Litige> allLitiges = litigeRepository.findAll();

		    return allLitiges.stream()
		        .filter(l -> {
		            boolean matchesSearch = true;
		            boolean matchesStatut = true;
		            boolean matchesDateStart = true;
		            boolean matchesDateEnd = true;

		            if (search != null && !search.isEmpty()) {
		                String lowerSearch = search.toLowerCase();

		                boolean typeMatch = l.getType() != null 
		                    && l.getType().name().toLowerCase().contains(lowerSearch);

		                boolean statutMatch = l.getStatut() != null 
		                    && l.getStatut().name().toLowerCase().contains(lowerSearch);

		                boolean descriptionMatch = l.getDescription() != null 
		                    && l.getDescription().toLowerCase().contains(lowerSearch);

		                boolean vehicleMatch = l.getReservation() != null 
		                    && l.getReservation().getVoiture() != null 
		                    && (l.getReservation().getVoiture().getMarque() + " " + l.getReservation().getVoiture().getModele())
		                        .toLowerCase()
		                        .contains(lowerSearch);

		                boolean clientMatch = l.getReservation() != null
		                    && l.getReservation().getUtilisateur() != null
		                    && (
		                        (l.getReservation().getUtilisateur().getFirstName() != null 
		                            && l.getReservation().getUtilisateur().getFirstName().toLowerCase().contains(lowerSearch))
		                        || (l.getReservation().getUtilisateur().getLastName() != null 
		                            && l.getReservation().getUtilisateur().getLastName().toLowerCase().contains(lowerSearch))
		                    );

		                matchesSearch = typeMatch || statutMatch || descriptionMatch || vehicleMatch || clientMatch;
		            }

		            if (statut != null && !statut.isEmpty()) {
		                try {
		                    StatutLitige filtreStatut = StatutLitige.valueOf(statut.toUpperCase());
		                    matchesStatut = filtreStatut.equals(l.getStatut());
		                } catch (IllegalArgumentException e) {
		                    matchesStatut = true; // ignore filtre invalide
		                }
		            }

		            if (dateStart != null) {
		                matchesDateStart = !l.getDateCreation().toLocalDate().isBefore(dateStart);
		            }

		            if (dateEnd != null) {
		                matchesDateEnd = !l.getDateCreation().toLocalDate().isAfter(dateEnd);
		            }

		            return matchesSearch && matchesStatut && matchesDateStart && matchesDateEnd;
		        })
		        .collect(Collectors.toList());
		}
		public List<Litige> findAll() {
	        return litigeRepository.findAll();
	    }

	    public Optional<Litige> findById(Long id) {
	        return litigeRepository.findById(id);
	    }

	    @Transactional
	    public Optional<Litige> resoudreLitige(Long id, String resolution) {
	        return litigeRepository.findById(id)
	                .map(litige -> {
	                    litige.resoudreLitige(resolution);
	                    return litigeRepository.save(litige);
	                });
	    }

	    @Transactional
	    public Optional<Litige> fermerLitige(Long id) {
	        return litigeRepository.findById(id)
	                .map(litige -> {
	                    litige.fermerLitige();
	                    return litigeRepository.save(litige);
	                });
	    }

}
