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
        // ===== 一级分类（顶级） =====
        List<IngredientCategoryDTO> topCategories = List.of(
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

        // ===== 二级分类（子分类） =====
        // 主食杂粮的子类
        List<IngredientCategoryDTO> grainSubs = List.of(
            new IngredientCategoryDTO(101L, DEFAULT_FAMILY_ID, "米面类", "grain_rice", "grain", 11, "🍚", "#F5A623", List.of("大米", "小米", "面粉", "面条")),
            new IngredientCategoryDTO(102L, DEFAULT_FAMILY_ID, "薯类", "grain_potato", "grain", 12, "🥔", "#D4A76A", List.of("土豆", "红薯", "紫薯", "山药")),
            new IngredientCategoryDTO(103L, DEFAULT_FAMILY_ID, "杂粮", "grain_cereal", "grain", 13, "🌽", "#C8A94E", List.of("玉米", "燕麦", "荞麦", "藜麦"))
        );

        // 蛋奶豆类的子类
        List<IngredientCategoryDTO> dairySubs = List.of(
            new IngredientCategoryDTO(201L, DEFAULT_FAMILY_ID, "蛋类", "dairy_egg", "dairy", 21, "🥚", "#FFE4B5", List.of("鸡蛋", "鸭蛋", "鹌鹑蛋")),
            new IngredientCategoryDTO(202L, DEFAULT_FAMILY_ID, "奶制品", "dairy_milk", "dairy", 22, "🥛", "#FFF8DC", List.of("牛奶", "酸奶", "奶酪")),
            new IngredientCategoryDTO(203L, DEFAULT_FAMILY_ID, "豆制品", "dairy_bean", "dairy", 23, "🧈", "#F5DEB3", List.of("豆腐", "豆浆", "豆皮"))
        );

        // 肉类的子类
        List<IngredientCategoryDTO> meatSubs = List.of(
            new IngredientCategoryDTO(301L, DEFAULT_FAMILY_ID, "猪肉", "meat_pork", "meat", 31, "🐷", "#F08080", List.of("瘦肉", "五花肉", "里脊")),
            new IngredientCategoryDTO(302L, DEFAULT_FAMILY_ID, "牛肉", "meat_beef", "meat", 32, "🐮", "#CD5C5C", List.of("牛腩", "牛排", "牛肉末")),
            new IngredientCategoryDTO(303L, DEFAULT_FAMILY_ID, "禽肉", "meat_poultry", "meat", 33, "🍗", "#FA8072", List.of("鸡肉", "鸭肉", "鸡翅"))
        );

        // 水产的子类
        List<IngredientCategoryDTO> seafoodSubs = List.of(
            new IngredientCategoryDTO(401L, DEFAULT_FAMILY_ID, "鱼类", "seafood_fish", "seafood", 41, "🐠", "#87CEEB", List.of("三文鱼", "鳕鱼", "鲈鱼")),
            new IngredientCategoryDTO(402L, DEFAULT_FAMILY_ID, "虾蟹类", "seafood_shellfish", "seafood", 42, "🦐", "#FFA07A", List.of("虾仁", "螃蟹", "龙虾")),
            new IngredientCategoryDTO(403L, DEFAULT_FAMILY_ID, "贝类", "seafood_mollusk", "seafood", 43, "🦪", "#DDA0DD", List.of("蛤蜊", "扇贝", "生蚝"))
        );

        // 蔬菜的子类
        List<IngredientCategoryDTO> vegSubs = List.of(
            new IngredientCategoryDTO(501L, DEFAULT_FAMILY_ID, "叶菜类", "veg_leafy", "vegetable", 51, "🥬", "#32CD32", List.of("菠菜", "生菜", "小白菜")),
            new IngredientCategoryDTO(502L, DEFAULT_FAMILY_ID, "根茎类", "veg_root", "vegetable", 52, "🥕", "#FF8C00", List.of("胡萝卜", "白萝卜", "莲藕")),
            new IngredientCategoryDTO(503L, DEFAULT_FAMILY_ID, "瓜果类", "veg_gourd", "vegetable", 53, "🥒", "#9ACD32", List.of("黄瓜", "南瓜", "西红柿")),
            new IngredientCategoryDTO(504L, DEFAULT_FAMILY_ID, "菌菇类", "veg_mushroom", "vegetable", 54, "🍄", "#D2691E", List.of("香菇", "金针菇", "木耳"))
        );

        // 水果的子类
        List<IngredientCategoryDTO> fruitSubs = List.of(
            new IngredientCategoryDTO(601L, DEFAULT_FAMILY_ID, "温带水果", "fruit_temperate", "fruit", 61, "🍎", "#FF6347", List.of("苹果", "梨", "桃子")),
            new IngredientCategoryDTO(602L, DEFAULT_FAMILY_ID, "热带水果", "fruit_tropical", "fruit", 62, "🍌", "#FFD700", List.of("香蕉", "芒果", "菠萝")),
            new IngredientCategoryDTO(603L, DEFAULT_FAMILY_ID, "浆果类", "fruit_berry", "fruit", 63, "🫐", "#4169E1", List.of("草莓", "蓝莓", "葡萄"))
        );

        // 坚果的子类
        List<IngredientCategoryDTO> nutSubs = List.of(
            new IngredientCategoryDTO(701L, DEFAULT_FAMILY_ID, "树坚果", "nut_tree", "nut", 71, "🥜", "#A0522D", List.of("核桃", "杏仁", "腰果")),
            new IngredientCategoryDTO(702L, DEFAULT_FAMILY_ID, "种子类", "nut_seed", "nut", 72, "🌻", "#DAA520", List.of("芝麻", "葵花籽", "南瓜籽"))
        );

        // 调味品的子类
        List<IngredientCategoryDTO> condSubs = List.of(
            new IngredientCategoryDTO(801L, DEFAULT_FAMILY_ID, "油类", "cond_oil", "condiment", 81, "🫒", "#FFDAB9", List.of("橄榄油", "花生油", "香油")),
            new IngredientCategoryDTO(802L, DEFAULT_FAMILY_ID, "酱料", "cond_sauce", "condiment", 82, "🍯", "#8B4513", List.of("酱油", "番茄酱", "沙拉酱")),
            new IngredientCategoryDTO(803L, DEFAULT_FAMILY_ID, "香料", "cond_spice", "condiment", 83, "🌿", "#556B2F", List.of("葱", "姜", "蒜", "香菜"))
        );

        // 合并所有数据
        List<IngredientCategoryDTO> allCategories = new ArrayList<>(topCategories);
        allCategories.addAll(grainSubs);
        allCategories.addAll(dairySubs);
        allCategories.addAll(meatSubs);
        allCategories.addAll(seafoodSubs);
        allCategories.addAll(vegSubs);
        allCategories.addAll(fruitSubs);
        allCategories.addAll(nutSubs);
        allCategories.addAll(condSubs);

        allCategories.forEach(cat -> store.put(cat.code(), cat));
        idGenerator.set(1000);
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
