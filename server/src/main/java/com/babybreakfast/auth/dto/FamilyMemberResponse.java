package com.babybreakfast.auth.dto;

import java.util.List;

public record FamilyMemberResponse(
        Long id,
        String name,
        String role,
        Integer age
) {
    public static FamilyMemberResponse fromMemberDTO(com.babybreakfast.member.dto.MemberDTO dto) {
        return new FamilyMemberResponse(dto.id(), dto.name(), dto.role(), dto.getAge());
    }
}
