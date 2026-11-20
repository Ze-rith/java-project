package kr.hs.dgsw.springproject.domain.answer.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateAnswerRequest(
        @NotBlank(message = "답변 내용은 비어 있을 수 없습니다.")
        String content
) {
}
