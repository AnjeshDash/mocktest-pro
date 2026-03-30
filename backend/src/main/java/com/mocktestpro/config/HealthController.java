package com.mocktestpro.config;

import com.mocktestpro.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/ping")
    public ApiResponse<Map<String, String>> ping() {
        return ApiResponse.success(
                "Server is running!",
                Map.of("status", "UP", "version", "1.0.0")
        );
    }
}