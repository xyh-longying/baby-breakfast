package com.babybreakfast.ingredient.dto;

import java.util.List;

public record CreateIngredientRequest(
        String name,
        String categoryCode,
        String unit,
        Double density,
        String imageUrl,
        List<String> aliases,
        List<String> tags
) {
}
