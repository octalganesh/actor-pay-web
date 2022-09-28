package com.octal.actorPay.jwt;

import com.octal.actorPay.dto.AuthenticationResponse;
import com.octal.actorPay.entities.Role;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserDocument;
import com.octal.actorPay.entities.UserSetting;
import com.octal.actorPay.exceptions.AccessDeniedException;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.repositories.SettingRepository;
import com.octal.actorPay.repositories.UserDocumentRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;

/**
 * @author naveen.kumawat
 * JwtTokenProvider - provides functionality to create JWT access and refresh token
 */
@Component
@RefreshScope
@ConfigurationProperties(prefix="jwt")
public class JwtTokenProvider {

    private static final Logger LOGGER = LogManager.getLogger(JwtTokenProvider.class);

    //    @Autowired
    //    private MessageHelper messageHelper;

    @Value(value = "${jwt.access.token.validity}")
    public long jwtTokenValidity;

    @Value(value = "${jwt.refresh.token.validity}")
    public long jwtRefreshTokenValidity;

    private static final String JWT_TOKEN_TYPE = "Bearer ";

    @Value("${jwt.secret}")
    private String secret;

    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    private static final String JWT_SCOPE = "scopes";

    @Autowired
    private UserDocumentRepository userDocumentRepository;

    @Autowired
    private SettingRepository settingRepository;

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    /**
     * this method is used to create JWT token,
     * while generating token we pass scope and other user related information
     * @param user - registered user object
     * @return - jwt token
     */
    public String createAccessJwtToken(User user) {

        if (user.getEmail().isEmpty())
            throw new IllegalArgumentException("cannot.create.token.without.username");
        if (user.getRole() == null)
            throw new IllegalArgumentException("user.has.no.privileges");

        Claims claims = Jwts.claims().setSubject(user.getEmail());
        Role role = user.getRole();
        claims.put(JWT_SCOPE,
                role);

//        claims.put("id",user.getId());
//        claims.put("emailId",user.getEmail());
//        claims.put("firstName",user.getFirstName());
//        claims.put("lastName",user.getLastName());
//        claims.put("role",roles.get(0));
//        claims.put("profileImage","http://13.235.72.118:7300/admins/avatar-1625799760851.png");
//        claims.put("permission","");
        return Jwts.builder().setClaims(claims).setIssuer(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenValidity * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    /**
     * generate new access token and refresh token when user login
     *
     * @param userDetails - authenticated user object
     * @return - jwt tokens and scopes
     */
    public AuthenticationResponse generateToken(User userDetails) {
        String access = createAccessJwtToken(userDetails);
        String refresh = createRefreshToken(userDetails, 1000);
//        List<String> roles = userDetails.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toList());

        AuthenticationResponse response = new AuthenticationResponse();
//            response.setId(userDetails.getId());
            response.setJwtToken(access);
            response.setEmail(userDetails.getEmail());
            response.setFirstName(userDetails.getFirstName());
            response.setLastName(userDetails.getLastName());
            response.setId(userDetails.getId());
            response.setTokenType(JWT_TOKEN_TYPE);
            response.setRefreshToken(refresh);
            response.setPanNumber(userDetails.getPanNumber());
            response.setAadharNumber(userDetails.getAadharNumber());
            response.seteKycStatus(String.valueOf(userDetails.getEkycStatus()));
            response.setDateOfBirth(userDetails.getDateOfBirth());

//            if(userDetails.getEkycStatus().name().equals("PENDING"))  {
//                Optional<UserDocument> byUser = userDocumentRepository.findByUserId(userDetails.getId());
//                if (!byUser.isPresent())  {
//                    response.setPanVerified("PENDING");
//                    response.setAadharVerified("PENDING");
//                } else {
//                    if (byUser.get().getDocType().equals("PAN")){
//                        response.setPanVerified(String.valueOf(byUser.get().getEkycStatus()));
//                    }
//
//                    if (byUser.get().getDocType().equals("AADHAAR")) {
//                        response.setAadharVerified(String.valueOf(byUser.get().getEkycStatus()));
//                    }
//                }
//            }

            //            response.setExpire(calculateExpirationDate());

        //Get User Setting
            Optional<UserSetting> userSetting = settingRepository.findByUser(userDetails);
            if(userSetting.isPresent()){
                response.setNotification(userSetting.get().getNotification());
            }else{
                response.setNotification(false);
            }
            if(userDetails.getProfilePicture()!=null){
                response.setProfilePicture(userDetails.getProfilePicture());
            }
            response.setReferralCode(userDetails.getReferralCode());
            return response;

    }


    /**
     * generate refresh token
     *
     * @param user - user details object that contains user information
     * @param miliSecond
     * @return - returns access token
     */
    public String createRefreshToken(User user, int miliSecond) {

        if (user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Cannot create token without username");
        }
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        // set refresh token in scope claim
        Role role = user.getRole();

        claims.put(JWT_SCOPE, Collections.singletonList(REFRESH_TOKEN));
        claims.put("id",user.getId());
        claims.put("emailId",user.getEmail());
        claims.put("firstName",user.getFirstName());
        claims.put("lastName",user.getLastName());
        claims.put("role",role);
        claims.put("profileImage","http://13.235.72.118:7300/admins/avatar-1625799760851.png");
        claims.put("permission","");
        return Jwts.builder().setClaims(claims).setIssuer(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshTokenValidity * miliSecond))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }


    /**
     * this method is used to calculated JWt expiration time
     * @return - expiration time
     */
    private Date calculateExpirationDate() {
        Date now = new Date();
        return new Date(now.getTime() + jwtTokenValidity);
    }

    /**
     * validate refresh token that refresh has expired or not and check that jwt
     * token contains refresh scope or not
     *
     * @param token - JWT token
     * @return - true if refresh token is valid
     */
    public boolean validateRefreshToken(String token) {
        LOGGER.info("validating refresh token");

        try {
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            List<String> scopes = claims.get(JWT_SCOPE, List.class);
            boolean isTokenBeRefreshed = canTokenBeRefreshed(token);
            if (!isTokenBeRefreshed) {
                throw new ActorPayException("refresh.token.is.expired");
            }
            if (scopes == null || scopes.isEmpty() || scopes.stream().noneMatch(REFRESH_TOKEN::equals)) {
                throw new AccessDeniedException("invalid.refresh.token");
            }

        } catch (ExpiredJwtException exception) {
            throw new ActorPayException("refresh.token.is.expired");
        }

        return true;
    }

    /**
     * check token can be refreshed or not
     *
     * @param token - JWT token
     * @return - return true if token can be refresh else false
     */
    public Boolean canTokenBeRefreshed(String token) {
        return (!isTokenExpired(token));
    }

    /**
     * check JWT token has expired or not
     *
     * @param token - JWT token
     * @return - return true if jwt token has expired else false
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * get expiration date from jwt token
     *
     * @param token - jwt token
     * @return - expiration date
     */
    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * get claims from jwt token
     *
     * @param token          - jwt token
     * @param claimsResolver - claim resolveer
     * @return - jwt claims
     */
    private  <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * get all claims of jwt token using jwt secret
     *
     * @param token - jwt token
     * @return - jwt claims
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
    /**
     * get user name from jwt token
     *
     * @param token - jwt token
     * @return - username
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * generate access token from refresh token
     *
     * @param user =
     * @return
     */
    public AuthenticationResponse generateTokenFromRefreshToken(User user, String refreshToken) {
        LOGGER.debug("get new access token from refresh token");

        /*List<String> roles = userDetails.getAuthorities().stream().map(Object::toString).collect(Collectors.toList());
        Optional<User> user = ruleValidator.checkPresence(userRepository.findByEmail(userDetails.getUsername()),
                messageHelper.getMessage("user.not.found"));*/

        if (user != null) {
//            List<String> roles = user.getRoles().stream().map(Object::toString).collect(Collectors.toList());
            String access = createAccessJwtToken(user);

            AuthenticationResponse response = new AuthenticationResponse();
            response.setJwtToken(access);
            response.setRefreshToken(createRefreshToken(user, 2000));
            response.setEmail(user.getEmail());
            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());
            response.setId(user.getId());
            response.setTokenType(JWT_TOKEN_TYPE);
//            response.setExpire(getExpirationDateFromToken(access));
            return response;
        }
        return null;
    }
}