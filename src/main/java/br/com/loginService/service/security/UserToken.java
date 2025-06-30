package br.com.loginService.service.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserToken {
    private String token;

    public UserToken(String token){
        super();
        this.token = token;
    }
}
