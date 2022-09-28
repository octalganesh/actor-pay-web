package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDate;

public class AuthenticationResponse implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;
	private String jwtToken;
	private String refreshToken;
	private String tokenType;
	private String role;
//	private Date expire;
	private String id;
//	private String userName;
	private String email;
	private String businessName;
	private String permission;
	private String profileImage;
	private String aadharNumber;
	private String panNumber;
	private String ekycStatus;
	private LocalDate dateOfBirth;
	private String panVerified;
	private String aadharVerified;
	private String docType;

	private Boolean notification;

	public String getPanVerified() {
		return panVerified;
	}

	public void setPanVerified(String panVerified) {
		this.panVerified = panVerified;
	}

	public String getAadharVerified() {
		return aadharVerified;
	}

	public void setAadharVerified(String aadharVerified) {
		this.aadharVerified = aadharVerified;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public Boolean getNotification() {
		return notification;
	}

	public void setNotification(Boolean notification) {
		this.notification = notification;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getEkycStatus() {
		return ekycStatus;
	}

	public void setEkycStatus(String ekycStatus) {
		this.ekycStatus = ekycStatus;
	}

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

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

//	public String getRoles() {
//		return roles;
//	}
//
//	public Date getExpire() {
//		return expire;
//	}
//
//	public String getId() {
//		return id;
//	}

	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

//	public void setRoles(String roles) {
//		this.roles = roles;
//	}
//
//	public void setExpire(Date expire) {
//		this.expire = expire;
//	}

	public void setId(String id) {
		this.id = id;
	}

//	public String getUserName() {
//		return userName;
//	}
//
//	public void setUserName(String userName) {
//		this.userName = userName;
//	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
}