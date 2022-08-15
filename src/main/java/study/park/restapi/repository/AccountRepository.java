package study.park.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.park.restapi.domain.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository <Account, Integer> {
    Optional<Account> findByEmail(String username);
}
