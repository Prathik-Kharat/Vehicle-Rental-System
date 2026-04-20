package com.vehiclerental.service.impl;
import com.vehiclerental.entity.Booking.BookingStatus;
import com.vehiclerental.entity.Rental;
import com.vehiclerental.entity.Rental.RentalStatus;
import com.vehiclerental.entity.User;
import com.vehiclerental.entity.Vehicle.VehicleStatus;
import com.vehiclerental.repository.RentalRepository;
import com.vehiclerental.service.BookingService;
import com.vehiclerental.service.PricingService;
import com.vehiclerental.service.RentalService;
import com.vehiclerental.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * GRASP: Pure Fabrication — this service coordinates rental workflows and pricing
 * without overloading entities/controllers with orchestration concerns.
 *
 * Design pattern in action: Spring Dependency Injection injects collaborating
 * services (including {@link PricingService}) into this proxy-managed service,
 * making the framework-level DI pattern explicit and testable.
 */
@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final BookingService bookingService;
    private final VehicleService vehicleService;
    private final PricingService pricingService;

    @Override
    @Transactional
    public Rental createRentalFromBooking(Long bookingId) {
        var booking = bookingService.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        Rental rental = new Rental();
        rental.setBooking(booking);
        rental.setStatus(RentalStatus.PENDING);
        return rentalRepository.save(rental);
    }

    @Override
    @Transactional
    public Rental markPickedUp(Long rentalId, LocalDate actualPickup) {
        Rental rental = getOrThrow(rentalId);
        rental.setActualPickupDate(actualPickup);
        rental.setStatus(RentalStatus.PICKED_UP);
        var booking = rental.getBooking();
        booking.setStatus(BookingStatus.ACTIVE);
        bookingService.update(booking);
        vehicleService.updateStatus(booking.getVehicle().getId(), VehicleStatus.RENTED);
        return rentalRepository.save(rental);
    }

    @Override
    @Transactional
    public Rental markReturned(Long rentalId, LocalDate actualReturn) {
        Rental rental = getOrThrow(rentalId);
        rental.setActualReturnDate(actualReturn);
        rental.setStatus(RentalStatus.RETURNED);
        var booking = rental.getBooking();
        vehicleService.updateStatus(booking.getVehicle().getId(), VehicleStatus.RETURNED);
        return rentalRepository.save(rental);
    }

    @Override
    @Transactional
    public Rental generateInvoice(Long rentalId) {
        Rental rental = getOrThrow(rentalId);
        var booking = rental.getBooking();
        var pricePerDay = booking.getVehicle().getPricePerDay();
        var base = pricingService.calculateBaseAmount(pricePerDay, booking.getStartDate(), booking.getEndDate());
        var addons = pricingService.calculateAddonAmount(booking);
        var lateFee = pricingService.calculateLateFee(booking.getEndDate(), rental.getActualReturnDate(), pricePerDay);
        rental.setBaseAmount(base);
        rental.setAddonAmount(addons);
        rental.setLateFee(lateFee);
        rental.setTotalAmount(pricingService.calculateTotal(base, addons, lateFee));
        rental.setStatus(RentalStatus.INVOICE_GENERATED);
        booking.setStatus(BookingStatus.COMPLETED);
        bookingService.update(booking);
        vehicleService.updateStatus(booking.getVehicle().getId(), VehicleStatus.AVAILABLE);
        return rentalRepository.save(rental);
    }

    @Override public Optional<Rental> findById(Long id) { return rentalRepository.findById(id); }
    @Override public Optional<Rental> findByBookingId(Long bookingId) { return rentalRepository.findByBookingId(bookingId); }
    @Override public List<Rental> findAll() { return rentalRepository.findAll(); }
    @Override public List<Rental> findByUser(User user) { return rentalRepository.findByBookingUser(user); }
    @Override public List<Rental> findActive() { return rentalRepository.findByStatus(RentalStatus.PICKED_UP); }

    private Rental getOrThrow(Long id) {
        return rentalRepository.findById(id).orElseThrow(() -> new RuntimeException("Rental not found: " + id));
    }
}
