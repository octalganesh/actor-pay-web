package com.octal.actorPay.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.octal.actorPay.dto.DeviceDetailsDTO;
import com.octal.actorPay.entities.UserDeviceDetails;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class LoginRequest implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	@NotBlank(message="Email is required")
	private String email;
	@NotBlank(message="Password is required")
	private String password;

	@JsonProperty("deviceInfo")
	private DeviceDetailsDTO deviceDetailsDTO;



	public LoginRequest(String email, String password) {
		this.setEmail(email);
		this.setPassword(password);
	}

	public LoginRequest() {
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String username) {
		this.email = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public DeviceDetailsDTO getDeviceDetailsDTO() {
		return deviceDetailsDTO;
	}

	public void setDeviceDetailsDTO(DeviceDetailsDTO deviceDetailsDTO) {
		this.deviceDetailsDTO = deviceDetailsDTO;
	}
}