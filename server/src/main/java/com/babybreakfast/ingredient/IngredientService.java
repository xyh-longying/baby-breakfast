package com.babybreakfast.ingredient;

import com.babybreakfast.ingredient.dto.CreateIngredientRequest;
import com.babybreakfast.ingredient.dto.IngredientDTO;
import com.babybreakfast.ingredient.dto.UpdateIngredientRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class IngredientService {

    private final ConcurrentHashMap<Long, IngredientDTO> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private static final Long DEFAULT_FAMILY_ID = 1L;

    public IngredientService() {
        List<IngredientDTO> defaults = List.of(
            new IngredientDTO(1L, DEFAULT_FAMILY_ID, "大米", "grain", "g", 0.8, null, List.of("粳米"), List.of("主食")),
            new IngredientDTO(2L, DEFAULT_FAMILY_ID, "面粉", "grain", "g", 0.5, null, List.of("小麦粉"), List.of("主食")),
            new IngredientDTO(3L, DEFAULT_FAMILY_ID, "鸡蛋", "dairy", "g", 1.0, null, List.of("蛋"), List.of("高蛋白")),
            new IngredientDTO(4L, DEFAULT_FAMILY_ID, "牛奶", "dairy", "ml", 1.03, null, List.of("鲜奶"), List.of("饮品", "高钙")),
            new IngredientDTO(5L, DEFAULT_FAMILY_ID, "猪肉", "meat", "g", 1.0, null, List.of("猪肉"), List.of("高蛋白")),
            new IngredientDTO(6L, DEFAULT_FAMILY_ID, "西红柿", "vegetable", "g", 1.0, null, List.of("番茄"), List.of("蔬菜")),
            new IngredientDTO(7L, DEFAULT_FAMILY_ID, "苹果", "fruit", "g", 0.85, null, List.of("苹果"), List.of("水果")),
            new IngredientDTO(8L, DEFAULT_FAMILY_ID, "盐", "condiment", "g", 1.2, null, List.of("食盐"), List.of("调味")),
            new IngredientDTO(9L, DEFAULT_FAMILY_ID, "白糖", "condiment", "g", 0.85, null, List.of("蔗糖"), List.of("调味", "甜味"))
        );
        defaults.forEach(ing -> store.put(ing.id(), ing));
        idGenerator.set(100);
    }

    public List<IngredientDTO> findAll() {
        return new ArrayList<>(store.values());
    }

    public IngredientDTO findById(Long id) {
        return store.get(id);
    }

    public List<IngredientDTO> findByCategory(String categoryCode) {
        return store.values().stream()
                .filter(ing -> categoryCode.equals(ing.categoryCode()))
                .toList();
    }

    public IngredientDTO create(CreateIngredientRequest request) {
        Long id = idGenerator.getAndIncrement();
        IngredientDTO ingredient = new IngredientDTO(
                id, DEFAULT_FAMILY_ID, request.name(), request.categoryCode(), request.unit(),
                request.density(), request.imageUrl(), request.aliases(), request.tags()
        );
        store.put(id, ingredient);
        return ingredient;
    }

    public IngredientDTO update(Long id, UpdateIngredientRequest request) {
        IngredientDTO existing = store.get(id);
        if (existing == null) {
            throw new IllegalArgumentException("Ingredient not found: " + id);
        }
        IngredientDTO updated = new IngredientDTO(
                id,
                existing.familyId(),
                request.name() != null ? request.name() : existing.name(),
                request.categoryCode() != null ? request.categoryCode() : existing.categoryCode(),
                request.unit() != null ? request.unit() : existing.unit(),
                request.density() != null ? request.density() : existing.density(),
                request.imageUrl() != null ? request.imageUrl() : existing.imageUrl(),
                request.aliases() != null ? request.aliases() : existing.aliases(),
                request.tags() != null ? request.tags() : existing.tags()
        );
        store.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        store.remove(id);
    }
}
