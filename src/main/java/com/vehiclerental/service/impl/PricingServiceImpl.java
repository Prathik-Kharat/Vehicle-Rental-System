package com.vehiclerental.service.impl;
import com.vehiclerental.entity.Booking;
import com.vehiclerental.service.PricingService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * GRASP: Pure Fabrication
 * PricingService is a fabricated class with no real-world entity counterpart.
 * It centralizes all pricing logic cleanly without polluting domain classes.
 */
@Service
public class PricingServiceImpl implements PricingService {

    private static final BigDecimal GPS_DAILY_COST = new BigDecimal("5.00");
    private static final BigDecimal INSURANCE_DAILY_COST = new BigDecimal("10.00");
    private static final BigDecimal CHILD_SEAT_DAILY_COST = new BigDecimal("3.00");
    private static final BigDecimal LATE_FEE_MULTIPLIER = new BigDecimal("1.5");

    @Override
    public BigDecimal calculateBaseAmount(BigDecimal pricePerDay, LocalDate start, LocalDate end) {
        long days = ChronoUnit.DAYS.between(start, end);
        if (days <= 0) days = 1;
        return pricePerDay.multiply(BigDecimal.valueOf(days));
    }

    @Override
    public BigDecimal calculateAddonAmount(Booking booking) {
        long days = ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate());
        if (days <= 0) days = 1;
        BigDecimal addons = BigDecimal.ZERO;
        if (booking.isWithGps()) addons = addons.add(GPS_DAILY_COST.multiply(BigDecimal.valueOf(days)));
        if (booking.isWithInsurance()) addons = addons.add(INSURANCE_DAILY_COST.multiply(BigDecimal.valueOf(days)));
        if (booking.isWithChildSeat()) addons = addons.add(CHILD_SEAT_DAILY_COST.multiply(BigDecimal.valueOf(days)));
        return addons;
    }

    @Override
    public BigDecimal calculateLateFee(LocalDate expectedReturn, LocalDate actualReturn, BigDecimal pricePerDay) {
        if (actualReturn == null || !actualReturn.isAfter(expectedReturn)) return BigDecimal.ZERO;
        long lateDays = ChronoUnit.DAYS.between(expectedReturn, actualReturn);
        return pricePerDay.multiply(LATE_FEE_MULTIPLIER).multiply(BigDecimal.valueOf(lateDays));
    }

    @Override
    public BigDecimal calculateTotal(BigDecimal base, BigDecimal addons, BigDecimal lateFee) {
        return base.add(addons).add(lateFee);
    }
}
