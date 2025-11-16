package location_voiture.persistence.model;


import javax.persistence.*;

@Entity
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName; // Nom original du fichier (par exemple, "contrat.pdf")

    @Lob // Indique que ce champ peut contenir de grandes données binaires
    private String contentType; // Type MIME du fichier (par exemple, "application/pdf")

    @Column
    private String filePath; 
    @ManyToOne
    @JoinColumn(name = "demande_id", nullable = false)
    private DemandePartenariat demande; // Relation avec la demande de partenariat

    // Constructeurs
    public Document() {
    }

    public Document(String fileName, byte[] fileContent, String contentType, DemandePartenariat demande) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.demande = demande;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

   
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public DemandePartenariat getDemande() {
        return demande;
    }

    public void setDemande(DemandePartenariat demande) {
        this.demande = demande;
    }

  

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

	public Document orElseThrow(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

}
