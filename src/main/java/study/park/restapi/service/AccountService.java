package study.park.restapi.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import study.park.restapi.domain.Account;

import java.util.List;

public interface AccountService extends UserDetailsService {

    Account saveAccount(Account account);

    List<Account> findAll();
}
