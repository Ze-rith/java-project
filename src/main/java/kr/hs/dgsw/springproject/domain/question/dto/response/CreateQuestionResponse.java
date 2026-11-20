package kr.hs.dgsw.springproject.domain.question.dto.response;

import kr.hs.dgsw.springproject.domain.question.domain.Question;

public record CreateQuestionResponse(
        Long id,
        String title,
        String content,
        Long userId,
        String userName
) {
    public static CreateQuestionResponse of(Question question) {
        return new CreateQuestionResponse(
                question.getId(),
                question.getTitle(),
                question.getContent(),
                question.getUser().getId(),
                question.getUser().getName()
        );
    }
}
