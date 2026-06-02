package com.babybreakfast.category;

import com.babybreakfast.common.ApiResponse;
import com.babybreakfast.category.dto.CreateIngredientCategoryRequest;
import com.babybreakfast.category.dto.IngredientCategoryDTO;
import com.babybreakfast.category.dto.UpdateIngredientCategoryRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingredient-categories")
public class IngredientCategoryController {

    private final IngredientCategoryService categoryService;

    public IngredientCategoryController(IngredientCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<IngredientCategoryDTO>>> list() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.findAll()));
    }

    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<IngredientCategoryDTO>> get(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.findByCode(code)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IngredientCategoryDTO>> create(@RequestBody CreateIngredientCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.create(request)));
    }

    @PutMapping("/{code}")
    public ResponseEntity<ApiResponse<IngredientCategoryDTO>> update(
            @PathVariable String code,
            @RequestBody UpdateIngredientCategoryRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.update(code, request)));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String code) {
        categoryService.delete(code);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
