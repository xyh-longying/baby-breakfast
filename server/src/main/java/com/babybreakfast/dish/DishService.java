package com.babybreakfast.dish;

import com.babybreakfast.dish.dto.CreateDishRequest;
import com.babybreakfast.dish.dto.DishDTO;
import com.babybreakfast.dish.dto.UpdateDishRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class DishService {

    private final ConcurrentHashMap<Long, DishDTO> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private static final Long DEFAULT_FAMILY_ID = 1L;

    public DishService() {
        List<DishDTO> defaults = List.of(
            new DishDTO(1L, DEFAULT_FAMILY_ID, "白粥", "porridge", "经典养胃白粥，口感绵软", 1, 5, 45, null, List.of("养胃", "易消化")),
            new DishDTO(2L, DEFAULT_FAMILY_ID, "小米粥", "porridge", "营养小米粥，健脾益胃", 1, 5, 40, null, List.of("营养", "健脾")),
            new DishDTO(3L, DEFAULT_FAMILY_ID, "鸡蛋煎饼", "pastry", "香脆可口的鸡蛋煎饼", 2, 10, 15, null, List.of("高蛋白", "快手")),
            new DishDTO(4L, DEFAULT_FAMILY_ID, "牛奶麦片", "staple", "温热牛奶搭配燕麦片", 1, 3, 5, null, List.of("营养", "快手")),
            new DishDTO(5L, DEFAULT_FAMILY_ID, "西红柿鸡蛋面", "staple", "家常西红柿鸡蛋面，酸甜开胃", 2, 10, 15, null, List.of("家常", "开胃")),
            new DishDTO(6L, DEFAULT_FAMILY_ID, "豆浆", "drink", "现磨豆浆，豆香浓郁", 1, 5, 20, null, List.of("饮品", "高蛋白")),
            new DishDTO(7L, DEFAULT_FAMILY_ID, "包子", "pastry", "松软肉包或素包", 3, 20, 25, null, List.of("面点", "传统")),
            new DishDTO(8L, DEFAULT_FAMILY_ID, "紫菜蛋花汤", "soup", "清淡紫菜蛋花汤，鲜美可口", 1, 5, 10, null, List.of("汤羹", "清淡")),
            new DishDTO(9L, DEFAULT_FAMILY_ID, "红薯粥", "porridge", "香甜红薯粥，富含膳食纤维", 1, 8, 35, null, List.of("粗粮", "甜味")),
            new DishDTO(10L, DEFAULT_FAMILY_ID, "馄饨", "pastry", "皮薄馅嫩的馄饨配清汤", 2, 15, 15, null, List.of("面点", "鲜美"))
        );
        defaults.forEach(dish -> store.put(dish.id(), dish));
        idGenerator.set(100);
    }

    public List<DishDTO> findAll() {
        return new ArrayList<>(store.values());
    }

    public DishDTO findById(Long id) {
        return store.get(id);
    }

    public List<DishDTO> findByCategory(String categoryCode) {
        return store.values().stream()
                .filter(dish -> categoryCode.equals(dish.categoryCode()))
                .toList();
    }

    public DishDTO create(CreateDishRequest request) {
        Long id = idGenerator.getAndIncrement();
        DishDTO dish = new DishDTO(
                id, DEFAULT_FAMILY_ID, request.name(), request.categoryCode(), request.description(),
                request.difficulty() != null ? request.difficulty() : 1,
                request.prepTimeMin() != null ? request.prepTimeMin() : 0,
                request.cookTimeMin() != null ? request.cookTimeMin() : 0,
                request.imageUrl(), request.tags()
        );
        store.put(id, dish);
        return dish;
    }

    public DishDTO update(Long id, UpdateDishRequest request) {
        DishDTO existing = store.get(id);
        if (existing == null) {
            throw new IllegalArgumentException("Dish not found: " + id);
        }
        DishDTO updated = new DishDTO(
                id,
                existing.familyId(),
                request.name() != null ? request.name() : existing.name(),
                request.categoryCode() != null ? request.categoryCode() : existing.categoryCode(),
                request.description() != null ? request.description() : existing.description(),
                request.difficulty() != null ? request.difficulty() : existing.difficulty(),
                request.prepTimeMin() != null ? request.prepTimeMin() : existing.prepTimeMin(),
                request.cookTimeMin() != null ? request.cookTimeMin() : existing.cookTimeMin(),
                request.imageUrl() != null ? request.imageUrl() : existing.imageUrl(),
                request.tags() != null ? request.tags() : existing.tags()
        );
        store.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        store.remove(id);
    }
}
