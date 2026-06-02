package com.babybreakfast.recipe;

import com.babybreakfast.common.ApiResponse;
import com.babybreakfast.recipe.dto.CreateRecipeRequest;
import com.babybreakfast.recipe.dto.RecipeDTO;
import com.babybreakfast.recipe.dto.UpdateRecipeRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecipeDTO>>> list(
            @RequestParam(required = false) Long dishId
    ) {
        List<RecipeDTO> recipes = dishId != null
                ? recipeService.findByDishId(dishId)
                : recipeService.findAll();
        return ResponseEntity.ok(ApiResponse.success(recipes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecipeDTO>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(recipeService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RecipeDTO>> create(@RequestBody CreateRecipeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(recipeService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RecipeDTO>> update(
            @PathVariable Long id,
            @RequestBody UpdateRecipeRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(recipeService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        recipeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
