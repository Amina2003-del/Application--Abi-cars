package location_voiture.persistence.model;

public class RapportRequest {
    private String type;
    private String startDate;
    private String endDate;

    // Getters & setters obligatoires !
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}
