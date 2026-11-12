package kr.hs.dgsw.springproject.domain.user.service;

import kr.hs.dgsw.springproject.domain.user.domain.User;

public interface UserService {
    User findById(Long id);
    User create(User user);
    User update(Long id, User user);
    Boolean delete(Long id);
}
