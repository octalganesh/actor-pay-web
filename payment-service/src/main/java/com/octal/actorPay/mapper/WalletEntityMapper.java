package com.octal.actorPay.mapper;

import com.octal.actorPay.dto.payments.WalletDTO;
import com.octal.actorPay.model.entities.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WalletEntityMapper extends BaseMapper<WalletDTO, Wallet> {

    @Mapping(source  = "active", target="active",defaultValue = "true")
    Wallet sourceToDestination(WalletDTO source);

}
