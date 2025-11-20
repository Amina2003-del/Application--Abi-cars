package location_voiture.persistence.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiements")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double montant;
    @OneToOne(mappedBy = "paiement", fetch = FetchType.LAZY)
    private Facture facture;
   

    @Column(nullable = false)
    private LocalDate date = LocalDate.now();

    @Column(nullable = false)
    private String methode; // "Carte", "Virement", "PayPal", etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiement statut = StatutPaiement.EN_ATTENTE;

    // Relation avec Reservation (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    // Constructeurs

    public Paiement(Double montant, String methode, Reservation reservation) {
        this.montant = montant;
        this.methode = methode;
        this.reservation = reservation;
    }
    public String getReferencePaiement() {
        return "PAY-" + this.date.getYear() + "-" + String.format("%06d", this.id);
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public Double getMontant() {
        return montant;
    }
    
    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMethode() {
        return methode;
    }
    public Paiement() {}

    public void setMethode(String methode) {
        this.methode = methode;
    }

    public StatutPaiement getStatut() {
        return statut;
    }

    public void setStatut(StatutPaiement statut) {
        this.statut = statut;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    // Méthodes métier
    public void confirmerPaiement() {
        if (this.statut == StatutPaiement.EN_ATTENTE) {
            this.statut = StatutPaiement.PAYE;
        }
    }

    public void annulerPaiement() {
        if (this.statut == StatutPaiement.EN_ATTENTE) {
            this.statut = StatutPaiement.ANNULE;
        }
    }
  

	public void setReservation(Locataire locataire) {
		// TODO Auto-generated method stub
		
	}

	public void setReservations(Object object) {
		// TODO Auto-generated method stub
		
	}
	
	public void setPaymentMethod(String paymentMethod) {
this.methode=		paymentMethod;
	}

	

	public Paiement orElse(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	public LocalDate getDatePaiement() {
	    return this.date; // <-- renvoie le champ réel
	}

	
	
	public String getMethodePaiement() {
	    return this.methode;
	}

	public String getModePaiement() {
	    return this.methode;
	}


	



}