package com.golden.erp.presentation.controller;

import com.golden.erp.application.auth.dto.LoginRequest;
import com.golden.erp.application.auth.dto.LoginResponse;
import com.golden.erp.infrastructure.persistence.user.UserJpaEntity;
import com.golden.erp.infrastructure.persistence.user.UserJpaRepository;
import com.golden.erp.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de autenticação JWT")
public class AuthController {

    private final JwtService jwtService;
    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration}")
    private long expiration;

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica o usuário comparando a senha com o hash BCrypt no banco e retorna um token JWT. Credenciais padrão: admin/admin123")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Optional<UserJpaEntity> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(401).build();
        }

        String token = jwtService.generateToken(userOpt.get().getUsername());

        return ResponseEntity.ok(LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(expiration)
                .build());
    }
}
