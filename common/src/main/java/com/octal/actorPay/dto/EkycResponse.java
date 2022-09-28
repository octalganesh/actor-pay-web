package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.octal.actorPay.constants.EkycStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EkycResponse implements Serializable {

    @JsonProperty("id_no")
    private String idNo;

    @JsonProperty("name")
    private String name;

    @JsonProperty("dob")
    private String dob;

    @JsonProperty("address")
    private String address;

    private String gender;

    @JsonProperty("id_type")
    private String idType;

    private String part;
    @JsonProperty("encoded_image")
    private String encodedImage;

    @JsonProperty("address_information")
    private AddressInformation addressInformation;

    @JsonProperty(value = "pan_verification_response",required = false)
    private PanVerificationResponse panVerificationResponse;

    private String createdAt;

    private String updatedAt;

    private EkycStatus ekycVerifyStatus;

    private String reasonDescription;

    private EkycStatus aadhaarVerifyStatus;

    private EkycStatus panVerifyStatus;

    private String userId;

    @Data
    public class AddressInformation {
        private String address;
        @JsonProperty("locality_or_post_office")
        private String localityOrPostOffice;
        @JsonProperty("district_or_city")
        private String districtOrCity;
        private String state;
        private String pincode;
    }

    @Data
    public class PanVerificationResponse {
        @JsonProperty("is_pan_dob_valid")
        boolean isPanDobValid;
    }
}

