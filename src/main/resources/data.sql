INSERT INTO users (email, name, password) VALUES ('kim@dgsw.hs.kr',  '김민준', 'password1234');
INSERT INTO users (email, name, password) VALUES ('lee@dgsw.hs.kr',  '이서연', 'password1234');
INSERT INTO users (email, name, password) VALUES ('park@dgsw.hs.kr', '박지호', 'password1234');
INSERT INTO users (email, name, password) VALUES ('choi@dgsw.hs.kr', '최하은', 'password1234');
INSERT INTO users (email, name, password) VALUES ('jung@dgsw.hs.kr', '정도윤', 'password1234');


INSERT INTO questions (title, content, user_id) VALUES
    ('스프링 부트에서 @Transactional 이 동작하지 않아요',
     '같은 클래스 안의 메서드에서 @Transactional 메서드를 호출했는데 롤백이 되지 않습니다. 이유가 뭘까요?',
     (SELECT id FROM users WHERE email = 'kim@dgsw.hs.kr'));

INSERT INTO questions (title, content, user_id) VALUES
    ('JPA N+1 문제 해결 방법이 궁금합니다',
     '질문 목록을 조회할 때 작성자 정보 때문에 쿼리가 여러 번 나갑니다. fetch join 과 @EntityGraph 중 무엇을 써야 하나요?',
     (SELECT id FROM users WHERE email = 'lee@dgsw.hs.kr'));

INSERT INTO questions (title, content, user_id) VALUES
    ('스프링 시큐리티 JWT 필터 순서',
     'JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 두는 이유가 무엇인가요?',
     (SELECT id FROM users WHERE email = 'park@dgsw.hs.kr'));

INSERT INTO questions (title, content, user_id) VALUES
    ('자바 record 와 Lombok @Value 차이',
     'DTO 를 만들 때 record 와 @Value 중 어떤 걸 쓰는 게 좋을까요? 장단점이 궁금합니다.',
     (SELECT id FROM users WHERE email = 'choi@dgsw.hs.kr'));

INSERT INTO questions (title, content, user_id) VALUES
    ('QueryDSL 설정 방법 (Gradle)',
     'Spring Boot 4 에서 QueryDSL 을 설정하려고 하는데 Q 클래스가 생성되지 않습니다.',
     (SELECT id FROM users WHERE email = 'jung@dgsw.hs.kr'));

INSERT INTO questions (title, content, user_id) VALUES
    ('Redis 캐시 TTL 설정',
     '이메일 인증 코드를 Redis 에 저장할 때 만료 시간을 어떻게 설정하나요?',
     (SELECT id FROM users WHERE email = 'kim@dgsw.hs.kr'));

INSERT INTO questions (title, content, user_id) VALUES
    ('자바 스트림 groupingBy 사용법',
     '리스트를 특정 필드 기준으로 묶어서 Map 으로 만들고 싶습니다. 예제가 있을까요?',
     (SELECT id FROM users WHERE email = 'lee@dgsw.hs.kr'));

INSERT INTO questions (title, content, user_id) VALUES
    ('@WebMvcTest 에서 Bean 을 못 찾는 오류',
     '컨트롤러 테스트를 돌리면 Service Bean 이 없다고 나옵니다. @MockitoBean 을 써야 하나요?',
     (SELECT id FROM users WHERE email = 'park@dgsw.hs.kr'));

INSERT INTO questions (title, content, user_id) VALUES
    ('H2 콘솔 접속이 안 됩니다',
     '/h2-console 로 접속하면 JDBC URL 을 뭘 넣어야 하는지 모르겠어요.',
     (SELECT id FROM users WHERE email = 'choi@dgsw.hs.kr'));

INSERT INTO questions (title, content, user_id) VALUES
    ('스프링 부트 프로필 분리 방법',
     'local, dev, prod 환경별로 application.yml 을 나누고 싶습니다.',
     (SELECT id FROM users WHERE email = 'jung@dgsw.hs.kr'));

INSERT INTO questions (title, content, user_id) VALUES
    ('Git 브랜치 전략 추천',
     '학교 팀 프로젝트에서 Git Flow 와 GitHub Flow 중 어떤 걸 쓰는 게 좋을까요?',
     (SELECT id FROM users WHERE email = 'kim@dgsw.hs.kr'));

INSERT INTO questions (title, content, user_id) VALUES
    ('JPA 연관관계 주인은 어떻게 정하나요?',
     '@OneToMany 와 @ManyToOne 양방향일 때 mappedBy 를 어디에 둬야 하는지 헷갈립니다.',
     (SELECT id FROM users WHERE email = 'lee@dgsw.hs.kr'));

INSERT INTO answers (content, accepted, question_id, user_id) VALUES
    ('같은 클래스 내부 호출은 프록시를 거치지 않아서 트랜잭션이 적용되지 않습니다. 별도 빈으로 분리해 보세요.',
     TRUE,
     (SELECT id FROM questions WHERE title = '스프링 부트에서 @Transactional 이 동작하지 않아요'),
     (SELECT id FROM users WHERE email = 'lee@dgsw.hs.kr'));

INSERT INTO answers (content, accepted, question_id, user_id) VALUES
    ('self-injection 으로도 해결할 수 있지만 구조상 좋지 않아서 클래스 분리를 추천합니다.',
     FALSE,
     (SELECT id FROM questions WHERE title = '스프링 부트에서 @Transactional 이 동작하지 않아요'),
     (SELECT id FROM users WHERE email = 'park@dgsw.hs.kr'));

