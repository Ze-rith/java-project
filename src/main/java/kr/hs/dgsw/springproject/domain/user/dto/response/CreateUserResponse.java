package kr.hs.dgsw.springproject.domain.user.dto.response;

import kr.hs.dgsw.springproject.domain.user.domain.User;

public record CreateUserResponse(
        Long id,
        String name,
        String email
) {
    public static CreateUserResponse of(User user) {
        return new CreateUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
