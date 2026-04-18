package com.vehiclerental.observer;
import com.vehiclerental.entity.Booking;
import com.vehiclerental.entity.Vehicle.VehicleStatus;
import com.vehiclerental.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Observer: Updates availability and logs changes triggered by booking events.
 */
@Component
@RequiredArgsConstructor
public class AvailabilityUpdaterObserver implements BookingObserver {

    private final VehicleService vehicleService;

    @Override
    public void onBookingCreated(Booking booking) {
        vehicleService.updateStatus(booking.getVehicle().getId(), VehicleStatus.RESERVED);
        System.out.printf("[AVAILABILITY] Vehicle #%d marked RESERVED after booking #%d%n",
            booking.getVehicle().getId(), booking.getId());
    }
    @Override
    public void onBookingCancelled(Booking booking) {
        vehicleService.updateStatus(booking.getVehicle().getId(), VehicleStatus.AVAILABLE);
        System.out.printf("[AVAILABILITY] Vehicle #%d marked AVAILABLE after booking #%d cancelled%n",
            booking.getVehicle().getId(), booking.getId());
    }
}
