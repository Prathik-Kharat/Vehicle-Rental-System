package com.vehiclerental.service;
import com.vehiclerental.entity.Rental;
import com.vehiclerental.entity.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentalService {
    Rental createRentalFromBooking(Long bookingId);
    Rental markPickedUp(Long rentalId, LocalDate actualPickup);
    Rental markReturned(Long rentalId, LocalDate actualReturn);
    Rental generateInvoice(Long rentalId);
    Optional<Rental> findById(Long id);
    Optional<Rental> findByBookingId(Long bookingId);
    List<Rental> findAll();
    List<Rental> findByUser(User user);
    List<Rental> findActive();
}
