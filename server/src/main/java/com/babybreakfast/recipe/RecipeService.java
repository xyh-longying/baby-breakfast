package com.babybreakfast.recipe;

import com.babybreakfast.recipe.dto.CreateRecipeRequest;
import com.babybreakfast.recipe.dto.RecipeDTO;
import com.babybreakfast.recipe.dto.UpdateRecipeRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class RecipeService {

    private final ConcurrentHashMap<Long, RecipeDTO> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private static final Long DEFAULT_FAMILY_ID = 1L;

    public RecipeService() {
        List<RecipeDTO> defaults = List.of(
            new RecipeDTO(1L, DEFAULT_FAMILY_ID, "白粥食谱", 1L, "白粥", 2,
                List.of(
                    "大米淘洗干净，用清水浸泡30分钟",
                    "锅中加入适量清水（米水比例约1:8），大火烧开",
                    "放入浸泡好的大米，用勺子轻轻搅动防止粘锅",
                    "烧开后转中小火，保持微沸状态熬煮约30分钟",
                    "期间每隔5分钟搅拌一次，防止糊底",
                    "煮至米粒软烂、汤汁浓稠即可出锅"
                ),
                List.of(
                    new RecipeDTO.RecipeIngredientItem("大米", "100", "g"),
                    new RecipeDTO.RecipeIngredientItem("清水", "800", "ml")
                ),
                "粥的浓稠度可根据个人喜好调整水量；煮粥时加盖可减少水分蒸发", null),

            new RecipeDTO(2L, DEFAULT_FAMILY_ID, "小米粥食谱", 2L, "小米粥", 2,
                List.of(
                    "小米用清水轻轻淘洗2遍，去除杂质",
                    "锅中加入适量清水（米水比例约1:10），大火烧开",
                    "再次烧开后转小火慢熬25-30分钟",
                    "期间不时用勺子顺同一方向搅拌，使粥更绵滑",
                    "煮至小米开花、表面浮现米油即可关火"
                ),
                List.of(
                    new RecipeDTO.RecipeIngredientItem("小米", "80", "g"),
                    new RecipeDTO.RecipeIngredientItem("清水", "800", "ml")
                ),
                "小米不需过度清洗以免流失营养；可加入几滴食用油使粥更香滑", null),

            new RecipeDTO(3L, DEFAULT_FAMILY_ID, "鸡蛋煎饼食谱", 3L, "鸡蛋煎饼", 2,
                List.of(
                    "面粉倒入碗中，慢慢加水搅拌成无颗粒的面糊",
                    "打入鸡蛋，加入盐和少许油，搅拌均匀",
                    "平底锅刷薄油烧热，舀入一勺面糊摊成圆饼",
                    "小火煎至底部凝固定型后翻面",
                    "两面煎至金黄即可出锅，可配酱料食用"
                ),
                List.of(
                    new RecipeDTO.RecipeIngredientItem("面粉", "150", "g"),
                    new RecipeDTO.RecipeIngredientItem("鸡蛋", "2", "个"),
                    new RecipeDTO.RecipeIngredientItem("清水", "200", "ml"),
                    new RecipeDTO.RecipeIngredientItem("盐", "2", "g"),
                    new RecipeDTO.RecipeIngredientItem("食用油", "10", "ml")
                ),
                "面糊稠度以能缓慢流动为宜；煎制时用小火避免外焦内生", null),

            new RecipeDTO(4L, DEFAULT_FAMILY_ID, "西红柿鸡蛋面食谱", 5L, "西红柿鸡蛋面", 2,
                List.of(
                    "西红柿洗净切成小块，鸡蛋打散加少许盐备用",
                    "锅中热油，倒入蛋液炒熟盛出",
                    "锅内再加少许油，放入西红柿块翻炒出汁",
                    "加入适量清水烧开，调入盐和生抽",
                    "另起锅煮面条至熟透捞出",
                    "将炒好的鸡蛋倒回汤中煮沸，浇在面条上即可"
                ),
                List.of(
                    new RecipeDTO.RecipeIngredientItem("面条", "200", "g"),
                    new RecipeDTO.RecipeIngredientItem("西红柿", "2", "个"),
                    new RecipeDTO.RecipeIngredientItem("鸡蛋", "2", "个"),
                    new RecipeDTO.RecipeIngredientItem("生抽", "10", "ml"),
                    new RecipeDTO.RecipeIngredientItem("盐", "3", "g"),
                    new RecipeDTO.RecipeIngredientItem("食用油", "15", "ml")
                ),
                "西红柿选熟透的更容易出汁；面条不要煮过久保持劲道口感", null),

            new RecipeDTO(5L, DEFAULT_FAMILY_ID, "豆浆食谱", 6L, "豆浆", 4,
                List.of(
                    "黄豆提前一晚用清水浸泡，泡发后沥干水分",
                    "将泡好的黄豆放入豆浆机或破壁机",
                    "按豆水比例1:8-1:10加入清水",
                    "启动豆浆程序，打磨煮熟约20分钟",
                    "用细滤网过滤掉豆渣，趁热饮用",
                    "可根据个人口味添加白糖调味"
                ),
                List.of(
                    new RecipeDTO.RecipeIngredientItem("黄豆", "100", "g"),
                    new RecipeDTO.RecipeIngredientItem("清水", "1000", "ml"),
                    new RecipeDTO.RecipeIngredientItem("白糖", "15", "g")
                ),
                "黄豆需充分泡发否则影响出浆率；豆浆必须彻底煮熟才能饮用", null)
        );
        defaults.forEach(recipe -> store.put(recipe.id(), recipe));
        idGenerator.set(100);
    }

    public List<RecipeDTO> findAll() {
        return new ArrayList<>(store.values());
    }

    public RecipeDTO findById(Long id) {
        return store.get(id);
    }

    public List<RecipeDTO> findByDishId(Long dishId) {
        return store.values().stream()
                .filter(recipe -> dishId.equals(recipe.dishId()))
                .toList();
    }

    public RecipeDTO create(CreateRecipeRequest request) {
        Long id = idGenerator.getAndIncrement();
        RecipeDTO recipe = new RecipeDTO(
                id, DEFAULT_FAMILY_ID, request.name(), request.dishId(), null,
                request.servings() != null ? request.servings() : 2,
                request.steps(),
                request.ingredientItems(),
                request.tips(),
                request.imageUrl()
        );
        store.put(id, recipe);
        return recipe;
    }

    public RecipeDTO update(Long id, UpdateRecipeRequest request) {
        RecipeDTO existing = store.get(id);
        if (existing == null) {
            throw new IllegalArgumentException("Recipe not found: " + id);
        }
        RecipeDTO updated = new RecipeDTO(
                id,
                existing.familyId(),
                request.name() != null ? request.name() : existing.name(),
                request.dishId() != null ? request.dishId() : existing.dishId(),
                existing.dishName(),
                request.servings() != null ? request.servings() : existing.servings(),
                request.steps() != null ? request.steps() : existing.steps(),
                request.ingredientItems() != null ? request.ingredientItems() : existing.ingredientItems(),
                request.tips() != null ? request.tips() : existing.tips(),
                request.imageUrl() != null ? request.imageUrl() : existing.imageUrl()
        );
        store.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        store.remove(id);
    }
}
