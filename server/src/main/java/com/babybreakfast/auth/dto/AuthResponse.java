package com.babybreakfast.auth.dto;

public record AuthResponse(
        String token,
        SessionUserResponse user,
        FamilyResponse currentFamily,
        FamilyMemberResponse currentMember
) {
}
