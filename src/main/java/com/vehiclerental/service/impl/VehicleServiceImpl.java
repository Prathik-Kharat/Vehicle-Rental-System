package com.vehiclerental.service.impl;
import com.vehiclerental.entity.Vehicle;
import com.vehiclerental.entity.Vehicle.VehicleStatus;
import com.vehiclerental.entity.Vehicle.VehicleType;
import com.vehiclerental.repository.VehicleRepository;
import com.vehiclerental.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * GRASP: Low Coupling — VehicleServiceImpl has ZERO imports/dependencies on BookingService.
 * Vehicle availability managed purely through its own status.
 */
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getStatus() == null) vehicle.setStatus(VehicleStatus.AVAILABLE);
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Optional<Vehicle> findById(Long id) {
        return vehicleRepository.findById(id);
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    @Override
    public List<Vehicle> findAvailable() {
        return vehicleRepository.findByStatus(VehicleStatus.AVAILABLE);
    }

    @Override
    public List<Vehicle> findByType(VehicleType type) {
        return vehicleRepository.findByType(type);
    }

    @Override
    public List<Vehicle> findAvailableByType(VehicleType type) {
        return vehicleRepository.findByStatusAndType(VehicleStatus.AVAILABLE, type);
    }

    @Override
    public List<Vehicle> findAvailableByMaxPrice(BigDecimal maxPrice) {
        return vehicleRepository.findByStatusAndPricePerDayLessThanEqual(VehicleStatus.AVAILABLE, maxPrice);
    }

    @Override
    public List<Vehicle> findAvailableByTypeAndMaxPrice(VehicleType type, BigDecimal maxPrice) {
        return vehicleRepository.findByStatusAndTypeAndPricePerDayLessThanEqual(VehicleStatus.AVAILABLE, type, maxPrice);
    }

    @Override
    public void updateStatus(Long vehicleId, VehicleStatus status) {
        vehicleRepository.findById(vehicleId).ifPresent(v -> {
            v.setStatus(status);
            vehicleRepository.save(v);
        });
    }

    @Override
    public void delete(Long id) {
        vehicleRepository.deleteById(id);
    }

    @Override
    public boolean registrationNumberExists(String regNum) {
        return vehicleRepository.existsByRegistrationNumber(regNum);
    }
}
