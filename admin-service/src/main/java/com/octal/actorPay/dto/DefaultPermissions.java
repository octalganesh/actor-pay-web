package com.octal.actorPay.dto;

import com.octal.actorPay.entities.RoleAPIMapping;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
public class DefaultPermissions {
    private Map<String, List<RoleAPIMapping>> adminDefaultPermissions = new HashMap<>();

    private Map<String, List<RoleAPIMapping>> subAdminDefaultPermissions = new HashMap<>();
}
