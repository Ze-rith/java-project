package kr.hs.dgsw.springproject.domain.auth.service;

public interface EmailService {
    void sendCode(String email);

    void verifyCode(String email, String authNum);

    void consumeVerifiedEmail(String email);
}
