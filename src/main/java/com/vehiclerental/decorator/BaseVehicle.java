package com.vehiclerental.decorator;
import com.vehiclerental.entity.Vehicle;
import java.math.BigDecimal;

/** Concrete component wrapping a Vehicle entity. */
public class BaseVehicle implements VehicleComponent {
    private final Vehicle vehicle;
    public BaseVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    @Override public String getDescription() { return vehicle.getBrand() + " " + vehicle.getModel(); }
    @Override public BigDecimal getDailyPrice() { return vehicle.getPricePerDay(); }
}
