package com.golden.erp.infrastructure.config;

import com.golden.erp.infrastructure.persistence.user.UserJpaEntity;
import com.golden.erp.infrastructure.persistence.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            UserJpaEntity admin = UserJpaEntity.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .build();
            userRepository.save(admin);
            log.info("[Seed] Usuário admin criado com sucesso");
        }
    }
}
