package location_voiture.persistence.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import ma.abisoft.persistence.model.User;

@Entity
public class Facture {

	

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    @ManyToOne
	    private User  utilisateur;
	    @OneToOne
	    @JoinColumn(name = "reservation_id", nullable = false)
	    private Réservation reservation; // Relation avec l'entité Réservation

	    @Column(nullable = false)
	    private Date dateEmission; // Date de création de la facture

	    @Column(nullable = false)
	    private String statut; // Statut de la facture (ex: 'payée', 'non payée', 'en attente')

	    @Column(nullable = false)
	    private Date dateLimite; // Date limite de paiement

	    @ManyToOne
	    @JoinColumn(name = "client_id", nullable = false)
	    private User client; // Relation avec l'entité User (le client)

	    @Column(nullable = true)
	    private String modePaiement; // Mode de paiement (ex: 'PayPal', 'Carte bancaire', etc.)

	    @Column(nullable = true)
	    private String referencePaiement; // Référence de paiement si disponible

	    @Lob
	    private byte[] facturePdf; // Facture en PDF (enregistrer sous forme binaire)

	    // Getters et setters

	    public Long getId() {
	        return id;
	    }
	    public void setUtilisateur(User utilisateur) {
	        this.utilisateur = utilisateur;
	    }

	    public User getUtilisateur() {
	        return this.utilisateur;
	    }


	    public void setId(Long id) {
	        this.id = id;
	    }

	    public Réservation getReservation() {
	        return reservation;
	    }

	    public void setReservation(Réservation reservation) {
	        this.reservation = reservation;
	    }

	    @OneToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "paiement_id")
	    private Paiement paiement;

	    // getter
	    public Paiement getPaiement() {
	        return paiement;
	    }

	    // setter
	    public void setPaiement(Paiement paiement) {
	        this.paiement = paiement;
	    }

	  

	    public Date getDateEmission() {
	        return dateEmission;
	    }

	    public void setDateEmission(Date dateEmission) {
	        this.dateEmission = dateEmission;
	    }

	    public String getStatut() {
	        return statut;
	    }

	    public void setStatut(String statut) {
	        this.statut = statut;
	    }

	    public Date getDateLimite() {
	        return dateLimite;
	    }

	    public void setDateLimite(Date dateLimite) {
	        this.dateLimite = dateLimite;
	    }

	    public User getClient() {
	        return client;
	    }

	    public void setClient(User client) {
	        this.client = client;
	    }

	    public String getModePaiement() {
	        return modePaiement;
	    }

	    public void setModePaiement(String modePaiement) {
	        this.modePaiement = modePaiement;
	    }

	    public String getReferencePaiement() {
	        return referencePaiement;
	    }

	    public void setReferencePaiement(String referencePaiement) {
	        this.referencePaiement = referencePaiement;
	    }

	    public byte[] getFacturePdf() {
	        return facturePdf;
	    }

	    public void setFacturePdf(byte[] facturePdf) {
	        this.facturePdf = facturePdf;
	    }

		public void setNomFichier(String factureFilename) {
			// TODO Auto-generated method stub
			
		}

		public Object getMontant() {
			// TODO Auto-generated method stub
			return null;
		}
	}
