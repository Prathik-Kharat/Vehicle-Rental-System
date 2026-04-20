package com.vehiclerental.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "bookings")
@Data @NoArgsConstructor @AllArgsConstructor
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
    @Column(name = "start_date", nullable = false) private LocalDate startDate;
    @Column(name = "end_date", nullable = false) private LocalDate endDate;
    @Column(name = "total_amount") private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING) private BookingStatus status;
    @Column(name = "created_at") private LocalDateTime createdAt;
    private String notes;
    private boolean withGps = false;
    private boolean withInsurance = false;
    private boolean withChildSeat = false;
    @PrePersist public void prePersist() {
        createdAt = LocalDateTime.now();
        if (status == null) status = BookingStatus.INITIATED;
    }
    public enum BookingStatus { INITIATED, CONFIRMED, ACTIVE, COMPLETED, CANCELLED }
}
