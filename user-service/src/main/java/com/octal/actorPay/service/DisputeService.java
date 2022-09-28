package com.octal.actorPay.service;

import com.octal.actorPay.dto.DisputeItemDTO;
import com.octal.actorPay.dto.DisputeMessageDTO;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.request.DisputeFilterRequest;
import com.octal.actorPay.entities.DisputeItem;
import com.octal.actorPay.entities.DisputeMessage;

import java.util.List;

public interface DisputeService {

    DisputeItemDTO raiseDispute(DisputeItemDTO disputeItemDTO);

    PageItem<DisputeItemDTO> getAllDispute(PagedItemInfo pagedInfo, DisputeFilterRequest filterRequest);

    DisputeItemDTO viewDispute(String disputeId,String id,String userType);
    void updateDisputeStatus(String disputeId,String status,Double penality,Boolean disputeFlag) throws Exception;

    void sendDisputeMessage(DisputeMessageDTO disputeMessageDTO);
    List<DisputeItemDTO> getAllDisputeMessages();

    DisputeItem findDisputeById(String disputeId);

}
