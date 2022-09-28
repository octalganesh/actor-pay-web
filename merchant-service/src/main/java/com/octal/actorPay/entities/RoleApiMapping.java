package com.octal.actorPay.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ROLE_API_MAPPING")
@Data
public class RoleApiMapping extends AbstractPersistable {

    private String roleId;

    private String permissionId;

    @Transient
    private String permissionName;

}
