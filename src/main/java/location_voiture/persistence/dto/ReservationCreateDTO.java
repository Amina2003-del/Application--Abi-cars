package location_voiture.persistence.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import location_voiture.persistence.model.TypeReservation;

public class ReservationCreateDTO {
    private Long carId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String pickupAddress;
    private String returnAddress;
    private TypeReservation typeReservation;


    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private String paymentMethod;
    private Double montant;
    private String chequeNumber;
    private String virementFileName;
    private String adresse;
    private String permis;

    // Getters & Setters
    public String getAdresse() {
        return adresse;
    }
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getPermis() {
        return permis;
    }
    public void setPermis(String permis) {
        this.permis = permis;
    }

    // Getters & Setters

    public Long getCarId() {
        return carId;
    }
    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }
    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getReturnAddress() {
        return returnAddress;
    }
    public void setReturnAddress(String returnAddress) {
        this.returnAddress = returnAddress;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getMontant() {
        return montant;
    }
    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public String getChequeNumber() {
        return chequeNumber;
    }
    public void setChequeNumber(String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }

    public String getVirementFileName() {
        return virementFileName;
    }
    public void setVirementFileName(String virementFileName) {
        this.virementFileName = virementFileName;
    }

    // Méthode utilitaire pour le nom complet
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
    public void setTypeReservation(TypeReservation typeReservation) {
        this.typeReservation = typeReservation;
    }
    public TypeReservation getTypeReservation() {
        return typeReservation;
    }

}
