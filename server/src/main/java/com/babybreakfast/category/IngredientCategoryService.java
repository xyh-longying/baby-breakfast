package com.babybreakfast.category;

import com.babybreakfast.category.dto.CreateIngredientCategoryRequest;
import com.babybreakfast.category.dto.IngredientCategoryDTO;
import com.babybreakfast.category.dto.UpdateIngredientCategoryRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class IngredientCategoryService {

    private final ConcurrentHashMap<String, IngredientCategoryDTO> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private static final Long DEFAULT_FAMILY_ID = 1L;

    public IngredientCategoryService() {
        List<IngredientCategoryDTO> defaults = List.of(
            new IngredientCategoryDTO(1L, DEFAULT_FAMILY_ID, "主食杂粮", "grain", null, 1, "🌾", "#F5A623", List.of("谷物", "杂粮")),
            new IngredientCategoryDTO(2L, DEFAULT_FAMILY_ID, "蛋奶豆类", "dairy", null, 2, "🥛", "#FFFFFF", List.of("奶制品", "豆制品")),
            new IngredientCategoryDTO(3L, DEFAULT_FAMILY_ID, "肉类", "meat", null, 3, "🥩", "#E74C3C", List.of("畜禽")),
            new IngredientCategoryDTO(4L, DEFAULT_FAMILY_ID, "水产", "seafood", null, 4, "🐟", "#5DADE2", List.of("鱼虾蟹贝")),
            new IngredientCategoryDTO(5L, DEFAULT_FAMILY_ID, "蔬菜", "vegetable", null, 5, "🥬", "#27AE60", List.of("叶菜", "根茎")),
            new IngredientCategoryDTO(6L, DEFAULT_FAMILY_ID, "水果", "fruit", null, 6, "🍎", "#FF6B6B", List.of("瓜果")),
            new IngredientCategoryDTO(7L, DEFAULT_FAMILY_ID, "坚果", "nut", null, 7, "🌰", "#8D6E63", List.of("干果")),
            new IngredientCategoryDTO(8L, DEFAULT_FAMILY_ID, "调味品", "condiment", null, 8, "🧂", "#95A5A6", List.of("调料")),
            new IngredientCategoryDTO(9L, DEFAULT_FAMILY_ID, "其他", "other", null, 99, "📦", "#BDC3C7", List.of())
        );
        defaults.forEach(cat -> store.put(cat.code(), cat));
        idGenerator.set(100);
    }

    public List<IngredientCategoryDTO> findAll() {
        return new ArrayList<>(store.values());
    }

    public IngredientCategoryDTO findByCode(String code) {
        return store.get(code);
    }

    public IngredientCategoryDTO create(CreateIngredientCategoryRequest request) {
        Long id = idGenerator.getAndIncrement();
        IngredientCategoryDTO category = new IngredientCategoryDTO(
                id, DEFAULT_FAMILY_ID, request.name(), request.code(), request.parentCode(),
                request.sortOrder() != null ? request.sortOrder() : 99, request.icon(), request.color(), request.aliases()
        );
        store.put(request.code(), category);
        return category;
    }

    public IngredientCategoryDTO update(String code, UpdateIngredientCategoryRequest request) {
        IngredientCategoryDTO existing = store.get(code);
        if (existing == null) {
            throw new IllegalArgumentException("Category not found: " + code);
        }
        IngredientCategoryDTO updated = new IngredientCategoryDTO(
                existing.id(), existing.familyId(),
                request.name() != null ? request.name() : existing.name(),
                code,
                request.parentCode() != null ? request.parentCode() : existing.parentCode(),
                request.sortOrder() != null ? request.sortOrder() : existing.sortOrder(),
                request.icon() != null ? request.icon() : existing.icon(),
                request.color() != null ? request.color() : existing.color(),
                request.aliases() != null ? request.aliases() : existing.aliases()
        );
        store.put(code, updated);
        return updated;
    }

    public void delete(String code) {
        store.remove(code);
    }
}
