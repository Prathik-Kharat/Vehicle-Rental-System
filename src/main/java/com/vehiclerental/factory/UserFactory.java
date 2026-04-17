package com.vehiclerental.factory;
import com.vehiclerental.entity.User;
import com.vehiclerental.entity.User.Role;

/**
 * Design Pattern: Factory (Creational)
 * Creates User objects with the correct Role and defaults without
 * exposing construction logic to the controller.
 */
public class UserFactory {
    private UserFactory() {
        // Utility class
    }

    public static User createUser(String name, String email, String password, String phone, Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setRole(role == null ? Role.CUSTOMER : role);
        user.setActive(true);
        return user;
    }

    public static User createCustomer(String name, String email, String password, String phone) {
        return createUser(name, email, password, phone, Role.CUSTOMER);
    }

    public static User createAdmin(String name, String email, String password, String phone) {
        return createUser(name, email, password, phone, Role.ADMIN);
    }
}
