package study.park.restapi;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import study.park.restapi.domain.Account;
import study.park.restapi.domain.AccountRole;
import study.park.restapi.service.AccountService;

import javax.annotation.PostConstruct;
import java.util.Set;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.accountAdd();
    }

    @Component
    static class InitService {

        @Autowired
        AccountService accountService;

        @Transactional
        public void accountAdd() {
            Account account = Account.builder()
                    .email("parkjun5@gmail.com")
                    .password("parkjun5")
                    .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                    .build();

            accountService.saveAccount(account);
        }

    }
}
