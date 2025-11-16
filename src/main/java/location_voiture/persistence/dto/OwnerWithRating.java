package location_voiture.persistence.dto;

import location_voiture.persistence.model.Propritaire;
import ma.abisoft.persistence.model.User;

public class OwnerWithRating {
    private Propritaire owner;
    private double moyenneNote;
    private long totalAvis;

  
    public OwnerWithRating(Propritaire owner, Double moyenneNote, Long totalAvis) {
        this.owner = owner;
        this.moyenneNote = moyenneNote;
        this.totalAvis = totalAvis;
    }
    
    // Getters et setters
    public Propritaire getOwner() { return owner; }
    public void setOwner(Propritaire owner) { this.owner = owner; }

    public double getMoyenneNote() { return moyenneNote; }
    public void setMoyenneNote(double moyenneNote) { this.moyenneNote = moyenneNote; }

    public long getTotalAvis() { return totalAvis; }
    public void setTotalAvis(long totalAvis) { this.totalAvis = totalAvis; }

    public String getNoteCategory() {
        if (totalAvis == 0) {
            return "noNotes";
        }
        if (Double.isNaN(moyenneNote)) {
            return "noNotes";
        }
        if (moyenneNote >= 4) {
            return "excellent";
        }
        if (moyenneNote >= 2.5) {
            return "medium";
        }
        return "toImprove";
    }
}
