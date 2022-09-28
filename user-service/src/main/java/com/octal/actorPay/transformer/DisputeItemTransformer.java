package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.DisputeItemDTO;
import com.octal.actorPay.dto.DisputeMessageDTO;
import com.octal.actorPay.entities.DisputeItem;
import com.octal.actorPay.entities.DisputeMessage;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;

public class DisputeItemTransformer {


    public static Function<DisputeItem, DisputeItemDTO> DISPUTE_ENTITY_TO_DTO = (disputeItem) -> {

        DisputeItemDTO disputeItemDTO = new DisputeItemDTO();
        disputeItemDTO.setDisputeId(disputeItem.getId());
        disputeItemDTO.setDescription(disputeItem.getDescription());
        disputeItemDTO.setImagePath(disputeItem.getImagePath());
        disputeItemDTO.setMerchantId(disputeItem.getMerchantId());
        disputeItemDTO.setUserId(disputeItem.getUserId());
        disputeItemDTO.setStatus(disputeItem.getStatus());
        disputeItemDTO.setOrderItemId(disputeItem.getOrderItem().getId());
        disputeItemDTO.setDisputeCode(disputeItem.getDisputeCode());
        disputeItemDTO.setTitle(disputeItem.getTitle());
        disputeItemDTO.setPenalityPercentage(disputeItem.getPenalityPercentage());
        disputeItemDTO.setActive(Boolean.TRUE);
        disputeItemDTO.setCreatedAt(disputeItem.getCreatedAt());
        disputeItemDTO.setUpdatedAt(disputeItem.getUpdatedAt());
        disputeItemDTO.setDisputeFlag(disputeItem.isDisputeFlag());
        disputeItemDTO.setOrderNo(disputeItem.getOrderNo());
        return disputeItemDTO;

    };

    public static Function<DisputeItemDTO, DisputeItem> DISPUTE_DTO_TO_ENTITY = (disputeItemDTO) -> {
        DisputeItem disputeItem = new DisputeItem();
        disputeItem.setId(disputeItemDTO.getDisputeId());
        disputeItem.setDescription(disputeItemDTO.getDescription());
        disputeItem.setImagePath(disputeItemDTO.getImagePath());
        disputeItem.setMerchantId(disputeItemDTO.getMerchantId());
        disputeItem.setTitle(disputeItemDTO.getTitle());
        disputeItem.setStatus(disputeItemDTO.getStatus());
        disputeItem.setUserId(disputeItemDTO.getUserId());
        disputeItem.setDisputeCode(disputeItemDTO.getDisputeCode());
        disputeItem.setPenalityPercentage(disputeItemDTO.getPenalityPercentage());
        disputeItem.setActive(Boolean.TRUE);
        disputeItem.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        return disputeItem;

    };

    public static Function<DisputeMessage, DisputeMessageDTO> DISPUTE_MSG_ENTITY_TO_DTO = (disputeMessage) -> {

        DisputeMessageDTO disputeMessageDTO = new DisputeMessageDTO();
        disputeMessageDTO.setMessage(disputeMessage.getMessage());
        disputeMessageDTO.setPostedById(disputeMessage.getPostedById());
        disputeMessageDTO.setPostedByName(disputeMessage.getPostedByName());
        disputeMessageDTO.setCreatedAt(disputeMessage.getCreatedAt());
        disputeMessageDTO.setUpdatedAt(disputeMessage.getUpdatedAt());
        disputeMessageDTO.setUserType(disputeMessage.getUserType());
        disputeMessageDTO.setActive(disputeMessage.getActive());
        disputeMessageDTO.setDisputeId(disputeMessage.getDisputeItem().getId());
        return disputeMessageDTO;
    };
    public static Function<DisputeMessageDTO,DisputeMessage> DISPUTE_MSG_DTO_TO_ENTITY = (disputeMessageDTO) -> {

        DisputeMessage disputeMessage = new DisputeMessage();
        disputeMessage.setMessage(disputeMessageDTO.getMessage());
        disputeMessage.setPostedById(disputeMessageDTO.getPostedById());
        disputeMessage.setPostedByName(disputeMessageDTO.getPostedByName());
        disputeMessage.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        disputeMessage.setUpdatedAt(disputeMessageDTO.getUpdatedAt());
        disputeMessage.setUserType(disputeMessageDTO.getUserType());
        disputeMessage.setActive(Boolean.TRUE);

        return disputeMessage;
    };

}
