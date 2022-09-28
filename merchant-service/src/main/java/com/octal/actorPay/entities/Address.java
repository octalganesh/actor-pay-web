package com.octal.actorPay.entities;

import com.octal.actorPay.utils.CommonUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "address")
public class Address extends CommonAddressFields {

}