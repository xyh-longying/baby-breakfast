package com.babybreakfast.category.dto;

import java.util.List;

public record CreateIngredientCategoryRequest(
        String name,
        String code,
        String parentCode,
        Integer sortOrder,
        String icon,
        String color,
        List<String> aliases
) {
}
