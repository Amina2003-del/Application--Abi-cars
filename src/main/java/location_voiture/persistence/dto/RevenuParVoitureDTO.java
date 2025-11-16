package location_voiture.persistence.dto;

public class RevenuParVoitureDTO {
    private String carModel;
    private Double totalRevenue;
    private String carBrand; // Ajout de la marque
    private Long rentalCount;

    public RevenuParVoitureDTO(String carBrand, String carModel, double totalRevenue, long rentalCount) {
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.totalRevenue = totalRevenue;
        this.rentalCount = rentalCount;
    }

    // Getters et setters
    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getRentalCount() {
        return rentalCount;
    }

    public void setRentalCount(long rentalCount) {
        this.rentalCount = rentalCount;
    }}