package location_voiture.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Disponibilite;

@Repository
public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Long> {

    List<Disponibilite> findByCarId(Long carId);

    
    // ✅ CORRECTION: Utilisez "d.car" et LocalDate
    @Query("SELECT d FROM Disponibilite d WHERE d.car = :car AND d.statut = 'réservé' AND " +
           "((d.dateDebut BETWEEN :dateDebut AND :dateFin) OR " +
           "(d.dateFin BETWEEN :dateDebut AND :dateFin) OR " +
           "(d.dateDebut <= :dateDebut AND d.dateFin >= :dateFin))")
    List<Disponibilite> findConflitsReservation(
            @Param("car") Car car,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);
    
    // ✅ Alternative plus simple
    @Query("SELECT d FROM Disponibilite d WHERE d.car = :car AND d.statut = 'réservé' AND " +
           "(d.dateDebut <= :dateFin AND d.dateFin >= :dateDebut)")
    List<Disponibilite> findConflitsReservationSimple(
            @Param("car") Car car,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    
    // Méthode alternative plus optimisée
   
    
        
        // CORRECTION: utiliser "statut" au lieu de "etat"
        @Query("SELECT COUNT(d) > 0 FROM Disponibilite d WHERE d.car = :voiture AND d.statut = 'Indisponible' " +
               "AND ((d.dateDebut BETWEEN :debut AND :fin) OR (d.dateFin BETWEEN :debut AND :fin) " +
               "OR (:debut BETWEEN d.dateDebut AND d.dateFin) OR (:fin BETWEEN d.dateDebut AND d.dateFin))")
        boolean existsIndisponibiliteChevauchante(
            @Param("voiture") Car voiture,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);
        
        // Méthode alternative pour debug
        @Query("SELECT d FROM Disponibilite d WHERE d.car = :voiture AND d.statut = 'Indisponible' " +
               "AND ((d.dateDebut BETWEEN :debut AND :fin) OR (d.dateFin BETWEEN :debut AND :fin) " +
               "OR (:debut BETWEEN d.dateDebut AND d.dateFin) OR (:fin BETWEEN d.dateDebut AND d.dateFin))")
        List<Disponibilite> findIndisponibilitesChevauchantes(
            @Param("voiture") Car voiture,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);
        
        // Récupérer toutes les indisponibilités d'une voiture
        List<Disponibilite> findByCarAndStatut(Car car, String statut);
    



}