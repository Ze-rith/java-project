package kr.hs.dgsw.springproject.auth.service;

import kr.hs.dgsw.springproject.auth.domain.User;

public interface UserService {
    User findById(Long id);
    User create(User user);
    User update(Long id, User user);
    Boolean delete(Long id);
}
