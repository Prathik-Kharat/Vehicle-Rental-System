package com.vehiclerental.controller;
import com.vehiclerental.entity.Rental;
import com.vehiclerental.entity.User;
import com.vehiclerental.service.RentalService;
import com.vehiclerental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

/**
 * GRASP: Pure Fabrication — delegates pricing to PricingService (via RentalService).
 * Design Pattern: Spring DI — all dependencies injected by Spring container.
 */
@Controller
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {

    private static final String REDIRECT_RENTAL_HISTORY = "redirect:/rentals/history";

    private final RentalService rentalService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String list(Model model) {
        model.addAttribute("rentals", rentalService.findAll());
        return "rental/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, Authentication auth) {
        var rental = rentalService.findById(id).orElseThrow();
        var currentUser = userService.findByEmail(auth.getName()).orElseThrow();
        if (!canAccessRental(rental, currentUser)) {
            return REDIRECT_RENTAL_HISTORY;
        }
        model.addAttribute("rental", rental);
        return "rental/detail";
    }

    @PostMapping("/create/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String createFromBooking(@PathVariable Long bookingId, RedirectAttributes ra) {
        var rental = rentalService.createRentalFromBooking(bookingId);
        ra.addFlashAttribute("success", "Rental created.");
        return "redirect:/rentals/" + rental.getId();
    }

    @PostMapping("/{id}/pickup")
    @PreAuthorize("hasRole('ADMIN')")
    public String markPickup(@PathVariable Long id,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
                             RedirectAttributes ra) {
        rentalService.markPickedUp(id, pickupDate);
        ra.addFlashAttribute("success", "Marked as picked up.");
        return "redirect:/rentals/" + id;
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('ADMIN')")
    public String markReturn(@PathVariable Long id,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
                             RedirectAttributes ra) {
        rentalService.markReturned(id, returnDate);
        ra.addFlashAttribute("success", "Marked as returned.");
        return "redirect:/rentals/" + id;
    }

    @PostMapping("/{id}/invoice")
    @PreAuthorize("hasRole('ADMIN')")
    public String generateInvoice(@PathVariable Long id, RedirectAttributes ra) {
        rentalService.generateInvoice(id);
        ra.addFlashAttribute("success", "Invoice generated.");
        return "redirect:/rentals/" + id;
    }

    @GetMapping("/{id}/invoice")
    public String invoicePage(@PathVariable Long id, Model model, Authentication auth) {
        var rental = rentalService.findById(id).orElseThrow();
        var currentUser = userService.findByEmail(auth.getName()).orElseThrow();
        if (!canAccessRental(rental, currentUser)) {
            return REDIRECT_RENTAL_HISTORY;
        }
        model.addAttribute("rental", rental);
        return "rental/invoice";
    }

    @GetMapping("/history")
    public String history(Model model, Authentication auth) {
        var currentUser = userService.findByEmail(auth.getName()).orElseThrow();
        if (currentUser.getRole() == User.Role.ADMIN) {
            model.addAttribute("rentals", rentalService.findAll());
        } else {
            model.addAttribute("rentals", rentalService.findByUser(currentUser));
        }
        return "rental/history";
    }

    private boolean canAccessRental(Rental rental, User currentUser) {
        return currentUser.getRole() == User.Role.ADMIN
            || rental.getBooking().getUser().getId().equals(currentUser.getId());
    }
}
