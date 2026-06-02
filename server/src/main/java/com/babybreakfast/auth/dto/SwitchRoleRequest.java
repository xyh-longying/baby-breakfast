package com.babybreakfast.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SwitchRoleRequest(
        @NotNull Long memberId,
        @NotBlank String role
) {
}
