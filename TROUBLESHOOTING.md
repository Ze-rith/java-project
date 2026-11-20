# 트러블슈팅 메모

## 회원 삭제 시 외래 키 제약 오류

### 증상

질문이나 답변이 연결된 회원을 삭제하면, 다른 테이블의 외래 키가 회원을 참조하고 있어 삭제가 실패했다.

### 원인

`Question.user`는 `questions.user_id`로 회원을 참조하고, `Answer.user`는 `answers.user_id`로 회원을 참조한다. 질문에는 답변 목록의 삭제 cascade가 있지만, 회원을 바로 삭제할 때는 회원이 쓴 답변과 회원의 질문에 달린 다른 사용자의 답변이 먼저 정리되지 않았다.

### 수정

하나의 트랜잭션 안에서 다음 순서로 삭제한다.

1. 해당 회원이 작성한 답변
2. 해당 회원의 질문에 달린 모든 답변
3. 해당 회원이 작성한 질문
4. 회원

### 검증

H2 통합 테스트에서 회원이 작성한 질문·답변과 다른 사용자가 그 질문에 작성한 답변을 만든 뒤 삭제했다. 회원과 해당 질문·답변이 모두 삭제되는 것을 확인했다.

실행 명령은 `./gradlew test --tests 'kr.hs.dgsw.springproject.domain.user.service.UserServiceIntegrationTest.delete_removesUserQuestionsAndAnswers' --no-problems-report`이며 통과했다. 이 검증은 H2 통합 테스트이고, Postman에서 실제 실행 중인 서버에 DELETE 요청을 보낸 검증은 아니다.
