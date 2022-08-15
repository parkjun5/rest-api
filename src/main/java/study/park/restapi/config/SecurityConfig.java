package study.park.restapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import study.park.restapi.config.jwt.JwtAuthenticationFilter;
import study.park.restapi.config.jwt.JwtAuthorizationFilter;
import study.park.restapi.repository.AccountRepository;
import study.park.restapi.service.AccountService;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {


    private final CorsConfig corsConfig;

    private final AccountRepository accountRepository;

    private final AccountService accountService;

    private final PasswordEncoder passwordEncoder;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeHttpRequests( (auth) ->
                auth.anyRequest().authenticated())
                        .httpBasic().disable()
                        .formLogin().disable()
                        .addFilter(corsConfig.corsFilter())
                        .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                        .addFilter(new JwtAuthorizationFilter(
                                authenticationManager(), accountRepository)
                        )
                        .csrf().disable()
                        .cors()
                        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//                        .and().authorizeHttpRequests().antMatchers("/api/**");
//                        .authorizeHttpRequests().antMatchers("/api/**");
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(accountService);
        return new ProviderManager(provider);
    }

}
