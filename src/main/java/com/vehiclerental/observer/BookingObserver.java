package com.vehiclerental.observer;
import com.vehiclerental.entity.Booking;

/**
 * Design Pattern: Observer (Behavioral)
 * All observers implement this interface and are notified on booking events.
 */
public interface BookingObserver {
    void onBookingCreated(Booking booking);
    void onBookingCancelled(Booking booking);
}
