package kr.hs.dgsw.springproject.answer.domain;

import jakarta.persistence.*;
import kr.hs.dgsw.springproject.auth.domain.User;
import kr.hs.dgsw.springproject.question.domain.Question;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "answers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean accepted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Answer(String content, Question question, User user) {
        this.content = content;
        this.question = question;
        this.user = user;
    }

    public void update(String content) {
        this.content = content;
    }

    public void accept() {
        this.accepted = true;
    }
}