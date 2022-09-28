package com.octal.actorPay.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Nishant Saraswat
 * JwtTokenProvider - provides functionality to create JWT access and refresh token
 */
@Component
@RefreshScope
public class JwtTokenProvider {

    private static final Logger LOGGER = LogManager.getLogger(JwtTokenProvider.class);


    @Value(value = "${jwt.access.token.validity}")
    public long jwtTokenValidity;

    @Value(value = "${jwt.refresh.token.validity}")
    public long jwtRefreshTokenValidity;

    private static final String JWT_TOKEN_TYPE = "Bearer ";

    @Value("${jwt.secret}")
    private String secret;

    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    private static final String JWT_SCOPE = "scopes";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String BEARER_PREFIX = "Bearer";

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }


    public String resolveToken(String token) {

        return (!Objects.isNull(token) && token.startsWith(BEARER_PREFIX)) ?
                token.substring(7, token.length()) : null;
    }

    public Claims getUsername(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

}
