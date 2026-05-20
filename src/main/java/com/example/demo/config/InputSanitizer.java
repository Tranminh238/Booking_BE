package com.example.demo.config;

import org.springframework.stereotype.Component;
import java.util.List;
@Component
public class InputSanitizer {
    private static final List<String> INJECTION_PATTERNS = List.of(
        "ignore previous",
        "ignore all instructions",
        "forget your instructions",
        "you are now",
        "act as",
        "pretend you are",
        "system prompt",
        "reveal your prompt",
        "what are your instructions",
        "bypass",
        "jailbreak",
        "từ bây giờ bạn là",
        "bỏ qua hướng dẫn",
        "tiết lộ prompt",
        "quên đi nhiệm vụ"
    );

    public String sanitize(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Tin nhắn không được để trống");
        }
        if (input.length() > 500) {
            input = input.substring(0, 500);
        }
        String lower = input.toLowerCase();
        boolean isInjection = INJECTION_PATTERNS.stream()
            .anyMatch(lower::contains);
        if (isInjection) {
            throw new SecurityException("INJECTION_DETECTED");
        }
        return input.replaceAll("[<>{}\\[\\]`]", "").trim();
    }
}
