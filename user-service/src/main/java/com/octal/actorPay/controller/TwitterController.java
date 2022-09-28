package com.octal.actorPay.controller;

import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;


@RestController
public class TwitterController {


    @GetMapping("/oauth2/callback/twitter")
    public void getTwitter() {

        TwitterConnectionFactory connectionFactory =
                new TwitterConnectionFactory("JjdNrNWUFoox7PbcHvJ9ZU0Xo","u1Z9qQWRePzPSNLug56E6rs02KNDIKUyN5pxdujDvjAG4JZ8B3");
        OAuth1Operations oauthOperations = connectionFactory.getOAuthOperations();
        OAuthToken requestToken = oauthOperations.fetchRequestToken("https://api.twitter.com/oauth/request_token", null);

        OAuthToken accessToken = oauthOperations.exchangeForAccessToken(
                new AuthorizedRequestToken(requestToken, ""), null);
        System.out.println("Token Value:- accesstoken");
        accessToken.getSecret();
        accessToken.getValue();
        Twitter twitter = new TwitterTemplate("JjdNrNWUFoox7PbcHvJ9ZU0Xo",
                "u1Z9qQWRePzPSNLug56E6rs02KNDIKUyN5pxdujDvjAG4JZ8B3",
                accessToken.getValue(),
                accessToken.getSecret());
        TwitterProfile profile = twitter.userOperations().getUserProfile();
        System.out.println(profile.toString());

    }


    @GetMapping("/oauth2/authorize/normal/twitter")
    public void twitterOauthLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {

        TwitterConnectionFactory connectionFactory =
                new TwitterConnectionFactory("JjdNrNWUFoox7PbcHvJ9ZU0Xo","u1Z9qQWRePzPSNLug56E6rs02KNDIKUyN5pxdujDvjAG4JZ8B3");
        OAuth1Operations oauthOperations = connectionFactory.getOAuthOperations();
        oauthOperations.toString();
        OAuthToken requestToken = oauthOperations.fetchRequestToken("https://api.twitter.com/oauth/request_token", null);
        String authorizeUrl = oauthOperations.buildAuthorizeUrl(requestToken.getValue(), OAuth1Parameters.NONE);
        response.sendRedirect(authorizeUrl);
    }

}