package com.babybreakfast.auth.dto;

import java.util.List;

public record FamilyResponse(
        Long id,
        String name,
        List<FamilyMemberResponse> members
) {
}
