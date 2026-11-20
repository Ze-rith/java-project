package kr.hs.dgsw.springproject.domain.question.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateQuestionRequest(
        @NotNull
        Long userId,
        @NotBlank
        @Size(max = 200)
        String title,
        @NotBlank
        String content
) {
}
