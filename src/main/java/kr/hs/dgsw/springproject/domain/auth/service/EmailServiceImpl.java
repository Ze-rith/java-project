package kr.hs.dgsw.springproject.domain.auth.service;

import jakarta.mail.internet.MimeMessage;
import kr.hs.dgsw.springproject.domain.auth.domain.EmailVerification;
import kr.hs.dgsw.springproject.domain.auth.exception.AuthStatusCode;
import kr.hs.dgsw.springproject.domain.auth.repository.EmailVerificationRepository;
import kr.hs.dgsw.springproject.domain.user.exception.UserStatusCode;
import kr.hs.dgsw.springproject.domain.user.repository.UserRepository;
import kr.hs.dgsw.springproject.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final JavaMailSender javaMailSender;
    private final EmailVerificationRepository emailVerificationRepository;
    private final TemplateEngine templateEngine;
    private final UserRepository userRepository;

    @Value("${spring.mail.username:}")
    private String senderAddress;

    @Override
    public void sendCode(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ApplicationException(UserStatusCode.EMAIL_ALREADY_EXISTS);
        }

        LocalDateTime now = LocalDateTime.now();
        emailVerificationRepository.findById(email)
                .filter(verification -> verification.isResendTooSoon(now))
                .ifPresent(verification -> {
                    throw new ApplicationException(AuthStatusCode.EMAIL_VERIFICATION_RESEND_TOO_SOON);
                });

        String code = String.format(Locale.ROOT, "%06d", SECURE_RANDOM.nextInt(1_000_000));
        Context context = new Context(Locale.KOREAN);
        context.setVariable("authNum", code);
        String content = templateEngine.process("EmailAuth", context);

        sendEmail(email, "이메일 인증 코드", content);
        emailVerificationRepository.save(EmailVerification.issue(email, code, now));
    }

    @Override
    public void verifyCode(String email, String authNum) {
        EmailVerification verification = emailVerificationRepository.findById(email)
                .orElseThrow(() -> new ApplicationException(AuthStatusCode.EMAIL_VERIFICATION_CODE_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        if (verification.isExpired(now)) {
            throw new ApplicationException(AuthStatusCode.EMAIL_VERIFICATION_CODE_EXPIRED);
        }
        if (!verification.matches(authNum)) {
            throw new ApplicationException(AuthStatusCode.EMAIL_VERIFICATION_CODE_INVALID);
        }

        verification.verify(now);
        emailVerificationRepository.save(verification);
    }

    @Override
    public void consumeVerifiedEmail(String email) {
        LocalDateTime now = LocalDateTime.now();
        EmailVerification verification = emailVerificationRepository.findById(email)
                .filter(found -> found.isUsableForSignUp(now))
                .orElseThrow(() -> new ApplicationException(AuthStatusCode.EMAIL_NOT_VERIFIED));

        emailVerificationRepository.delete(verification);
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            if (senderAddress != null && !senderAddress.isBlank()) {
                helper.setFrom(senderAddress);
            }
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            javaMailSender.send(message);
        } catch (Exception exception) {
            throw new IllegalStateException("이메일 발송에 실패했습니다.", exception);
        }
    }
}
