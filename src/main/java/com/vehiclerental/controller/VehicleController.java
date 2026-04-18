package com.vehiclerental.controller;
import com.vehiclerental.decorator.BaseVehicle;
import com.vehiclerental.decorator.ChildSeatDecorator;
import com.vehiclerental.decorator.GpsDecorator;
import com.vehiclerental.decorator.InsuranceDecorator;
import com.vehiclerental.decorator.VehicleComponent;
import com.vehiclerental.entity.Vehicle;
import com.vehiclerental.entity.Vehicle.*;
import com.vehiclerental.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

/**
 * GRASP: Low Coupling — VehicleController only uses VehicleService.
 * No dependency on BookingService or any other service.
 */
@Controller
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public String list(@RequestParam(required = false) String type,
                       @RequestParam(required = false) BigDecimal maxPrice,
                       Model model) {
        boolean hasType = type != null && !type.isEmpty();
        boolean hasMaxPrice = maxPrice != null;

        if (hasType && hasMaxPrice) {
            model.addAttribute("vehicles", vehicleService.findAvailableByTypeAndMaxPrice(VehicleType.valueOf(type), maxPrice));
        } else if (hasType) {
            model.addAttribute("vehicles", vehicleService.findAvailableByType(VehicleType.valueOf(type)));
        } else if (hasMaxPrice) {
            model.addAttribute("vehicles", vehicleService.findAvailableByMaxPrice(maxPrice));
        } else {
            model.addAttribute("vehicles", vehicleService.findAvailable());
        }
        model.addAttribute("types", VehicleType.values());
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedMaxPrice", maxPrice);
        return "vehicle/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        var vehicle = vehicleService.findById(id).orElseThrow();
        model.addAttribute("vehicle", vehicle);

        VehicleComponent base = new BaseVehicle(vehicle);
        model.addAttribute("baseDescription", base.getDescription());
        model.addAttribute("basePrice", base.getDailyPrice());

        VehicleComponent withGps = new GpsDecorator(new BaseVehicle(vehicle));
        VehicleComponent withInsurance = new InsuranceDecorator(new BaseVehicle(vehicle));
        VehicleComponent withChildSeat = new ChildSeatDecorator(new BaseVehicle(vehicle));
        VehicleComponent withAllAddons = new ChildSeatDecorator(new InsuranceDecorator(new GpsDecorator(new BaseVehicle(vehicle))));

        model.addAttribute("withGpsDescription", withGps.getDescription());
        model.addAttribute("withGpsPrice", withGps.getDailyPrice());
        model.addAttribute("withInsuranceDescription", withInsurance.getDescription());
        model.addAttribute("withInsurancePrice", withInsurance.getDailyPrice());
        model.addAttribute("withChildSeatDescription", withChildSeat.getDescription());
        model.addAttribute("withChildSeatPrice", withChildSeat.getDailyPrice());
        model.addAttribute("withAllDescription", withAllAddons.getDescription());
        model.addAttribute("withAllPrice", withAllAddons.getDailyPrice());

        return "vehicle/detail";
    }

    @GetMapping("/admin/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("types", VehicleType.values());
        return "admin/vehicle-form";
    }

    @PostMapping("/admin/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String save(@ModelAttribute Vehicle vehicle, RedirectAttributes ra) {
        vehicleService.save(vehicle);
        ra.addFlashAttribute("success", "Vehicle saved successfully.");
        return "redirect:/admin/vehicles";
    }

    @GetMapping("/admin/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("vehicle", vehicleService.findById(id).orElseThrow());
        model.addAttribute("types", VehicleType.values());
        model.addAttribute("statuses", VehicleStatus.values());
        return "admin/vehicle-form";
    }

    @PostMapping("/admin/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        vehicleService.delete(id);
        ra.addFlashAttribute("success", "Vehicle deleted.");
        return "redirect:/admin/vehicles";
    }

    @PostMapping("/admin/{id}/maintenance")
    @PreAuthorize("hasRole('ADMIN')")
    public String markMaintenance(@PathVariable Long id, RedirectAttributes ra) {
        vehicleService.updateStatus(id, VehicleStatus.UNDER_MAINTENANCE);
        ra.addFlashAttribute("success", "Vehicle marked UNDER_MAINTENANCE.");
        return "redirect:/admin/vehicles";
    }

    @PostMapping("/admin/{id}/available")
    @PreAuthorize("hasRole('ADMIN')")
    public String markAvailable(@PathVariable Long id, RedirectAttributes ra) {
        vehicleService.updateStatus(id, VehicleStatus.AVAILABLE);
        ra.addFlashAttribute("success", "Vehicle marked AVAILABLE.");
        return "redirect:/admin/vehicles";
    }
}
