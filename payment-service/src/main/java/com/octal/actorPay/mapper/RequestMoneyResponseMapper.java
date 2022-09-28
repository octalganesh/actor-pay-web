package com.octal.actorPay.mapper;

import com.octal.actorPay.dto.payments.RequestMoneyDTO;
import com.octal.actorPay.dto.payments.WalletDTO;
import com.octal.actorPay.model.entities.RequestMoney;
import com.octal.actorPay.model.entities.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RequestMoneyResponseMapper extends BaseMapper<RequestMoney, RequestMoneyDTO> {

    @Mapping(source  = "active", target="active",defaultValue = "true")
    @Mapping(source = "id",target = "requestId")
    RequestMoneyDTO sourceToDestination(RequestMoney source);

}
