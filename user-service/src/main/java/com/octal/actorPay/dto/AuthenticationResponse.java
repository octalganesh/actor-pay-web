package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class AuthenticationResponse implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;
	private String jwtToken;
	private String refreshToken;
	private String tokenType;
//	private String roles;
//	private Date expire;
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String panNumber;
	private String aadharNumber;
	private String eKycStatus;
	private LocalDate dateOfBirth;
	private String panVerified;
	private String aadharVerified;
	private String docType;
	private Boolean notification;
	private String referralCode;

	private String profilePicture;

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

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

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String geteKycStatus() {
		return eKycStatus;
	}

	public void seteKycStatus(String eKycStatus) {
		this.eKycStatus = eKycStatus;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
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
	public String getId() {
		return id;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Boolean getNotification() {
		return notification;
	}

	public void setNotification(Boolean notification) {
		this.notification = notification;
	}

	public String getReferralCode() {
		return referralCode;
	}

	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}
}