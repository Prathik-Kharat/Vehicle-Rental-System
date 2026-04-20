package com.vehiclerental.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "vehicles")
@Data @NoArgsConstructor @AllArgsConstructor
public class Vehicle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank private String brand;
    @NotBlank private String model;
    @Column(name = "registration_number", unique = true)
    private String registrationNumber;
    @Enumerated(EnumType.STRING) private VehicleType type;
    @Positive @Column(name = "price_per_day")
    private BigDecimal pricePerDay;
    @Enumerated(EnumType.STRING) private VehicleStatus status;
    private String imageUrl;
    private String description;
    private Integer seats;
    private String fuelType;
    private String transmission;
    // Decorator pattern fields
    private boolean hasGps = false;
    private boolean hasInsurance = false;
    private boolean hasChildSeat = false;
    public enum VehicleType { CAR, BIKE, SUV, TRUCK, VAN }
    public enum VehicleStatus { AVAILABLE, RESERVED, RENTED, RETURNED, UNDER_MAINTENANCE }
}
