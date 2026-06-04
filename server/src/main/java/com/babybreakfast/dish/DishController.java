package com.babybreakfast.dish;

import com.babybreakfast.common.ApiResponse;
import com.babybreakfast.dish.dto.CreateDishRequest;
import com.babybreakfast.dish.dto.DishDTO;
import com.babybreakfast.dish.dto.UpdateDishRequest;
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
@RequestMapping("/api/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DishDTO>>> list(
            @RequestParam(required = false) String category
    ) {
        List<DishDTO> dishes = category != null
                ? dishService.findByCategory(category)
                : dishService.findAll();
        return ResponseEntity.ok(ApiResponse.success(dishes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DishDTO>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(dishService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DishDTO>> create(@RequestBody CreateDishRequest request) {
        return ResponseEntity.ok(ApiResponse.success(dishService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DishDTO>> update(
            @PathVariable Long id,
            @RequestBody UpdateDishRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(dishService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        dishService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
