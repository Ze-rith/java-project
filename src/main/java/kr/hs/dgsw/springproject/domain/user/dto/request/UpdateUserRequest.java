package kr.hs.dgsw.springproject.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank
        String name,
        @NotBlank
        String email
) {
}
