package com.octal.actorPay.mapper;

import com.octal.actorPay.dto.payments.WalletTransactionDTO;
import com.octal.actorPay.model.entities.WalletTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WalletTransactionResponseMapper extends BaseMapper<WalletTransaction, WalletTransactionDTO> {


    @Mapping(source = "wallet.id", target = "walletId")
    @Override
    WalletTransactionDTO sourceToDestination(WalletTransaction source);
}
