package com.octal.actorPay.configuration.jwt;


import com.octal.actorPay.constants.ErrorMessage;
import com.octal.actorPay.constants.SecurityConstants;
import com.octal.actorPay.exceptionHandler.UnauthorisedException;
import com.octal.actorPay.services.CustomAdminDetailsService;
import com.octal.actorPay.services.CustomMerchantDetailsService;
import com.octal.actorPay.services.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Naveen
 */
@Component
public class JwtTokenUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomAdminDetailsService customAdminDetailsService;

    @Autowired
    private CustomMerchantDetailsService customMerchantDetailsService;


    private String secretKey;

    @Value("${jwt.secret}")
    private String secret;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    /**
     * This method is used to get Authentication object when user's credential is validated correctly
     *
     * @param token - jwt token
     * @return - returns authentication object when user is successfully authenticated with their username/password
     */
    protected Authentication getAuthentication(String token, String serviceName) {
        UserDetails userDetails = null;
        if (serviceName.equalsIgnoreCase("Admin")) {
            userDetails = this.customAdminDetailsService.loadUserByUsername(getUsername(token));
        } else if (serviceName.equalsIgnoreCase("Merchant")) {
            userDetails = this.customMerchantDetailsService.loadUserByUsername(getUsername(token));
        } else {
            userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));

        }
        // TODO check password
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


    /**
     * Get all claims of jwt token using jwt secret
     *
     * @param token - jwt token
     * @return - Claims object that contains all the jwt claims which was added during the JWT token creation
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    /**
     * Get username from jwt token
     *
     * @param token - jwt token
     * @return - current logged in username
     */
    private String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * This method is used to extract token from request header
     *
     * @param req - HttpServletRequest object
     * @return - if token starts with bearer then it will return extracted token from the string else it it will return null
     */
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(SecurityConstants.AUTHORIZATION_HEADER);

        return (!Objects.isNull(bearerToken) && bearerToken.startsWith(SecurityConstants.BEARER_PREFIX)) ?
                bearerToken.substring(7, bearerToken.length()) : null;
    }

    /**
     * Get expiration date from jwt token
     *
     * @param token - jwt token
     * @return - expiration date
     */
    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Get claims from jwt token
     *
     * @param token          - jwt token
     * @param claimsResolver - claim resolveer
     * @return - jwt claims
     */
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Check JWT token has expired or not?
     *
     * @param token - JWT token
     * @return - return true if jwt token has expired else false
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * This method is used to validate given jwt token
     *
     * @param token - jwt token
     * @return - return true if token is validated else it it will return false
     */
    public boolean validateToken(String token) {

        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);

            // return (!claims.getBody().getExpiration().before(new Date()));
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            LOGGER.error("Expired or invalid JWT token");
            throw new UnauthorisedException(ErrorMessage.TokenInvalid.MESSAGE, ErrorMessage.TokenInvalid.DEVELOPER_MESSAGE);
        }
    }
}