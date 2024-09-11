package br.com.agendapro.projeto.service.security;

import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.Token;

@Getter
@Setter
public class UserToken {
    private String token;

    public UserToken(String token){
        super();
        this.token = token;
    }
}
