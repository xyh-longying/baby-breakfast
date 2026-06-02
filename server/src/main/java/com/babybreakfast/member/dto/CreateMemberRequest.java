package com.babybreakfast.member.dto;

import java.util.List;

public record CreateMemberRequest(
        String name,
        String role,
        String gender,
        String birthday,
        String avatarUrl,
        List<String> allergens,
        List<String> dietaryRestrictions,
        List<String> preferences
) {
}
