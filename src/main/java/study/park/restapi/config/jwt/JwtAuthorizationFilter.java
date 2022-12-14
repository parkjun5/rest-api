package study.park.restapi.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import study.park.restapi.config.principal.PrincipalDetails;
import study.park.restapi.domain.Account;
import study.park.restapi.repository.AccountRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final AccountRepository accountRepository;

    private final AuthenticationManager authenticationManager;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(JwtProperties.HEADER_STRING);
        if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("header = " + header);

        String token = request.getHeader(JwtProperties.HEADER_STRING).replace(JwtProperties.TOKEN_PREFIX, "");

        String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
                .getClaim("username").asString();

        if (StringUtils.hasText(username)) {
            Account account = accountRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException(username));

            PrincipalDetails principalDetails = new PrincipalDetails(account);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            principalDetails,
                            null,
                            principalDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
