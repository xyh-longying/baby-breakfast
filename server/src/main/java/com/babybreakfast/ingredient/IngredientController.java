package com.babybreakfast.ingredient;

import com.babybreakfast.common.ApiResponse;
import com.babybreakfast.ingredient.dto.CreateIngredientRequest;
import com.babybreakfast.ingredient.dto.IngredientDTO;
import com.babybreakfast.ingredient.dto.UpdateIngredientRequest;
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
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<IngredientDTO>>> list(
            @RequestParam(required = false) String category
    ) {
        List<IngredientDTO> ingredients = category != null
                ? ingredientService.findByCategory(category)
                : ingredientService.findAll();
        return ResponseEntity.ok(ApiResponse.success(ingredients));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IngredientDTO>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(ingredientService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IngredientDTO>> create(@RequestBody CreateIngredientRequest request) {
        return ResponseEntity.ok(ApiResponse.success(ingredientService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IngredientDTO>> update(
            @PathVariable Long id,
            @RequestBody UpdateIngredientRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(ingredientService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        ingredientService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
