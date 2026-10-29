package com.nak.demo.web;

import com.nak.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Makes sure the account named in {@code app.bootstrap-admin} (application.yaml) has the ADMIN role, so there is
 * always one admin who can manage menu items and users. Runs once at startup; does nothing when the setting is empty.
 */
@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserRepository userRepository;
    private final String username;

    public AdminBootstrap(UserRepository userRepository, @Value("${app.bootstrap-admin:}") String username) {
        this.userRepository = userRepository;
        this.username = username == null ? "" : username.trim();
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (username.isEmpty()) return;
        userRepository.findByName(username)
                .or(() -> userRepository.findByEmailIgnoreCase(username))
                .ifPresentOrElse(user -> {
                    if ("ADMIN".equalsIgnoreCase(user.getRole() == null ? "" : user.getRole().trim())) {
                        log.info("Bootstrap admin '{}' already has the ADMIN role", user.getName());
                        return;
                    }
                    user.setRole("ADMIN");
                    userRepository.save(user);
                    log.warn("Bootstrap admin: gave '{}' the ADMIN role (app.bootstrap-admin)", user.getName());
                }, () -> log.warn("Bootstrap admin: no user named '{}' was found", username));
    }
}
