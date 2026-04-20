package com.vehiclerental.controller;
import com.vehiclerental.service.UserService;
import com.vehiclerental.service.VehicleService;
import com.vehiclerental.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final VehicleService vehicleService;
    private final UserService userService;
    private final BookingService bookingService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("availableCount", vehicleService.findAvailable().size());
        return "home";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        var user = userService.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("user", user);
        if (user.getRole().name().equals("ADMIN")) {
            model.addAttribute("totalVehicles", vehicleService.findAll().size());
            model.addAttribute("totalBookings", bookingService.findAll().size());
            return "admin/dashboard";
        }
        model.addAttribute("myBookings", bookingService.findByUser(user));
        return "dashboard";
    }
}
