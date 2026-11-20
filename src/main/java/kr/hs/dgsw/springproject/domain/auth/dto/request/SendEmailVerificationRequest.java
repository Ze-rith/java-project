package kr.hs.dgsw.springproject.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendEmailVerificationRequest(
        @NotBlank
        @Email
        String email
) {
}
