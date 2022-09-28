package com.octal.actorPay.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class OutletDto extends AddressDTO {

    private String id;

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    @Size(max = 255)
    private String licenceNumber;

//    @NotBlank
    @Size(max = 255)
    private String resourceType;

    private String merchantId;


    @NotBlank
    @Size(max = 255)
    private String contactNumber;

    @NotBlank
    @Size(max = 3)
    private String extensionNumber;

    @Size(max = 255)
    @Size(max = 255)
    private String description;


}
