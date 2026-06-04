package com.babybreakfast.recipe.dto;

import java.util.List;

public record RecipeDTO(
        Long id,
        Long familyId,
        String name,
        Long dishId,
        String dishName,
        Integer servings,
        List<String> steps,
        List<RecipeIngredientItem> ingredientItems,
        String tips,
        String imageUrl
) {
    public record RecipeIngredientItem(
            String ingredientName,
            String amount,
            String unit
    ) {
    }
}
