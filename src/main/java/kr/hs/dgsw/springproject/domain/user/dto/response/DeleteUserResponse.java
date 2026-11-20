package kr.hs.dgsw.springproject.domain.user.dto.response;

public record DeleteUserResponse(
        Boolean deleted
) {
    public static DeleteUserResponse of(Boolean deleted) {
        return new DeleteUserResponse(deleted);
    }
}
