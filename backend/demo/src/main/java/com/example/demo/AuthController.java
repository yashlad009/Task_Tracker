package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegistrationOtpService registrationOtpService;

    // YOUR SECRET ADMIN KEY - Ensure this matches your frontend registration field
    private static final String SECRET_ADMIN_KEY = "YASH_ADMIN_777";

    private Map<String, Object> toUserResponse(User user) {
        return Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "tokens", user.getTokens()
        );
    }

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

        if (registrationOtpService.verifyOtp(email, otp)) {
            if (userRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User already exists!"));
            }

            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(password);

            // ROLE-BASED ACCESS CONTROL (RBAC) LOGIC
            if (providedAdminKey != null && providedAdminKey.equals(SECRET_ADMIN_KEY)) {
                newUser.setRole("ADMIN");
            } else {
                newUser.setRole("USER");
            }

            User savedUser = userRepository.save(newUser);
            return ResponseEntity.ok(toUserResponse(savedUser));
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
                    if (dbUser.getPassword().equals(user.getPassword())) {
                        return ResponseEntity.ok((Object) toUserResponse(dbUser));
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
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 5. UPDATE PROFILE
     * Allows password changes for existing users.
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody User updatedUser) {
        return userRepository.findByEmail(updatedUser.getEmail())
                .map(user -> {
                    user.setPassword(updatedUser.getPassword());
                    userRepository.save(user);
                    return ResponseEntity.ok((Object) Map.of("message", "Profile updated!"));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found")));
    }
}
