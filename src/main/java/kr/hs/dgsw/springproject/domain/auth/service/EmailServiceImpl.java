package kr.hs.dgsw.springproject.domain.auth.service;

import jakarta.mail.internet.MimeMessage;
import kr.hs.dgsw.springproject.domain.auth.exception.AuthStatusCode;
import kr.hs.dgsw.springproject.domain.user.exception.UserStatusCode;
import kr.hs.dgsw.springproject.domain.user.repository.UserRepository;
import kr.hs.dgsw.springproject.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private static final String CODE_KEY_PREFIX = "email:verification:";
    private static final String RESEND_KEY_PREFIX = "email:verification:resend:";
    private static final String VERIFIED = "ACCESS";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final JavaMailSender javaMailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private final TemplateEngine templateEngine;
    private final UserRepository userRepository;

    @Value("${spring.mail.username:}")
    private String senderAddress;

    @Override
    public void sendCode(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ApplicationException(UserStatusCode.EMAIL_ALREADY_EXISTS);
        }

        String codeKey = codeKey(email);
        String resendKey = RESEND_KEY_PREFIX + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(resendKey))) {
            throw new ApplicationException(AuthStatusCode.EMAIL_VERIFICATION_RESEND_TOO_SOON);
        }

        String code = String.format(Locale.ROOT, "%06d", SECURE_RANDOM.nextInt(1_000_000));
        Context context = new Context(Locale.KOREAN);
        context.setVariable("authNum", code);
        String content = templateEngine.process("EmailAuth", context);

        sendEmail(email, "이메일 인증 코드", content);
        redisTemplate.opsForValue().set(codeKey, code, 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(resendKey, "sent", 1, TimeUnit.MINUTES);
    }

    @Override
    public void verifyCode(String email, String authNum) {
        String key = codeKey(email);
        String savedCode = redisTemplate.opsForValue().get(key);

        if (savedCode == null) {
            throw new ApplicationException(AuthStatusCode.EMAIL_VERIFICATION_CODE_NOT_FOUND);
        }
        if (!savedCode.equals(authNum)) {
            throw new ApplicationException(AuthStatusCode.EMAIL_VERIFICATION_CODE_INVALID);
        }

        redisTemplate.opsForValue().set(key, VERIFIED, 10, TimeUnit.MINUTES);
    }

    @Override
    public void consumeVerifiedEmail(String email) {
        String key = codeKey(email);
        String value = redisTemplate.opsForValue().get(key);
        if (!VERIFIED.equals(value)) {
            throw new ApplicationException(AuthStatusCode.EMAIL_NOT_VERIFIED);
        }
        redisTemplate.delete(key);
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

    private String codeKey(String email) {
        return CODE_KEY_PREFIX + email;
    }
}
