package kr.hs.dgsw.springproject.global.common;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        String message,
        Map<String, String> details,
        LocalDateTime timestamp
) {
    public static ErrorResponse of(String message) {
        return new ErrorResponse(message, null, LocalDateTime.now());
    }

    public static ErrorResponse of(String message, Map<String, String> details) {
        return new ErrorResponse(message, details, LocalDateTime.now());
    }
}