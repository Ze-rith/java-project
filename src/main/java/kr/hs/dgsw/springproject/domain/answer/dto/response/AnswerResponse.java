package kr.hs.dgsw.springproject.domain.answer.dto.response;

import kr.hs.dgsw.springproject.domain.answer.domain.Answer;

public record AnswerResponse(
        Long id,
        String content,
        boolean accepted,
        Long questionId,
        Long userId
) {
    public static AnswerResponse of(Answer answer) {
        return new AnswerResponse(
                answer.getId(),
                answer.getContent(),
                answer.isAccepted(),
                answer.getQuestion().getId(),
                answer.getUser().getId()
        );
    }
}
