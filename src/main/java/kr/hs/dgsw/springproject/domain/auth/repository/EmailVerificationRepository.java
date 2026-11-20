package kr.hs.dgsw.springproject.domain.auth.repository;

import kr.hs.dgsw.springproject.domain.auth.domain.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {
}
