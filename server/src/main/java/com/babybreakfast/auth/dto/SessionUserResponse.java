package com.babybreakfast.auth.dto;

public record SessionUserResponse(
        Long id,
        String username,
        String displayName
) {
}
