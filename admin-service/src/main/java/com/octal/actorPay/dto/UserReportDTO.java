package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserReportDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private String dateOfBirth;
    private String email;
    private String profilePicture;
    @Size( max = 10)
    private String contactNumber;
    private List<String> roles;

}