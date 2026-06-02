package com.babybreakfast.category.dto;

import java.util.List;

public record IngredientCategoryDTO(
        Long id,
        Long familyId,
        String name,
        String code,
        String parentCode,
        Integer sortOrder,
        String icon,
        String color,
        List<String> aliases
) {
}
