package com.vehiclerental.config;
import com.vehiclerental.entity.User;
import com.vehiclerental.entity.Vehicle;
import com.vehiclerental.entity.Vehicle.*;
import com.vehiclerental.factory.UserFactory;
import com.vehiclerental.service.UserService;
import com.vehiclerental.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/** Seeds default admin and sample vehicles on startup. */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final VehicleService vehicleService;

    @Override
    public void run(String... args) {
        if (!userService.emailExists("admin@vrental.com")) {
            User admin = UserFactory.createAdmin("Admin", "admin@vrental.com", "admin123", "9999999999");
            userService.register(admin);
            System.out.println("[INIT] Admin user created: admin@vrental.com / admin123");
        }
        if (vehicleService.findAll().isEmpty()) {
            seedVehicle("Toyota", "Innova", "KA01AA1234", VehicleType.SUV, "2500.00", 7, "Diesel", "Automatic");
            seedVehicle("Honda", "City", "KA02BB5678", VehicleType.CAR, "1500.00", 5, "Petrol", "Manual");
            seedVehicle("Royal Enfield", "Classic 350", "KA03CC9012", VehicleType.BIKE, "500.00", 1, "Petrol", "Manual");
            seedVehicle("Tata", "Ace", "KA04DD3456", VehicleType.TRUCK, "3000.00", 2, "Diesel", "Manual");
            System.out.println("[INIT] Sample vehicles created");
        }
    }

    private void seedVehicle(String brand, String model, String reg, VehicleType type,
                              String price, int seats, String fuel, String trans) {
        Vehicle v = new Vehicle();
        v.setBrand(brand); v.setModel(model); v.setRegistrationNumber(reg);
        v.setType(type); v.setPricePerDay(new BigDecimal(price));
        v.setSeats(seats); v.setFuelType(fuel); v.setTransmission(trans);
        v.setStatus(VehicleStatus.AVAILABLE);
        vehicleService.save(v);
    }
}
