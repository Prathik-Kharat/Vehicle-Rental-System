package com.vehiclerental.controller;
import com.vehiclerental.controller.dto.RegistrationForm;
import com.vehiclerental.entity.User;
import com.vehiclerental.factory.UserFactory;
import com.vehiclerental.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * GRASP: Information Expert — delegates all user logic to UserService.
 * Design Pattern: Factory — uses UserFactory to create user objects.
 */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() { return "auth/login"; }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new RegistrationForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") RegistrationForm formUser,
                           BindingResult bindingResult,
                           Authentication auth,
                           RedirectAttributes ra) {
        if (formUser.getPassword() == null || formUser.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "password.tooShort", "Password must be at least 6 characters.");
        }

        if (userService.emailExists(formUser.getEmail())) {
            bindingResult.rejectValue("email", "email.exists", "Email already registered.");
        }

        User.Role requestedRole = formUser.getRole() == null ? User.Role.CUSTOMER : formUser.getRole();
        if (requestedRole == User.Role.ADMIN && !isAdmin(auth)) {
            bindingResult.rejectValue("role", "role.forbidden", "Only admins can create admin accounts.");
        }

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        User user = UserFactory.createUser(
            formUser.getName(),
            formUser.getEmail(),
            formUser.getPassword(),
            formUser.getPhone(),
            requestedRole
        );

        userService.register(user);
        ra.addFlashAttribute("success", "Registration successful! Please login.");
        return "redirect:/auth/login";
    }

    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}
