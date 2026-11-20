package kr.hs.dgsw.springproject.domain.user.dto.response;

import kr.hs.dgsw.springproject.domain.user.domain.User;

public record UserFindByIdResponse(
        Long id,
        String name,
        String email
) {
    public static UserFindByIdResponse of(User user) {
        return new UserFindByIdResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
