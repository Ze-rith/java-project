package kr.hs.dgsw.springproject.domain.answer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAnswerRequest(
        @NotBlank(message = "답변 내용은 비어 있을 수 없습니다.")
        String content,
        @NotNull(message = "작성자 ID는 필수입니다.")
        Long userId
) {
}
