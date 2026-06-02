package com.babybreakfast.api;

import com.babybreakfast.common.ApiResponse;
import java.time.OffsetDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ApiResponse<HealthPayload> health() {
        return ApiResponse.success(
                new HealthPayload("baby-breakfast-server", OffsetDateTime.now().toString())
        );
    }

    public record HealthPayload(String service, String timestamp) {
    }
}
