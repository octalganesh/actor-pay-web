package com.octal.actorPay.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ROLE_API_MAPPING_OUTLET")
@Data
public class RoleApiMappingOutlet extends AbstractPersistable{

    private String outletId;
}
