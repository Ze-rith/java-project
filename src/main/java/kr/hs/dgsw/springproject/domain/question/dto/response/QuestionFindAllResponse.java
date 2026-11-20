package kr.hs.dgsw.springproject.domain.question.dto.response;

import kr.hs.dgsw.springproject.domain.question.domain.Question;

import java.util.List;

public record QuestionFindAllResponse(
        Long id,
        String title,
        Long userId,
        String userName
) {
    public static QuestionFindAllResponse of(Question question) {
        return new QuestionFindAllResponse(
                question.getId(),
                question.getTitle(),
                question.getUser().getId(),
                question.getUser().getName()
        );
    }

    public static List<QuestionFindAllResponse> of(List<Question> questions) {
        return questions.stream()
                .map(QuestionFindAllResponse::of)
                .toList();
    }
}
