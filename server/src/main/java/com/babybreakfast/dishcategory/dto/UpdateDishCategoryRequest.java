package com.babybreakfast.dishcategory.dto;

import java.util.List;

public record UpdateDishCategoryRequest(
        String name,
        String parentCode,
        Integer sortOrder,
        String icon,
        String color,
        List<String> aliases
) {
}
