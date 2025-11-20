package ma.abisoft.persistence.model;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import location_voiture.persistence.model.Alert;
import location_voiture.persistence.model.Avis;
import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Litige;
import location_voiture.persistence.model.Locataire;
import location_voiture.persistence.model.Message;
import location_voiture.persistence.model.RoleUtilisateur;
import location_voiture.persistence.model.Reservation;

import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity(name = "user_account")
@Table
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public class User{
	@Enumerated(EnumType.STRING) private RoleUtilisateur role;
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

   

    public String getDisplayName1() {
        return (firstName != null && lastName != null) 
            ? firstName + " " + lastName 
            : email != null ? email : "Utilisateur inconnu";
    }
   // "GOOGLE", "LOCAL", etc.
   

    
    public Double getPrixMinVoiture() {
        if (voitures == null || voitures.isEmpty()) {
            return null; // ou 0, selon ton besoin
        }
        return voitures.stream()
                      .map(Car::getPrixJournalier)
                      .min(Double::compare)
                      .orElse(null);
    }
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Locataire locataire;

    public Locataire getLocataire() {
        return locataire;
    }

    public void setLocataire(Locataire locataire) {
        this.locataire = locataire;
    }

   
    private String tel;

    @Column(length = 60)
    private String password;

    private boolean enabled;

    private boolean isUsing2FA;

    private String secret;

    @OneToMany(mappedBy = "auteur")
    @JsonIgnore
    private Collection<Avis> avisRecus;

    @OneToMany(mappedBy = "utilisateur")
    private Collection<Message> messagesEnvoyes;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    public User() {
        super();
        this.secret = Base32.random();
        this.enabled = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }
    
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(final Collection<Role> roles) {
        this.roles = roles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isUsing2FA() {
        return isUsing2FA;
    }

    public void setUsing2FA(boolean isUsing2FA) {
        this.isUsing2FA = isUsing2FA;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Collection<Avis> getAvisRecus() {
    	
        return avisRecus;
    }

    public void setAvisRecus(Collection<Avis> avisRecus) {
        this.avisRecus = avisRecus;
    }

    public Collection<Message> getMessagesEnvoyes() {
        return messagesEnvoyes;
    }

    public void setMessagesEnvoyes(Collection<Message> messagesEnvoyes) {
        this.messagesEnvoyes = messagesEnvoyes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User user = (User) obj;
        if (email == null) {
            if (user.email != null) {
                return false;
            }
        } else if (!email.equals(user.email)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("User [id=").append(id).append(", firstName=").append(firstName).append(", lastName=").append(lastName).append(", email=").append(email).append(", password=").append(password).append(", enabled=").append(enabled).append(", isUsing2FA=")
                .append(isUsing2FA).append(", secret=").append(secret).append(", roles=").append(roles).append("]");
        return builder.toString();
    }

	public void removeAlertInternal(Alert alert) {
		// TODO Auto-generated method stub
		
	}

	public void addAlertInternal(Alert alert) {
		// TODO Auto-generated method stub
		
	}
	  public String getFullNameWithId() {
	        String fullName = "";
	        if (firstName != null) {
	            fullName += firstName;
	        }
	        if (lastName != null) {
	            if (!fullName.isEmpty()) {
	                fullName += " ";
	            }
	            fullName += lastName;
	        }
	        if (fullName.isEmpty()) {
	            fullName = "Utilisateur Inconnu"; // Ou une autre valeur par défaut
	        }
	        return fullName + " (#" + (id != null ? id : "N/A") + ")";
	    }

	

	public String getSujet() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCibleGroupe() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getUtilisateur() {
		// TODO Auto-generated method stub
		return null;
	}

	public LocalDateTime getDateEnvoi() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isEnvoyeAvecSucces() {
		// TODO Auto-generated method stub
		return false;
	}
	
	  public String getPhoneNumber() {
	        return this.tel;
	    }


	    // ========================================================================
	    // === AJOUTER OU VÉRIFIER CETTE MÉTHODE ===
	    // ========================================================================
	    /**
	     * Retourne un nom d'affichage pour l'utilisateur, basé sur le prénom et le nom.
	     * Utilise l'email comme fallback si le prénom et le nom ne sont pas disponibles.
	     * En dernier recours, retourne "Utilisateur [ID]" ou "Utilisateur Inconnu".
	     *
	     * @return Le nom d'affichage de l'utilisateur.
	     */
	    public String getDisplayName() {
	        StringBuilder displayNameBuilder = new StringBuilder();

	        // Ajouter le prénom s'il existe
	        if (this.firstName != null && !this.firstName.trim().isEmpty()) {
	            displayNameBuilder.append(this.firstName.trim());
	        }

	        // Ajouter le nom de famille s'il existe, avec un espace si le prénom a été ajouté
	        if (this.lastName != null && !this.lastName.trim().isEmpty()) {
	            if (displayNameBuilder.length() > 0) {
	                displayNameBuilder.append(" "); // Ajoute un espace entre prénom et nom
	            }
	            displayNameBuilder.append(this.lastName.trim());
	        }

	        // Si le nom construit est toujours vide (ni prénom ni nom fournis)
	        if (displayNameBuilder.length() == 0) {
	            if (this.email != null && !this.email.trim().isEmpty()) {
	                return this.email.trim(); // Utiliser l'email comme fallback
	            } else if (this.id != null) {
	                return "Utilisateur " + this.id; // Fallback à "Utilisateur [ID]"
	            } else {
	                return "Utilisateur Inconnu"; // Fallback ultime
	            }
	        }

	        return displayNameBuilder.toString();
	    }
	    // ========================================================================
	    // === FIN DE LA MÉTHODE À AJOUTER/VÉRIFIER ===
	    // ========================================================================

		public void setAvisList(List<Avis> avis) {
			// TODO Auto-generated method stub
			
		}

		public void setReservationList(List<Reservation> reservations) {
			// TODO Auto-generated method stub
			
		}

		

		public User orElseGet(Object object) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getNom() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getNomComplet() {
		    StringBuilder sb = new StringBuilder();
		    if (firstName != null && !firstName.trim().isEmpty()) {
		        sb.append(firstName.trim());
		    }
		    if (lastName != null && !lastName.trim().isEmpty()) {
		        if (sb.length() > 0) {
		            sb.append(" ");
		        }
		        sb.append(lastName.trim());
		    }
		    if (sb.length() == 0) {
		        if (email != null) {
		            return email;
		        } else {
		            return "Utilisateur Inconnu";
		        }
		    }
		    return sb.toString();
		}

		public void setNom(Object nom) {
			// TODO Auto-generated method stub
			
		}

		public void setTelephone(Object telephone) {
			// TODO Auto-generated method stub
			
		}

		public void setRaisonsociale(String raisonsociale) {
			// TODO Auto-generated method stub
			
		}

		public void setICE(String ice) {
			// TODO Auto-generated method stub
			
		}

		public void setFullName(Object fullName) {
			// TODO Auto-generated method stub
			
		}

		
		public void setUserType(Object userType) {
			// TODO Auto-generated method stub
			
		}

		public String getTelephone() {
		    return this.tel;  // renvoie la valeur réelle
		}

		public void setTelephone(String telephone) {
		    this.tel = telephone; // met à jour le champ réel
		}
		public String getFullName() {
			// TODO Auto-generated method stub
			return null;
		}

		public void setCustomUserType(Object userType) {
			// TODO Auto-generated method stub
			
		}

		public Object getCustomUserType() {
			// TODO Auto-generated method stub
			return null;
		}

	
		

		    public Collection<? extends GrantedAuthority> getAuthorities() {
		        if (roles == null) {
		            return List.of(); // Retourne une liste vide si aucun rôle n’est défini
		        }

		        return roles.stream()
		            .filter(role -> role != null && role.getName() != null)
		            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
		            .collect(Collectors.toList());
		    }


		public boolean isAccountNonExpired() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isAccountNonLocked() {
			// TODO Auto-generated method stub
			return false;
		}
		
		public boolean isCredentialsNonExpired() {
			// TODO Auto-generated method stub
			return false;
		}

		public User orElse(Object object) {
			// TODO Auto-generated method stub
			return null;
		}

		
		
		
		@OneToMany(mappedBy = "proprietaire")
		private List<Car> voitures;


		@OneToMany(mappedBy = "utilisateur")
		@JsonManagedReference
		private List<Reservation> reservations;



		public List<Car> getVoitures() {
		    return voitures;  // actuellement tu retournes null, corrige ici !
		}

		public List<Reservation> getReservations() {
		    return reservations;  // c'est ok
		}

	

		public Boolean getEnabled() {
		    return enabled;
		}

	

		public String getNumeroPermis() {
			// TODO Auto-generated method stub
			return null;
		}

		

		

		

		public List<Litige> getLitiges() {
			// TODO Auto-generated method stub
			return null;
		}

		public List<Avis> getAvis() {
		    if (avisRecus == null) {
		        return new ArrayList<>();
		    }
		    // Convertir la collection en List (si ce n’est pas déjà une List)
		    return new ArrayList<>(avisRecus);

		}

	

		public String getStatut() {
			// TODO Auto-generated method stub
			return null;
		}

		

		public Object getAvatarUrl() {
			// TODO Auto-generated method stub
			return null;
		}

		

public String getPhone() {
    return this.tel;
}

public Integer getVoituresCount() {
    if (voitures == null) return null;
    return voitures.size();
}



public Object getCreatedAt() {
	// TODO Auto-generated method stub
	return null;
}

public String getUsername() {
    // Ici, on peut considérer l'email comme identifiant unique
    return this.email;
}

public void setUsername(String username) {
    this.email = username;
}
public LocalDate getDerniereActivite() {
    if (reservations == null || reservations.isEmpty()) {
        return null;
    }
    return reservations.stream()
        .map(Reservation::getDateDebut)  // doît retourner LocalDateTime
        .filter(Objects::nonNull)
        .max(Comparator.naturalOrder())
        .orElse(null);
}
public RoleUtilisateur getRole() {
    return role;
}

public void setRole(RoleUtilisateur role) {
    this.role = role;
}

}

		


	    // Si vous avez toujours getFullNameWithId et que vous voulez la conserver:
	
	
	
	
	
	   
	
	
	
	
