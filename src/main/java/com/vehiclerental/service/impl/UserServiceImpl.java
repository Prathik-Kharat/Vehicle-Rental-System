package com.vehiclerental.service.impl;
import com.vehiclerental.entity.User;
import com.vehiclerental.repository.UserRepository;
import com.vehiclerental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * GRASP: Information Expert — UserServiceImpl owns all user decisions.
 * Spring DI (Design Pattern: Dependency Injection via @Autowired/@RequiredArgsConstructor)
 * is the framework-enforced design pattern demonstrated here.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deactivate(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean authenticate(String email, String rawPassword) {
        return findByEmail(email)
            .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
            .orElse(false);
    }

    @Override
    public User.Role getRole(String email) {
        return findByEmail(email)
            .map(User::getRole)
            .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    @Override
    public boolean changePassword(String email, String currentRawPassword, String newRawPassword) {
        var userOpt = findByEmail(email);
        if (userOpt.isEmpty()) {
            return false;
        }
        var user = userOpt.get();
        if (!passwordEncoder.matches(currentRawPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);
        return true;
    }
}
