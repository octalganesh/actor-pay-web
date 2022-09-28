package com.octal.actorPay.dto;

import com.octal.actorPay.entities.RoleApiMapping;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
public class RoleDTO extends BaseDTO {

    private String id;

    private String userId;

    @NotBlank
    @Size(max = 255)
    private String name;
    @Size(max = 500)
    private String description;

    private ArrayList<String> permissions;

    private List<RoleApiMapping> roleApiMappings;

}