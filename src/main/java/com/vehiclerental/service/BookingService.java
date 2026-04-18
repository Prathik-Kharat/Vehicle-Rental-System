package com.vehiclerental.service;
import com.vehiclerental.entity.Booking;
import com.vehiclerental.entity.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * GRASP: Controller
 * BookingService handles all booking domain operations.
 * BookingController delegates all system events here.
 */
public interface BookingService {
    Booking createBooking(Booking booking);
    Optional<Booking> findById(Long id);
    Booking update(Booking booking);
    List<Booking> findByUser(User user);
    List<Booking> findAll();
    Booking cancelBooking(Long bookingId);
    boolean isVehicleAvailable(Long vehicleId, LocalDate start, LocalDate end);
}
