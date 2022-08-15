package study.park.restapi.domain.request.dto;

import lombok.Data;

@Data
public class LoginRequestDto {

    private String email;
    private String password;

    public LoginRequestDto() {
    }

    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
