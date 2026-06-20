package com.userservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/user")
    public String userFallback() {
        return "User Service временно недоступен. Попробуйте позже.";
    }

    @GetMapping("/fallback/notification")
    public String notificationFallback() {
        return "Notification Service временно недоступен.";
    }
}