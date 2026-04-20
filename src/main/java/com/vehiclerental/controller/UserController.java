package com.vehiclerental.controller;

import com.vehiclerental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Member 1 minor use case: Profile view & edit.
 * GRASP: Information Expert — delegates to UserService for all user data.
 */
@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserController {

    private static final String FLASH_ERROR = "error";
    private static final String REDIRECT_PROFILE = "redirect:/profile";

    private final UserService userService;

    @GetMapping
    public String profile(Authentication auth, Model model) {
        var user = userService.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("user", user);
        return "user/profile";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String phone,
                                @RequestParam String address,
                                @RequestParam(required = false) String currentPassword,
                                @RequestParam(required = false) String newPassword,
                                @RequestParam(required = false) String confirmPassword,
                                Authentication auth,
                                RedirectAttributes ra) {
        var user = userService.findByEmail(auth.getName()).orElseThrow();
        user.setName(name);
        user.setPhone(phone);
        user.setAddress(address);

        boolean wantsPasswordChange = notBlank(currentPassword) || notBlank(newPassword) || notBlank(confirmPassword);
        if (wantsPasswordChange) {
            if (!notBlank(currentPassword) || !notBlank(newPassword) || !notBlank(confirmPassword)) {
                ra.addFlashAttribute(FLASH_ERROR, "To change password, fill current, new, and confirm password fields.");
                return REDIRECT_PROFILE;
            }
            if (newPassword.length() < 6) {
                ra.addFlashAttribute(FLASH_ERROR, "New password must be at least 6 characters.");
                return REDIRECT_PROFILE;
            }
            if (!newPassword.equals(confirmPassword)) {
                ra.addFlashAttribute(FLASH_ERROR, "New password and confirm password do not match.");
                return REDIRECT_PROFILE;
            }
            boolean changed = userService.changePassword(auth.getName(), currentPassword, newPassword);
            if (!changed) {
                ra.addFlashAttribute(FLASH_ERROR, "Current password is incorrect.");
                return REDIRECT_PROFILE;
            }
        }

        userService.update(user);
        ra.addFlashAttribute("success", wantsPasswordChange ? "Profile and password updated." : "Profile updated.");
        return REDIRECT_PROFILE;
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
