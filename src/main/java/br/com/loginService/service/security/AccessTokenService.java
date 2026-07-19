package br.com.loginService.service.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;

import br.com.loginService.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class AccessTokenService {
    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";
    private static final long EXPIRATION = 15 * 60 * 1000;
    private static final String SECRET_KEY = System.getenv("JWT_SECRET");
    private static final String EMISSOR = "https://danielsouzz.com.br";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public static String createAcessToken(User user){
        return generateToken(user.getEmail());
    }

    private static boolean isEmissorValid(String emissor){
        return emissor.equals(EMISSOR);
    }

    private static boolean isSubjectValid(String username){
        return username != null && username.length() > 0;
    }

    public static UsernamePasswordAuthenticationToken validate(HttpServletRequest request){

        String header = request.getHeader(HEADER);
        if (header == null || !header.startsWith(PREFIX)) return null;

        String token = header.substring(PREFIX.length());

        try {
            Jws<Claims> jwsClaims = Jwts.parserBuilder().setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token);

            String email = jwsClaims.getBody().getSubject();
            String issuer = jwsClaims.getBody().getIssuer();

            if (isSubjectValid(email) && isEmissorValid(issuer)) {
                return new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
            }
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }

        return null;
    }

    private static String generateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuer(EMISSOR)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + AccessTokenService.EXPIRATION))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }
}