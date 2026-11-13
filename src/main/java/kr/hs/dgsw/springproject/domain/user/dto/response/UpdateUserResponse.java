package kr.hs.dgsw.springproject.domain.user.dto.response;

import kr.hs.dgsw.springproject.domain.user.domain.User;

public record UpdateUserResponse(
        String name,
        String email
) {
    public static UpdateUserResponse of(User user) {
        return new UpdateUserResponse(
                user.getName(),
                user.getEmail()
        );
    }
}
