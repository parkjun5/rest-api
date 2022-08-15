package study.park.restapi.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import study.park.restapi.domain.Account;
import study.park.restapi.domain.AccountRole;
import study.park.restapi.repository.AccountRepository;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Test
    @DisplayName("회원 이름으로 찾기 비밀번호 X")
    void findByUsername() throws Exception {
        //given
        String username = "parkjun5@gmail.com";
        String password = "123";

        creteAccount(username, password);

        //when
        UserDetailsService userDetailsService = (UserDetailsService) accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        //then
        assertThat(userDetails.getPassword()).isEqualTo(password);
    }



    @Test
    @DisplayName("회원을 찾으려 했으나 없었다.")
    void findUsernameButNoOne() throws Exception {
        //given
        String username = "parkjun2@gmail.com";
        String password = "123";

        creteAccount(username, password);

        //when
        UserDetailsService userDetailsService = (UserDetailsService) accountService;

        //then
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("하이"));
    }

    private void creteAccount(String username, String password) {
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        accountRepository.save(account);
    }

}