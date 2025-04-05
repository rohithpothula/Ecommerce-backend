package com.flipkart.ecommerce_backend.bootstrap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

import com.flipkart.ecommerce_backend.models.ERole;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.Role;
import com.flipkart.ecommerce_backend.repository.LocalUserRepository;
import com.flipkart.ecommerce_backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner; // Or use ApplicationRunner or @PostConstruct
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer implements CommandLineRunner {

    private final LocalUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;


    @Override
    @Transactional // Ensure operations happen within a transaction
    public void run(String... args) throws Exception {
        log.info("Checking for initial admin user...");

        // Check if ANY admin user exists first
        // Assumes RoleRepository has findByName and UserRepository has findByRolesContains
        // This query might need optimization depending on your setup
        boolean adminExists = roleRepository.findByName(ERole.ROLE_ADMIN)
                .map(userRepository::existsByRolesContaining)
                .orElse(false);


        if (!adminExists) {
            log.warn("No ADMIN user found, creating initial admin account.");

            // Find or create ROLE_ADMIN
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_ADMIN)));
            // Ensure ROLE_USER exists too if needed
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_USER)));

            // Check if the specific default admin username/email exists
            if (userRepository.findByUsernameIgnoreCase(adminUsername).isEmpty() &&
                    userRepository.findByEmailIgnoreCase(adminEmail).isEmpty()) {

                LocalUser adminUser = new LocalUser();
                adminUser.setUsername(adminUsername);
                adminUser.setEmail(adminEmail);
                adminUser.setPassword(passwordEncoder.encode(adminPassword));
                adminUser.setFirstName("Admin");
                adminUser.setLastName("User");
                adminUser.setEnabled(true); // Enable the admin account
                adminUser.setEmailVerified(true); // Mark as verified
                adminUser.setAccountNonExpired(true);
                adminUser.setAccountNonLocked(true);
                adminUser.setCredentialsNonExpired(true);
                adminUser.setRoles(Set.of(adminRole, userRole)); // Assign roles

                userRepository.save(adminUser);
            } else {
                log.info("Admin role exists but default admin username/email might be taken. Skipping default admin creation.");
            }

        } else {
            log.info("Admin user already exists. Skipping initial admin creation.");
        }
    }
    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
