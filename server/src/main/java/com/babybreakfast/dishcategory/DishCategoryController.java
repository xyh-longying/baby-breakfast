package com.babybreakfast.dishcategory;

import com.babybreakfast.common.ApiResponse;
import com.babybreakfast.dishcategory.dto.CreateDishCategoryRequest;
import com.babybreakfast.dishcategory.dto.DishCategoryDTO;
import com.babybreakfast.dishcategory.dto.UpdateDishCategoryRequest;
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
@RequestMapping("/api/dish-categories")
public class DishCategoryController {

    private final DishCategoryService dishCategoryService;

    public DishCategoryController(DishCategoryService dishCategoryService) {
        this.dishCategoryService = dishCategoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DishCategoryDTO>>> list() {
        return ResponseEntity.ok(ApiResponse.success(dishCategoryService.findAll()));
    }

    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<DishCategoryDTO>> get(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(dishCategoryService.findByCode(code)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DishCategoryDTO>> create(@RequestBody CreateDishCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(dishCategoryService.create(request)));
    }

    @PutMapping("/{code}")
    public ResponseEntity<ApiResponse<DishCategoryDTO>> update(
            @PathVariable String code,
            @RequestBody UpdateDishCategoryRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(dishCategoryService.update(code, request)));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String code) {
        dishCategoryService.delete(code);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
