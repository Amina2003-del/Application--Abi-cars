package ma.abisoft.persistence.dao;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import location_voiture.persistence.dto.OwnerWithRating;
import location_voiture.persistence.model.Propritaire;
import location_voiture.persistence.model.RoleUtilisateur;
import ma.abisoft.persistence.model.Role;
import ma.abisoft.persistence.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    @Override
    void delete(User user);

    List<User> findByEnabled(boolean b);

    List<User> findByRoles_NameAndEnabled(String string, boolean b);

    List<User> findByIdInAndEnabled(List<Long> ids, boolean b);

    List<User> findAllByRole(Role role);

    List<User> findByIdIn(List<Long> ids);

    // ✅ Corrected: roles moved to associated User
    @Query("SELECT p FROM Propritaire p JOIN p.user u JOIN u.roles r " +
           "WHERE r.name = :roleName AND p.descriptionAgence IS NOT NULL AND p.descriptionAgence <> ''")
    List<Propritaire> findUsersByRoleAndDescriptionNotEmpty(@Param("roleName") String roleName);

    Collection<? extends User> findAllByRole(RoleUtilisateur proprietaire);

    User findByEmailAndTel(String email, String tel);

    User findByEmailAndLastNameAndFirstNameAndTel(String email, String lastName, String firstName, String tel);

    @Query("SELECT COUNT(DISTINCT r.utilisateur) FROM Reservation r")
    long countClientsAyantLoue();

    Optional<User> findById(User userId);

    List<User> findByRole(RoleUtilisateur administrateur);

    List<User> findByRoles_Name(String roleName);

    @Query("SELECT u FROM user_account u WHERE u.role = :role")
    List<User> findByRoles(@Param("role") RoleUtilisateur role);

    User findByEmail(Object destinataireEmail);

    Optional<User> findByFirstNameAndLastName(String firstName, String lastName);

    @Query("SELECT DISTINCT r.utilisateur FROM Reservation r WHERE r.voiture.proprietaire.id = :ownerId")
    List<User> findDistinctClientsWithReservations(@Param("ownerId") Long ownerId);

    @Query("SELECT DISTINCT r.utilisateur FROM Reservation r")
    List<User> findAllDistinctClientsWithReservations();

    @Query("SELECT u.firstName, u.email FROM user_account u WHERE u.role = :role")
    List<Object[]> findUsersByRole(@Param("role") RoleUtilisateur role);

    @Query("SELECT DISTINCT r.utilisateur.firstName, r.utilisateur.email, r.dateDebut FROM Reservation r " +
           "WHERE r.dateDebut BETWEEN :start AND :end")
    List<Object[]> findUsersActivityBetween(LocalDate start, LocalDate end);

    boolean existsByEmail(String email);

    // ✅ Corrected: roles moved to associated User
    @Query("SELECT p FROM Propritaire p JOIN p.user u JOIN u.roles r WHERE r.name = :roleName")
    List<Propritaire> findByRoleName(@Param("roleName") String roleName);

    // ✅ Corrected: roles moved to associated User
    @Query("SELECT new location_voiture.persistence.dto.OwnerWithRating(" +
    	       "p, COALESCE(AVG(av.note), 0), COUNT(av.id)) " +  // Utiliser p (Propritaire) au lieu de u (User)
    	       "FROM Propritaire p " +
    	       "JOIN p.user u " +
    	       "JOIN u.roles r " +
    	       "JOIN Car v ON v.proprietaire = p " +
    	       "LEFT JOIN Avis av ON av.voiture = v " +
    	       "WHERE r.name = 'ROLE_OWNER' " +
    	       "GROUP BY p")  // Grouper par p (Propritaire) au lieu de u (User)
    	List<OwnerWithRating> findAllOwnersWithRatings();
}
