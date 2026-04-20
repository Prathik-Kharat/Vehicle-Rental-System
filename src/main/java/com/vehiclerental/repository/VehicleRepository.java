package com.vehiclerental.repository;
import com.vehiclerental.entity.Vehicle;
import com.vehiclerental.entity.Vehicle.VehicleStatus;
import com.vehiclerental.entity.Vehicle.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByStatus(VehicleStatus status);
    List<Vehicle> findByType(VehicleType type);
    List<Vehicle> findByStatusAndType(VehicleStatus status, VehicleType type);
    List<Vehicle> findByStatusAndPricePerDayLessThanEqual(VehicleStatus status, BigDecimal maxPrice);
    List<Vehicle> findByStatusAndTypeAndPricePerDayLessThanEqual(VehicleStatus status, VehicleType type, BigDecimal maxPrice);
    boolean existsByRegistrationNumber(String registrationNumber);
}
