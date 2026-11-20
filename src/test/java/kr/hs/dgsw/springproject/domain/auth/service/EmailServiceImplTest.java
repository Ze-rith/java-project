package kr.hs.dgsw.springproject.domain.auth.service;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityManager;
import kr.hs.dgsw.springproject.domain.auth.domain.EmailVerification;
import kr.hs.dgsw.springproject.domain.auth.exception.AuthStatusCode;
import kr.hs.dgsw.springproject.domain.auth.repository.EmailVerificationRepository;
import kr.hs.dgsw.springproject.domain.user.domain.User;
import kr.hs.dgsw.springproject.domain.user.exception.UserStatusCode;
import kr.hs.dgsw.springproject.domain.user.repository.UserRepository;
import kr.hs.dgsw.springproject.global.exception.ApplicationException;
import kr.hs.dgsw.springproject.global.exception.statuscode.StatusCode;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DataJpaTest
@DisplayName("이메일 인증 서비스 통합 테스트")
class EmailServiceImplTest {
    private static final String EMAIL = "verify@example.com";
    private static final String CODE = "123456";

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private JavaMailSender javaMailSender;
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        javaMailSender = mock(JavaMailSender.class);
        when(javaMailSender.createMimeMessage()).thenAnswer(invocation -> new MimeMessage((Session) null));

        TemplateEngine templateEngine = mock(TemplateEngine.class);
        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("<p>인증 코드</p>");

