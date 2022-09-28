package com.octal.actorPay.service.impl;

import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.request.LoyaltyRewardsRequest;
import com.octal.actorPay.dto.request.ReferralHistoryResponse;
import com.octal.actorPay.entities.ReferralHistory;
import com.octal.actorPay.repositories.ReferralHistoryRepository;
import com.octal.actorPay.service.ReferralUserService;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReferralUserServiceImpl implements ReferralUserService {

    @Autowired
    private ReferralHistoryRepository referralHistoryRepository;

    @Autowired
    private SpecificationFactory<ReferralHistory> specificationFactoryHistory;

    @Override
    public PageItem<ReferralHistoryResponse> getReferralHistoryByUserId(PagedItemInfo pagedInfo, String userId) {
        List<ReferralHistoryResponse> responsesList = new ArrayList<>();
        GenericSpecificationsBuilder<ReferralHistory> builder = new GenericSpecificationsBuilder<>();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(ReferralHistory.class, pagedInfo);
        builder.with(specificationFactoryHistory.isEqual("userId", userId));
        Page<ReferralHistory> pageResult = referralHistoryRepository.findAll(builder.build(), pageRequest);
        for (ReferralHistory referralHistory : pageResult) {
            ReferralHistoryResponse historyResponse = new ReferralHistoryResponse();
            historyResponse.setId(referralHistory.getId());
            historyResponse.setAmount(referralHistory.getAmount());
            historyResponse.setReferredUserId(referralHistory.getReferredUserId());
            historyResponse.setReferredUserName(referralHistory.getReferredUserName());
            historyResponse.setWalletTransactionId(referralHistory.getWalletTransactionId());
            historyResponse.setCreatedAt(referralHistory.getCreatedAt());
            historyResponse.setUpdatedAt(referralHistory.getUpdatedAt());
            responsesList.add(historyResponse);
        }
        return new PageItem<>(pageResult.getTotalPages(), pageResult.getTotalElements(), responsesList, pagedInfo.page,
                pagedInfo.items);
    }
}
