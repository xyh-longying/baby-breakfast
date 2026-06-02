package com.babybreakfast.ingredient.dto;

import java.util.List;

public record IngredientDTO(
        Long id,
        Long familyId,
        String name,
        String categoryCode,
        String unit,
        Double density,
        String imageUrl,
        List<String> aliases,
        List<String> tags
) {
}
