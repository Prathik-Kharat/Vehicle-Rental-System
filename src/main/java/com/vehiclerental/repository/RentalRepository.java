package com.vehiclerental.repository;
import com.vehiclerental.entity.Rental;
import com.vehiclerental.entity.Rental.RentalStatus;
import com.vehiclerental.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByStatus(RentalStatus status);
    List<Rental> findByBookingUser(User user);
    Optional<Rental> findByBookingId(Long bookingId);
}
