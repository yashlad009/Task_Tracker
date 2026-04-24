package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Transactional
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegistrationOtpService registrationOtpService;

    @Autowired
    private PasswordService passwordService;

    // YOUR SECRET ADMIN KEY - Ensure this matches your frontend registration field
    private static final String SECRET_ADMIN_KEY = "YASH_ADMIN_777";

    /**
     * 1. SEND OTP
     * Checks if user exists, then triggers the EmailService to send code.
     */
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody User user) {
        try {
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required."));
            }
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User already exists!"));
            }
            String otp = registrationOtpService.generateAndSaveOtp(user.getEmail());
            registrationOtpService.sendOtpEmail(user.getEmail(), otp);
            return ResponseEntity.ok(Map.of(
                    "message", "OTP sent successfully",
                    "delivery", "email"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 2. VERIFY & REGISTER
     * Validates OTP and assigns role "ADMIN" if the secret key matches.
     */
    @PostMapping("/verify-and-register")
    public ResponseEntity<?> verifyAndRegister(@RequestBody Map<String, String> data) {
        String email = data.get("email");
        String password = data.get("password");
        String otp = data.get("otp");
        String providedAdminKey = data.get("adminKey");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required."));
        }

        if (registrationOtpService.verifyOtp(email, otp)) {
            if (userRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User already exists!"));
            }

            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(passwordService.hash(password));

            // ROLE-BASED ACCESS CONTROL (RBAC) LOGIC
            if (providedAdminKey != null && providedAdminKey.equals(SECRET_ADMIN_KEY)) {
                newUser.setRole("ADMIN");
            } else {
                newUser.setRole("USER");
            }

            User savedUser = userRepository.save(newUser);
            return ResponseEntity.ok(UserResponseMapper.toUserResponse(savedUser));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired OTP."));
        }
    }

    /**
     * 3. LOGIN
     * Returns the full User object (including role) to the frontend.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()
                || user.getPassword() == null || user.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required."));
        }

        return userRepository.findByEmail(user.getEmail())
                .map(dbUser -> {
                    if (passwordService.matches(user.getPassword(), dbUser.getPassword())) {
                        if (passwordService.needsUpgrade(dbUser.getPassword())) {
                            dbUser.setPassword(passwordService.hash(user.getPassword()));
                            userRepository.save(dbUser);
                        }
                        return ResponseEntity.ok((Object) UserResponseMapper.toUserResponse(dbUser));
                    }
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("error", "Invalid email or password."));
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Account not found.")));
    }

    /**
     * 4. UTILITY - GET ALL USERS
     * Used by the Admin Portal to display the user directory.
     */
    @GetMapping("/users")
    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * 5. UPDATE PROFILE
     * Allows password changes for existing users.
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody User updatedUser) {
        if (updatedUser.getEmail() == null || updatedUser.getEmail().isBlank()
                || updatedUser.getPassword() == null || updatedUser.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required."));
        }

        return userRepository.findByEmail(updatedUser.getEmail())
                .map(user -> {
                    user.setPassword(passwordService.hash(updatedUser.getPassword()));
                    userRepository.save(user);
                    return ResponseEntity.ok((Object) Map.of("message", "Profile updated!"));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found")));
    }
}
