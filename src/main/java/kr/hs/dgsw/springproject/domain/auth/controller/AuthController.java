package kr.hs.dgsw.springproject.domain.auth.controller;

import jakarta.validation.Valid;
import kr.hs.dgsw.springproject.domain.auth.dto.request.SendEmailVerificationRequest;
import kr.hs.dgsw.springproject.domain.auth.dto.request.VerifyEmailRequest;
import kr.hs.dgsw.springproject.domain.auth.service.EmailService;
import kr.hs.dgsw.springproject.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final EmailService emailService;

    @PostMapping("/email/code")
    public ApiResponse<Void> sendEmailVerificationCode(
            @RequestBody @Valid SendEmailVerificationRequest request
    ) {
        emailService.sendCode(request.email());
        return ApiResponse.ok("이메일 인증 코드를 발송했습니다.");
    }

    @PostMapping("/email/verify")
    public ApiResponse<Void> verifyEmail(
            @RequestBody @Valid VerifyEmailRequest request
    ) {
        emailService.verifyCode(request.email(), request.code());
        return ApiResponse.ok("이메일 인증이 완료되었습니다.");
    }
}
