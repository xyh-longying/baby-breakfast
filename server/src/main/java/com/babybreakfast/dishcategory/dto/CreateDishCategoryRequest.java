package com.babybreakfast.dishcategory.dto;

import java.util.List;

public record CreateDishCategoryRequest(
        String name,
        String code,
        String parentCode,
        Integer sortOrder,
        String icon,
        String color,
        List<String> aliases
) {
}
