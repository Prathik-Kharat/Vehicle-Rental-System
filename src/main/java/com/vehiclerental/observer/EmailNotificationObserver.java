package com.vehiclerental.observer;
import com.vehiclerental.entity.Booking;
import org.springframework.stereotype.Component;

/**
 * Observer: Sends email notification (console-logged for demo).
 */
@Component
public class EmailNotificationObserver implements BookingObserver {
    @Override
    public void onBookingCreated(Booking booking) {
        System.out.printf("[EMAIL] Booking confirmed for %s | Vehicle: %s %s | Dates: %s to %s | Total: ₹%.2f%n",
            booking.getUser().getName(),
            booking.getVehicle().getBrand(), booking.getVehicle().getModel(),
            booking.getStartDate(), booking.getEndDate(),
            booking.getTotalAmount());
    }
    @Override
    public void onBookingCancelled(Booking booking) {
        System.out.printf("[EMAIL] Booking #%d cancelled for %s%n",
            booking.getId(), booking.getUser().getName());
    }
}
