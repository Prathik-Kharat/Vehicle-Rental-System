package com.vehiclerental.service;
import com.vehiclerental.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * GRASP: Information Expert
 * UserService is the information expert for all user-related decisions.
 * It owns the knowledge of users and provides all user-related operations.
 */
public interface UserService {
    User register(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findAll();
    User update(User user);
    void deactivate(Long id);
    boolean emailExists(String email);
    boolean authenticate(String email, String rawPassword);
    User.Role getRole(String email);
    boolean changePassword(String email, String currentRawPassword, String newRawPassword);
}
