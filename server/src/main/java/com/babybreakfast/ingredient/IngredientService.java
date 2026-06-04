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
        // 所有食材关联到二级分类（不再是顶级分类）
        List<IngredientDTO> defaults = List.of(
            // ===== 主食杂粮 → 二级分类 =====
            new IngredientDTO(1L, DEFAULT_FAMILY_ID, "大米", "grain_rice", "g", 0.8, null, List.of("粳米", "米饭"), List.of("主食")),
            new IngredientDTO(2L, DEFAULT_FAMILY_ID, "面粉", "grain_rice", "g", 0.5, null, List.of("小麦粉", "中筋粉"), List.of("主食")),
            new IngredientDTO(10L, DEFAULT_FAMILY_ID, "小米", "grain_rice", "g", 0.75, null, List.of("黄小米"), List.of("主食", "杂粮")),
            new IngredientDTO(11L, DEFAULT_FAMILY_ID, "玉米面", "grain_cereal", "g", 0.6, null, List.of("棒子面"), List.of("主食")),
            new IngredientDTO(12L, DEFAULT_FAMILY_ID, "红薯", "grain_potato", "g", 0.6, null, List.of("地瓜"), List.of("主食", "根茎")),

            // ===== 蛋奶豆类 → 二级分类 =====
            new IngredientDTO(3L, DEFAULT_FAMILY_ID, "鸡蛋", "dairy_egg", "g", 1.0, null, List.of("蛋"), List.of("高蛋白")),
            new IngredientDTO(13L, DEFAULT_FAMILY_ID, "鸭蛋", "dairy_egg", "g", 1.0, null, List.of("青皮鸭蛋"), List.of("高蛋白")),
            new IngredientDTO(4L, DEFAULT_FAMILY_ID, "牛奶", "dairy_milk", "ml", 1.03, null, List.of("鲜奶"), List.of("饮品", "高钙")),
            new IngredientDTO(14L, DEFAULT_FAMILY_ID, "酸奶", "dairy_milk", "ml", 1.05, null, List.of("酸牛奶"), List.of("饮品")),
            new IngredientDTO(15L, DEFAULT_FAMILY_ID, "豆腐", "dairy_bean", "g", 0.8, null, List.of("嫩豆腐"), List.of("高蛋白", "豆制品")),

            // ===== 肉类 → 二级分类 =====
            new IngredientDTO(5L, DEFAULT_FAMILY_ID, "猪肉", "meat_pork", "g", 1.0, null, List.of("瘦肉", "五花肉"), List.of("高蛋白")),
            new IngredientDTO(16L, DEFAULT_FAMILY_ID, "牛肉", "meat_beef", "g", 1.02, null, List.of("牛腩", "牛排"), List.of("高蛋白")),
            new IngredientDTO(17L, DEFAULT_FAMILY_ID, "鸡肉", "meat_poultry", "g", 0.95, null, List.of("鸡胸肉", "鸡腿肉"), List.of("高蛋白")),

            // ===== 水产 → 二级分类 =====
            new IngredientDTO(18L, DEFAULT_FAMILY_ID, "三文鱼", "seafood_fish", "g", 0.95, null, List.of("鲑鱼"), List.of("高蛋白", "DHA")),
            new IngredientDTO(19L, DEFAULT_FAMILY_ID, "虾仁", "seafood_shellfish", "g", 0.9, null, List.of("鲜虾仁"), List.of("高蛋白")),
            new IngredientDTO(20L, DEFAULT_FAMILY_ID, "蛤蜊", "seafood_mollusk", "g", 1.0, null, List.of("花甲"), List.of("海鲜")),

            // ===== 蔬菜 → 二级分类 =====
            new IngredientDTO(6L, DEFAULT_FAMILY_ID, "西红柿", "veg_gourd", "g", 1.0, null, List.of("番茄"), List.of("蔬菜")),
            new IngredientDTO(21L, DEFAULT_FAMILY_ID, "菠菜", "veg_leafy", "g", 0.3, null, List.of("红根菜"), List.of("蔬菜", "补铁")),
            new IngredientDTO(22L, DEFAULT_FAMILY_ID, "胡萝卜", "veg_root", "g", 0.65, null, List.of("红萝卜"), List.of("蔬菜")),
            new IngredientDTO(23L, DEFAULT_FAMILY_ID, "香菇", "veg_mushroom", "g", 0.35, null, List.of("鲜香菇"), List.of("蔬菜")),

            // ===== 水果 → 二级分类 =====
            new IngredientDTO(7L, DEFAULT_FAMILY_ID, "苹果", "fruit_temperate", "g", 0.85, null, List.of("红富士"), List.of("水果")),
            new IngredientDTO(24L, DEFAULT_FAMILY_ID, "香蕉", "fruit_tropical", "g", 0.9, null, List.of("芭蕉"), List.of("水果")),
            new IngredientDTO(25L, DEFAULT_FAMILY_ID, "草莓", "fruit_berry", "g", 0.4, null, List.of("鲜草莓"), List.of("水果")),

            // ===== 坚果 → 二级分类 =====
            new IngredientDTO(26L, DEFAULT_FAMILY_ID, "核桃", "nut_tree", "g", 0.5, null, List.of("核桃仁"), List.of("坚果", "健脑")),
            new IngredientDTO(27L, DEFAULT_FAMILY_ID, "芝麻", "nut_seed", "g", 0.45, null, List.of("白芝麻"), List.of("坚果")),

            // ===== 调味品 → 二级分类 =====
            new IngredientDTO(8L, DEFAULT_FAMILY_ID, "盐", "cond_spice", "g", 1.2, null, List.of("食盐", "精盐"), List.of("调味")),
            new IngredientDTO(28L, DEFAULT_FAMILY_ID, "酱油", "cond_sauce", "ml", 1.15, null, List.of("生抽"), List.of("调味")),
            new IngredientDTO(29L, DEFAULT_FAMILY_ID, "橄榄油", "cond_oil", "ml", 0.92, null, List.of("特级初榨"), List.of("油料")),
            new IngredientDTO(9L, DEFAULT_FAMILY_ID, "白糖", "cond_sauce", "g", 0.85, null, List.of("蔗糖", "白砂糖"), List.of("调味", "甜味"))
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
