package com.octal.actorPay.dto;

import com.octal.actorPay.entities.RoleApiMapping;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
public class DefaultPermissions {

    private Map<String, List<RoleApiMapping>> primaryMerchantDefaultPermissions = new HashMap<>();

    private Map<String, List<RoleApiMapping>> subMerchantDefaultPermissions = new HashMap<>();

    private Map<String,List<RoleApiMapping>> adminDefaultPermissions  = new HashMap<>();

}