        emailService = new EmailServiceImpl(javaMailSender, emailVerificationRepository, templateEngine, userRepository);
    }

    private void saveVerification(EmailVerification verification) {
        emailVerificationRepository.saveAndFlush(verification);
        entityManager.clear();
    }

    private EmailVerification findVerification() {
        entityManager.flush();
        entityManager.clear();
        return emailVerificationRepository.findById(EMAIL).orElseThrow();
    }

    private void assertStatusCode(ThrowingCallable callable, StatusCode statusCode) {
        assertThatThrownBy(callable)
                .isInstanceOf(ApplicationException.class)
                .extracting(exception -> ((ApplicationException) exception).getStatusCode())
                .isEqualTo(statusCode);
    }

    @Nested
    @DisplayName("인증 코드 발송")
    class SendCode {

        @Test
        @DisplayName("인증 코드를 메일로 보내고 미인증 상태로 DB에 저장한다")
        void sendCode_sendsMailAndSavesVerification() {
            emailService.sendCode(EMAIL);

            EmailVerification found = findVerification();
            assertThat(found.getCode()).matches("\\d{6}");
            assertThat(found.isVerified()).isFalse();
            assertThat(found.getExpiresAt()).isAfter(found.getSentAt());
            verify(javaMailSender).send(any(MimeMessage.class));
        }

        @Test
        @DisplayName("이미 가입된 이메일이면 EMAIL_ALREADY_EXISTS 예외가 발생하고 메일을 보내지 않는다")
        void sendCode_whenEmailAlreadyExists_throwsEmailAlreadyExists() {
            userRepository.saveAndFlush(User.builder()
                    .name("테스트 사용자")
                    .email(EMAIL)
                    .password("password")
                    .build());

            assertStatusCode(() -> emailService.sendCode(EMAIL), UserStatusCode.EMAIL_ALREADY_EXISTS);
            assertThat(emailVerificationRepository.existsById(EMAIL)).isFalse();
            verify(javaMailSender, never()).send(any(MimeMessage.class));
        }

        @Test
        @DisplayName("1분 안에 다시 요청하면 EMAIL_VERIFICATION_RESEND_TOO_SOON 예외가 발생한다")
        void sendCode_whenRequestedWithinCooldown_throwsResendTooSoon() {
            saveVerification(EmailVerification.issue(EMAIL, CODE, LocalDateTime.now()));

            assertStatusCode(() -> emailService.sendCode(EMAIL), AuthStatusCode.EMAIL_VERIFICATION_RESEND_TOO_SOON);
            verify(javaMailSender, never()).send(any(MimeMessage.class));
        }

        @Test
        @DisplayName("1분이 지난 뒤 다시 요청하면 새 코드로 덮어쓴다")
        void sendCode_afterCooldown_overwritesVerification() {
            LocalDateTime previousSentAt = LocalDateTime.now().minusMinutes(2);
            saveVerification(EmailVerification.issue(EMAIL, CODE, previousSentAt));

            emailService.sendCode(EMAIL);

            EmailVerification found = findVerification();
            assertThat(found.getSentAt()).isAfter(previousSentAt);
            assertThat(found.isVerified()).isFalse();
            assertThat(emailVerificationRepository.count()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("인증 코드 확인")
    class VerifyCode {

        @Test
        @DisplayName("코드가 일치하면 인증 완료 상태로 바꾸고 유효 시간을 연장한다")
        void verifyCode_whenCodeMatches_marksVerified() {
            LocalDateTime sentAt = LocalDateTime.now();
            saveVerification(EmailVerification.issue(EMAIL, CODE, sentAt));

            emailService.verifyCode(EMAIL, CODE);

            EmailVerification found = findVerification();
            assertThat(found.isVerified()).isTrue();
            assertThat(found.getExpiresAt()).isAfter(sentAt.plusMinutes(5));
        }

        @Test
        @DisplayName("코드를 요청한 적 없으면 EMAIL_VERIFICATION_CODE_NOT_FOUND 예외가 발생한다")
        void verifyCode_whenNotRequested_throwsCodeNotFound() {
            assertStatusCode(() -> emailService.verifyCode(EMAIL, CODE), AuthStatusCode.EMAIL_VERIFICATION_CODE_NOT_FOUND);
        }

        @Test
        @DisplayName("코드가 다르면 EMAIL_VERIFICATION_CODE_INVALID 예외가 발생하고 인증되지 않는다")
        void verifyCode_whenCodeDiffers_throwsCodeInvalid() {
            saveVerification(EmailVerification.issue(EMAIL, CODE, LocalDateTime.now()));

            assertStatusCode(() -> emailService.verifyCode(EMAIL, "000000"), AuthStatusCode.EMAIL_VERIFICATION_CODE_INVALID);
            assertThat(findVerification().isVerified()).isFalse();
        }

        @Test
        @DisplayName("코드가 만료되면 EMAIL_VERIFICATION_CODE_EXPIRED 예외가 발생한다")
        void verifyCode_whenExpired_throwsCodeExpired() {
            saveVerification(EmailVerification.issue(EMAIL, CODE, LocalDateTime.now().minusMinutes(6)));

            assertStatusCode(() -> emailService.verifyCode(EMAIL, CODE), AuthStatusCode.EMAIL_VERIFICATION_CODE_EXPIRED);
        }
    }

    @Nested
    @DisplayName("인증 완료 이메일 소비")
    class ConsumeVerifiedEmail {

        @Test
        @DisplayName("인증된 이메일이면 인증 정보를 삭제한다")
        void consumeVerifiedEmail_whenVerified_deletesVerification() {
            EmailVerification verification = EmailVerification.issue(EMAIL, CODE, LocalDateTime.now());
            verification.verify(LocalDateTime.now());
            saveVerification(verification);

            emailService.consumeVerifiedEmail(EMAIL);
            entityManager.flush();

            assertThat(emailVerificationRepository.existsById(EMAIL)).isFalse();
        }

        @Test
        @DisplayName("인증하지 않은 이메일이면 EMAIL_NOT_VERIFIED 예외가 발생한다")
        void consumeVerifiedEmail_whenNotVerified_throwsEmailNotVerified() {
            saveVerification(EmailVerification.issue(EMAIL, CODE, LocalDateTime.now()));

            assertStatusCode(() -> emailService.consumeVerifiedEmail(EMAIL), AuthStatusCode.EMAIL_NOT_VERIFIED);
        }

        @Test
        @DisplayName("인증 정보가 없으면 EMAIL_NOT_VERIFIED 예외가 발생한다")
        void consumeVerifiedEmail_whenNoVerification_throwsEmailNotVerified() {
            assertStatusCode(() -> emailService.consumeVerifiedEmail(EMAIL), AuthStatusCode.EMAIL_NOT_VERIFIED);
        }

        @Test
        @DisplayName("인증 후 10분이 지나면 EMAIL_NOT_VERIFIED 예외가 발생한다")
        void consumeVerifiedEmail_whenVerificationExpired_throwsEmailNotVerified() {
            LocalDateTime verifiedAt = LocalDateTime.now().minusMinutes(11);
            EmailVerification verification = EmailVerification.issue(EMAIL, CODE, verifiedAt);
            verification.verify(verifiedAt);
            saveVerification(verification);

            assertStatusCode(() -> emailService.consumeVerifiedEmail(EMAIL), AuthStatusCode.EMAIL_NOT_VERIFIED);
        }
    }
}
