package com.vehiclerental.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "rentals")
@Data @NoArgsConstructor @AllArgsConstructor
public class Rental {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    @Column(name = "actual_pickup_date") private LocalDate actualPickupDate;
    @Column(name = "actual_return_date") private LocalDate actualReturnDate;
    @Column(name = "base_amount") private BigDecimal baseAmount;
    @Column(name = "addon_amount") private BigDecimal addonAmount;
    @Column(name = "late_fee") private BigDecimal lateFee;
    @Column(name = "total_amount") private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING) private RentalStatus status;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @PrePersist public void prePersist() {
        createdAt = LocalDateTime.now();
        if (status == null) status = RentalStatus.PENDING;
    }
    public enum RentalStatus { PENDING, PICKED_UP, RETURNED, INVOICE_GENERATED }
}
