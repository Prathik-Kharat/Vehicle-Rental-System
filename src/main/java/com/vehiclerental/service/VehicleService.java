package com.vehiclerental.service;
import com.vehiclerental.entity.Vehicle;
import com.vehiclerental.entity.Vehicle.VehicleStatus;
import com.vehiclerental.entity.Vehicle.VehicleType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * GRASP: Low Coupling
 * VehicleService has NO dependency on BookingService.
 * Vehicle availability is managed independently via its own status field.
 */
public interface VehicleService {
    Vehicle save(Vehicle vehicle);
    Optional<Vehicle> findById(Long id);
    List<Vehicle> findAll();
    List<Vehicle> findAvailable();
    List<Vehicle> findByType(VehicleType type);
    List<Vehicle> findAvailableByType(VehicleType type);
    List<Vehicle> findAvailableByMaxPrice(BigDecimal maxPrice);
    List<Vehicle> findAvailableByTypeAndMaxPrice(VehicleType type, BigDecimal maxPrice);
    void updateStatus(Long vehicleId, VehicleStatus status);
    void delete(Long id);
    boolean registrationNumberExists(String regNum);
}
