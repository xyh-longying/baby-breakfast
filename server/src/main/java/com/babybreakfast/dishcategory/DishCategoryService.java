package com.babybreakfast.dishcategory;

import com.babybreakfast.dishcategory.dto.CreateDishCategoryRequest;
import com.babybreakfast.dishcategory.dto.DishCategoryDTO;
import com.babybreakfast.dishcategory.dto.UpdateDishCategoryRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class DishCategoryService {

    private final ConcurrentHashMap<String, DishCategoryDTO> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private static final Long DEFAULT_FAMILY_ID = 1L;

    public DishCategoryService() {
        List<DishCategoryDTO> defaults = List.of(
            new DishCategoryDTO(1L, DEFAULT_FAMILY_ID, "早餐主食", "staple", null, 1, "🍚", "#F5A623", List.of("主食")),
            new DishCategoryDTO(2L, DEFAULT_FAMILY_ID, "粥品", "porridge", null, 2, "🥣", "#5DADE2", List.of("稀饭")),
            new DishCategoryDTO(3L, DEFAULT_FAMILY_ID, "面点", "pastry", null, 3, "🥟", "#E74C3C", List.of("包子", "馒头")),
            new DishCategoryDTO(4L, DEFAULT_FAMILY_ID, "小食", "snack", null, 4, "🍪", "#F39C12", List.of("点心")),
            new DishCategoryDTO(5L, DEFAULT_FAMILY_ID, "汤羹", "soup", null, 5, "🍲", "#27AE60", List.of("汤")),
            new DishCategoryDTO(6L, DEFAULT_FAMILY_ID, "饮品", "drink", null, 6, "🧋", "#9B59B6", List.of("饮料")),
            new DishCategoryDTO(7L, DEFAULT_FAMILY_ID, "其他", "other", null, 99, "📦", "#BDC3C7", List.of())
        );
        defaults.forEach(cat -> store.put(cat.code(), cat));
        idGenerator.set(100);
    }

    public List<DishCategoryDTO> findAll() {
        return new ArrayList<>(store.values());
    }

    public DishCategoryDTO findByCode(String code) {
        return store.get(code);
    }

    public DishCategoryDTO create(CreateDishCategoryRequest request) {
        Long id = idGenerator.getAndIncrement();
        DishCategoryDTO category = new DishCategoryDTO(
                id, DEFAULT_FAMILY_ID, request.name(), request.code(), request.parentCode(),
                request.sortOrder() != null ? request.sortOrder() : 99, request.icon(), request.color(), request.aliases()
        );
        store.put(request.code(), category);
        return category;
    }

    public DishCategoryDTO update(String code, UpdateDishCategoryRequest request) {
        DishCategoryDTO existing = store.get(code);
        if (existing == null) {
            throw new IllegalArgumentException("DishCategory not found: " + code);
        }
        DishCategoryDTO updated = new DishCategoryDTO(
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
