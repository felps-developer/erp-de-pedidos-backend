package com.golden.erp.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Usuário é obrigatório")
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    private String password;
}
