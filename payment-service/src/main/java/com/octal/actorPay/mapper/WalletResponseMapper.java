package com.octal.actorPay.mapper;

import com.octal.actorPay.dto.payments.WalletDTO;
import com.octal.actorPay.model.entities.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WalletResponseMapper extends BaseMapper<Wallet, WalletDTO> {

    @Mapping(source  = "active", target="active",defaultValue = "true")
    WalletDTO sourceToDestination(Wallet source);

}
