package study.park.restapi.config.jwt;

public interface JwtProperties {
    String SECRET = "secret";
    int EXPIRATION_TIME = 864000000;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}
