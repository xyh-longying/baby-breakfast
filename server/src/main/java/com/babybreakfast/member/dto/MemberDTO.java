package com.babybreakfast.member.dto;

import java.util.List;

public record MemberDTO(
        Long id,
        Long familyId,
        String name,
        String role,
        String gender,
        String birthday,
        String avatarUrl,
        List<String> allergens,
        List<String> dietaryRestrictions,
        List<String> preferences
) {
    public Integer getAge() {
        if (birthday == null || birthday.isEmpty()) {
            return null;
        }
        try {
            java.time.LocalDate birthDate = java.time.LocalDate.parse(birthday);
            java.time.LocalDate today = java.time.LocalDate.now();
            int age = today.getYear() - birthDate.getYear();
            if (today.getMonthValue() < birthDate.getMonthValue() ||
                (today.getMonthValue() == birthDate.getMonthValue() && today.getDayOfMonth() < birthDate.getDayOfMonth())) {
                age--;
            }
            return age;
        } catch (Exception e) {
            return null;
        }
    }
}
