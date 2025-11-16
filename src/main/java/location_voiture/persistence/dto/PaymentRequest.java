package location_voiture.persistence.dto;

public class PaymentRequest {
    private Long carId;
    private int days;
    private double totalPrice;

    // Getters et Setters
    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}