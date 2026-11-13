package kr.hs.dgsw.springproject.domain.user.service;

import kr.hs.dgsw.springproject.domain.user.domain.User;
import kr.hs.dgsw.springproject.domain.user.dto.request.CreateUserRequest;
import kr.hs.dgsw.springproject.domain.user.exception.UserStatusCode;
import kr.hs.dgsw.springproject.domain.user.repository.UserRepository;
import kr.hs.dgsw.springproject.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(UserStatusCode.USER_NOT_FOUND));
    }

    @Override
    public User create(CreateUserRequest request) {
        validateUser(request.email());

        return userRepository.save(
                User.builder()
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .build()
        );
    }

    @Override
    public User update(Long id, User user) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    private void validateUser(String email) {
        if (userRepository.existsByEmail(email))
            throw new ApplicationException(UserStatusCode.EMAIL_ALREADY_EXISTS);
    }
}
