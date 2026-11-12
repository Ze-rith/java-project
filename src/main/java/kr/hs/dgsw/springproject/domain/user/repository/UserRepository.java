package kr.hs.dgsw.springproject.domain.user.repository;

import kr.hs.dgsw.springproject.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
