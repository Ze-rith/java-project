package kr.hs.dgsw.springproject.auth.repository;

import kr.hs.dgsw.springproject.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
