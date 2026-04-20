package com.vehiclerental.service.impl;
import com.vehiclerental.entity.Booking;
import com.vehiclerental.entity.Booking.BookingStatus;
import com.vehiclerental.entity.User;
import com.vehiclerental.observer.BookingObserver;
import com.vehiclerental.repository.BookingRepository;
import com.vehiclerental.service.BookingService;
import com.vehiclerental.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * GRASP: Controller domain — BookingServiceImpl handles all booking operations.
 * Design Pattern: Observer — notifies registered observers on booking events.
 * Spring DI injects PricingService and observer list.
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PricingService pricingService;
    private final List<BookingObserver> observers; // Observer pattern - Spring injects all observers

    @Override
    public Booking createBooking(Booking booking) {
        // Calculate pricing
        var pricePerDay = booking.getVehicle().getPricePerDay();
        var base = pricingService.calculateBaseAmount(pricePerDay, booking.getStartDate(), booking.getEndDate());
        var addons = pricingService.calculateAddonAmount(booking);
        booking.setTotalAmount(pricingService.calculateTotal(base, addons, java.math.BigDecimal.ZERO));
        if (booking.getStatus() == null) {
            booking.setStatus(BookingStatus.INITIATED);
        }

        // Persist initiated state so INITIATED -> CONFIRMED transition is real in runtime flow
        Booking initiated = bookingRepository.save(booking);

        initiated.setStatus(BookingStatus.CONFIRMED);
        Booking saved = bookingRepository.save(initiated);

        // Notify observers (Observer pattern)
        observers.forEach(o -> o.onBookingCreated(saved));
        return saved;
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    @Override
    public Booking update(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> findByUser(User user) {
        return bookingRepository.findByUser(user);
    }

    @Override
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));
        booking.setStatus(BookingStatus.CANCELLED);
        Booking cancelled = bookingRepository.save(booking);
        observers.forEach(o -> o.onBookingCancelled(cancelled));
        return cancelled;
    }

    @Override
    public boolean isVehicleAvailable(Long vehicleId, LocalDate start, LocalDate end) {
        List<Booking> conflicts = bookingRepository
            .findByVehicleIdAndStatusNotAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                vehicleId, BookingStatus.CANCELLED, end, start);
        return conflicts.isEmpty();
    }
}
