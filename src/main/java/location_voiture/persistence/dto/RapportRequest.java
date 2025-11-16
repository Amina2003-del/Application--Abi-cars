package location_voiture.persistence.dto;

public class RapportRequest {
    private String type;
    private String startDate;
    private String endDate;
    public String getType() {
        return type;
    }

    // Setter pour type
    public void setType(String type) {
        this.type = type;
    }

    // Getter pour startDate
    public String getStartDate() {
        return startDate;
    }

    // Setter pour startDate
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    // Getter pour endDate
    public String getEndDate() {
        return endDate;
    }

    // Setter pour endDate
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }    // getters et setters
}
