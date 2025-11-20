package location_voiture.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import location_voiture.persistence.model.Message;
import location_voiture.persistence.model.Reservation;
import location_voiture.persistence.model.TypeMessage;
import ma.abisoft.persistence.model.User;
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	// Trouver les messages internes d'un locataire
    List<Message> findByReservationAndTypeOrderByDateEnvoiDesc(Reservation reservation, TypeMessage type);

    // Trouver les messages non lus d'un locataire
    List<Message> findByLuFalseAndReservationAndTypeOrderByDateEnvoiDesc(Reservation reservation, TypeMessage type);

    // Trouver tous les messages d'un type donné (notification ou interne)
    List<Message> findByTypeOrderByDateEnvoiDesc(TypeMessage type);
    
    long count();

	long countByLuFalse();


	List<Message> findTop3ByOrderByDateEnvoiDesc();
	
    List<Message> findByExpediteurIdOrDestinataireIdOrderByDateEnvoiDesc(Long expediteurId, Long destinataireId);

	List<Message> findByDestinataireId(Long id);

	long countByDestinataireIdAndLuFalse(Long id);
    List<Message> findByDestinataire(User destinataire);

    List<Message> findByDestinataireOrderByDateEnvoiDesc(User destinataire);


    List<Message> findByExpediteur(User user);

    Page<Message> findByDestinataireOrderByDateEnvoiDesc(User destinataire, Pageable pageable);
  
    List<Message> findByDestinataireAndDeletedFalse(User destinataire);



    @Query("SELECT m FROM Message m WHERE m.destinataire.email = :email "
            + "AND (:query IS NULL OR LOWER(m.sujet) LIKE %:query% "
            + "OR LOWER(m.expediteur.firstName) LIKE %:query% "
            + "OR LOWER(m.expediteur.lastName) LIKE %:query% "
            + "OR LOWER(m.expediteur.email) LIKE %:query%) "
            + "AND (:status IS NULL OR ( :status = 'lu' AND m.lu = true ) OR ( :status = 'non lu' AND m.lu = false ))")
    List<Message> searchMessagesByEmail(@Param("email") String email,
                                        @Param("query") String query,
                                        @Param("status") String status,
                                        Sort sort);

    @Query("SELECT m FROM Message m LEFT JOIN FETCH m.expediteur WHERE m.destinataire.id = :destinataireId ORDER BY m.dateEnvoi DESC")
    List<Message> findByDestinataireIdWithExpediteur(@Param("destinataireId") Long destinataireId);

 
    @Query("SELECT DISTINCT m FROM Message m " +
            "JOIN FETCH m.reservation r " +
            "JOIN FETCH r.voiture v " +
            "WHERE m.destinataire = :destinataire " +
            "AND v.proprietaire = :destinataire " +
            "ORDER BY m.dateEnvoi DESC")
     List<Message> findMessagesAvecReservationByProprietaire(@Param("destinataire") User destinataire); }
