package com.babybreakfast.dish.dto;

import java.util.List;

public record DishDTO(
        Long id,
        Long familyId,
        String name,
        String categoryCode,
        String description,
        Integer difficulty,
        Integer prepTimeMin,
        Integer cookTimeMin,
        String imageUrl,
        List<String> tags
) {
}
