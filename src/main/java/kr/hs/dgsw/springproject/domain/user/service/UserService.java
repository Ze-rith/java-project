package kr.hs.dgsw.springproject.domain.user.service;

import kr.hs.dgsw.springproject.domain.user.domain.User;
import kr.hs.dgsw.springproject.domain.user.dto.request.CreateUserRequest;

public interface UserService {
    User findById(Long id);
    User create(CreateUserRequest request);
    User update(Long id, User user);
    Boolean delete(Long id);
}
