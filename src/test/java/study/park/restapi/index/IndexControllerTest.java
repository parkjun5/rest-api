package study.park.restapi.index;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import study.park.restapi.BaseControllerTest;
import study.park.restapi.config.jwt.JwtProperties;
import study.park.restapi.domain.Account;
import study.park.restapi.domain.AccountRole;
import study.park.restapi.domain.request.dto.LoginRequestDto;
import study.park.restapi.service.AccountService;

import java.util.Date;
import java.util.Set;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class IndexControllerTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Test
    void index() throws Exception {
        this.mockMvc.perform(get("/api/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.events").exists())
                .andDo(document(
                        "index-access"
                ));
    }

    @Test
    void testLogin() throws Exception {
        String username = "parkjun2@gmail.com";
        String password = "123";
        String TOKEN = "";

        Account account = creteAccount(username, password);
        LoginRequestDto loginRequestDto = new LoginRequestDto(username, password);
        TOKEN = JwtProperties.TOKEN_PREFIX + JWT.create()
                .withSubject(account.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("username", account.getEmail())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        this.mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", username)
                        .param("password", password)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"));
    }

    private Account creteAccount(String username, String password) {
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        return accountService.saveAccount(account);
    }

}
