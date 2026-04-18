package com.vehiclerental.controller;
import com.vehiclerental.decorator.*;
import com.vehiclerental.entity.Booking;
import com.vehiclerental.entity.Vehicle;
import com.vehiclerental.service.BookingService;
import com.vehiclerental.service.UserService;
import com.vehiclerental.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

/**
 * GRASP: Controller — BookingController is the GRASP Controller.
 * It is the first object to receive UI events for the booking domain
 * and delegates ALL business logic to BookingService.
 * Design Pattern: Decorator — demonstrates VehicleDecorator before confirming booking.
 */
@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String REDIRECT_MY_BOOKINGS = "redirect:/bookings/my";

    private final BookingService bookingService;
    private final VehicleService vehicleService;
    private final UserService userService;

    @GetMapping("/new/{vehicleId}")
    public String bookingForm(@PathVariable Long vehicleId, Model model) {
        Vehicle vehicle = vehicleService.findById(vehicleId).orElseThrow();
        // Decorator pattern demo: show price with all addons
        VehicleComponent base = new BaseVehicle(vehicle);
        VehicleComponent withAll = new ChildSeatDecorator(new InsuranceDecorator(new GpsDecorator(base)));
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("decoratedDescription", withAll.getDescription());
        model.addAttribute("decoratedPrice", withAll.getDailyPrice());
        model.addAttribute("today", LocalDate.now());
        return "booking/form";
    }

    @PostMapping("/create")
    public String createBooking(@RequestParam Long vehicleId,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                @RequestParam(defaultValue = "false") boolean withGps,
                                @RequestParam(defaultValue = "false") boolean withInsurance,
                                @RequestParam(defaultValue = "false") boolean withChildSeat,
                                Authentication auth, RedirectAttributes ra) {
        if (!bookingService.isVehicleAvailable(vehicleId, startDate, endDate)) {
            ra.addFlashAttribute("error", "Vehicle is not available for selected dates.");
            return "redirect:/bookings/new/" + vehicleId;
        }
        var user = userService.findByEmail(auth.getName()).orElseThrow();
        var vehicle = vehicleService.findById(vehicleId).orElseThrow();
        Booking booking = new Booking();
        booking.setUser(user); booking.setVehicle(vehicle);
        booking.setStartDate(startDate); booking.setEndDate(endDate);
        booking.setWithGps(withGps); booking.setWithInsurance(withInsurance); booking.setWithChildSeat(withChildSeat);
        booking.setStatus(Booking.BookingStatus.INITIATED);
        Booking saved = bookingService.createBooking(booking);
        ra.addFlashAttribute("success", "Booking confirmed! ID: " + saved.getId());
        return "redirect:/bookings/" + saved.getId() + "/confirm";
    }

    @GetMapping("/{id}")
    public String bookingDetail(@PathVariable Long id, Model model, Authentication auth) {
        var booking = bookingService.findById(id).orElseThrow();
        var currentUser = userService.findByEmail(auth.getName()).orElseThrow();
        if (!canAccessBooking(booking, currentUser)) {
            return REDIRECT_MY_BOOKINGS;
        }
        model.addAttribute("booking", booking);
        return "booking/detail";
    }

    @GetMapping("/{id}/confirm")
    public String bookingConfirm(@PathVariable Long id, Model model, Authentication auth) {
        var booking = bookingService.findById(id).orElseThrow();
        var currentUser = userService.findByEmail(auth.getName()).orElseThrow();
        if (!canAccessBooking(booking, currentUser)) {
            return REDIRECT_MY_BOOKINGS;
        }
        model.addAttribute("booking", booking);
        return "booking/confirm";
    }

    @GetMapping("/my")
    public String myBookings(Authentication auth, Model model) {
        var user = userService.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("bookings", bookingService.findByUser(user));
        return "booking/my-bookings";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes ra) {
        bookingService.cancelBooking(id);
        ra.addFlashAttribute("success", "Booking cancelled.");
        return REDIRECT_MY_BOOKINGS;
    }

    private boolean canAccessBooking(Booking booking, com.vehiclerental.entity.User currentUser) {
        return booking.getUser().getId().equals(currentUser.getId())
            || currentUser.getRole() == com.vehiclerental.entity.User.Role.ADMIN;
    }
}
