package kr.hs.dgsw.springproject.domain.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "email_verifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerification {
    private static final long CODE_VALID_MINUTES = 5;
    private static final long VERIFIED_VALID_MINUTES = 10;
    private static final long RESEND_COOLDOWN_MINUTES = 1;

    @Id
    @Column(length = 100)
    private String email;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false)
    private boolean verified;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private EmailVerification(String email, String code, LocalDateTime sentAt) {
        this.email = email;
        this.code = code;
        this.verified = false;
        this.sentAt = sentAt;
        this.expiresAt = sentAt.plusMinutes(CODE_VALID_MINUTES);
    }

    public static EmailVerification issue(String email, String code, LocalDateTime now) {
        return new EmailVerification(email, code, now);
    }

    public boolean isResendTooSoon(LocalDateTime now) {
        return sentAt.plusMinutes(RESEND_COOLDOWN_MINUTES).isAfter(now);
    }

    public boolean isExpired(LocalDateTime now) {
        return !expiresAt.isAfter(now);
    }

    public boolean matches(String inputCode) {
        return code.equals(inputCode);
    }

    public boolean isUsableForSignUp(LocalDateTime now) {
        return verified && !isExpired(now);
    }

    public void verify(LocalDateTime now) {
        this.verified = true;
        this.expiresAt = now.plusMinutes(VERIFIED_VALID_MINUTES);
    }
}
