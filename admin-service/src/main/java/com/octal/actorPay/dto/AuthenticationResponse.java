package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class AuthenticationResponse implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;
	private String jwtToken;
	private String refreshToken;
	private String tokenType;
//	private String roles;
//	private Date expire;
//	private String id;
//	private String userName;
//	private String email;

	@JsonProperty("access_token")
	public String getJwtToken() {
		return jwtToken;
	}

	@JsonProperty("refresh_token")
	public String getRefreshToken() {
		return refreshToken;
	}

	@JsonProperty(value = "token_type")
	public String getTokenType() {
		return tokenType;
	}

	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

}