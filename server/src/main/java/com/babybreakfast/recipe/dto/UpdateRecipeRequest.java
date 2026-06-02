package com.babybreakfast.recipe.dto;

import java.util.List;

public record UpdateRecipeRequest(
        String name,
        Long dishId,
        Integer servings,
        List<String> steps,
        List<RecipeDTO.RecipeIngredientItem> ingredientItems,
        String tips,
        String imageUrl
) {
}
