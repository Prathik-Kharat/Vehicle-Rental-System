package com.vehiclerental.repository;
import com.vehiclerental.entity.Booking;
import com.vehiclerental.entity.Booking.BookingStatus;
import com.vehiclerental.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByUserAndStatus(User user, BookingStatus status);
    List<Booking> findByVehicleIdAndStatusNotAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        Long vehicleId, BookingStatus status, LocalDate end, LocalDate start);
}