INSERT INTO answers (content, accepted, question_id, user_id) VALUES
    ('단순 조회라면 @EntityGraph 로 충분하고, 조건이 복잡하면 QueryDSL fetchJoin 을 쓰세요.',
     TRUE,
     (SELECT id FROM questions WHERE title = 'JPA N+1 문제 해결 방법이 궁금합니다'),
     (SELECT id FROM users WHERE email = 'kim@dgsw.hs.kr'));

INSERT INTO answers (content, accepted, question_id, user_id) VALUES
    ('컬렉션 fetch join 은 페이징과 같이 쓰면 메모리에서 페이징되니 주의하세요. batch size 설정도 방법입니다.',
     FALSE,
     (SELECT id FROM questions WHERE title = 'JPA N+1 문제 해결 방법이 궁금합니다'),
     (SELECT id FROM users WHERE email = 'jung@dgsw.hs.kr'));

INSERT INTO answers (content, accepted, question_id, user_id) VALUES
    ('폼 로그인 필터보다 먼저 토큰을 검사해서 SecurityContext 에 인증 정보를 넣어야 하기 때문입니다.',
     FALSE,
     (SELECT id FROM questions WHERE title = '스프링 시큐리티 JWT 필터 순서'),
     (SELECT id FROM users WHERE email = 'choi@dgsw.hs.kr'));

INSERT INTO answers (content, accepted, question_id, user_id) VALUES
    ('Java 16 이상이면 record 를 추천합니다. 불변이고 별도 라이브러리가 필요 없습니다.',
     TRUE,
     (SELECT id FROM questions WHERE title = '자바 record 와 Lombok @Value 차이'),
     (SELECT id FROM users WHERE email = 'park@dgsw.hs.kr'));

INSERT INTO answers (content, accepted, question_id, user_id) VALUES
    ('annotationProcessor 에 querydsl-apt 의 jakarta classifier 를 추가했는지 확인해 보세요.',
     FALSE,
     (SELECT id FROM questions WHERE title = 'QueryDSL 설정 방법 (Gradle)'),
     (SELECT id FROM users WHERE email = 'lee@dgsw.hs.kr'));

INSERT INTO answers (content, accepted, question_id, user_id) VALUES
    ('RedisTemplate 의 opsForValue().set(key, value, Duration.ofMinutes(5)) 처럼 Duration 을 넘기면 됩니다.',
     TRUE,
     (SELECT id FROM questions WHERE title = 'Redis 캐시 TTL 설정'),
     (SELECT id FROM users WHERE email = 'jung@dgsw.hs.kr'));

INSERT INTO answers (content, accepted, question_id, user_id) VALUES
    ('Collectors.groupingBy(User::getName) 처럼 쓰면 Map<String, List<User>> 가 됩니다.',
     FALSE,
     (SELECT id FROM questions WHERE title = '자바 스트림 groupingBy 사용법'),
     (SELECT id FROM users WHERE email = 'choi@dgsw.hs.kr'));

INSERT INTO answers (content, accepted, question_id, user_id) VALUES
    ('네, @WebMvcTest 는 웹 계층만 띄우기 때문에 Service 는 @MockitoBean 으로 등록해야 합니다.',
     TRUE,
     (SELECT id FROM questions WHERE title = '@WebMvcTest 에서 Bean 을 못 찾는 오류'),
     (SELECT id FROM users WHERE email = 'kim@dgsw.hs.kr'));

INSERT INTO answers (content, accepted, question_id, user_id) VALUES
    ('datasource url 을 지정하지 않으면 매번 랜덤 이름이 붙습니다. 실행 로그에서 "H2 console available at" 줄을 확인하세요.',
     FALSE,
     (SELECT id FROM questions WHERE title = 'H2 콘솔 접속이 안 됩니다'),
     (SELECT id FROM users WHERE email = 'park@dgsw.hs.kr'));

INSERT INTO answers (content, accepted, question_id, user_id) VALUES
    ('application-local.yml 처럼 파일을 나누고 spring.profiles.active 로 선택하면 됩니다.',
     FALSE,
     (SELECT id FROM questions WHERE title = '스프링 부트 프로필 분리 방법'),
     (SELECT id FROM users WHERE email = 'lee@dgsw.hs.kr'));

INSERT INTO answers (content, accepted, question_id, user_id) VALUES
    ('mappedBy 가 없는 쪽, 즉 외래키를 가진 @ManyToOne 쪽이 연관관계의 주인입니다.',
     TRUE,
     (SELECT id FROM questions WHERE title = 'JPA 연관관계 주인은 어떻게 정하나요?'),
     (SELECT id FROM users WHERE email = 'choi@dgsw.hs.kr'));

INSERT INTO answers (content, accepted, question_id, user_id) VALUES
    ('주인 쪽에서만 외래키를 변경할 수 있으니, 편의 메서드로 양쪽을 같이 세팅해 주세요.',
     FALSE,
     (SELECT id FROM questions WHERE title = 'JPA 연관관계 주인은 어떻게 정하나요?'),
     (SELECT id FROM users WHERE email = 'jung@dgsw.hs.kr'));
