package com.babybreakfast.category.dto;

import java.util.List;

public record UpdateIngredientCategoryRequest(
        String name,
        String parentCode,
        Integer sortOrder,
        String icon,
        String color,
        List<String> aliases
) {
}
