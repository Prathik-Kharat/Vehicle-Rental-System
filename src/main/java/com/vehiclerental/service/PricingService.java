package com.vehiclerental.service;
import com.vehiclerental.entity.Booking;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * GRASP: Pure Fabrication
 * PricingService does not map to any real-world domain entity.
 * It is fabricated purely to handle pricing/charge calculation logic,
 * keeping Booking and Vehicle entities clean from pricing concerns.
 */
public interface PricingService {
    BigDecimal calculateBaseAmount(BigDecimal pricePerDay, LocalDate start, LocalDate end);
    BigDecimal calculateAddonAmount(Booking booking);
    BigDecimal calculateLateFee(LocalDate expectedReturn, LocalDate actualReturn, BigDecimal pricePerDay);
    BigDecimal calculateTotal(BigDecimal base, BigDecimal addons, BigDecimal lateFee);
}
