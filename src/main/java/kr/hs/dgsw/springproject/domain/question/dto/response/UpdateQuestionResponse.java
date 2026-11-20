package kr.hs.dgsw.springproject.domain.question.dto.response;

import kr.hs.dgsw.springproject.domain.question.domain.Question;

public record UpdateQuestionResponse(
        Long id,
        String title,
        String content,
        Long userId,
        String userName
) {
    public static UpdateQuestionResponse of(Question question) {
        return new UpdateQuestionResponse(
                question.getId(),
                question.getTitle(),
                question.getContent(),
                question.getUser().getId(),
                question.getUser().getName()
        );
    }
}
