package com.octal.actorPay.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.client.AdminPaymentClient;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.Event;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.LoyaltyRewardHistoryResponse;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.SystemConfigurationDTO;
import com.octal.actorPay.dto.WalletTransactionResponse;
import com.octal.actorPay.dto.payments.WalletRequest;
import com.octal.actorPay.dto.request.CovertRewardsRequest;
import com.octal.actorPay.dto.request.DeactivateLoyaltyRewardRequest;
import com.octal.actorPay.dto.request.LoyaltyRewardsRequest;
import com.octal.actorPay.entities.LoyaltyRewards;
import com.octal.actorPay.entities.LoyaltyRewardsHistory;
import com.octal.actorPay.entities.LoyaltyRewardsUser;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.repositories.LoyaltyRewardsHistoryRepository;
import com.octal.actorPay.repositories.LoyaltyRewardsRepository;
import com.octal.actorPay.repositories.LoyaltyRewardsUserRepository;
import com.octal.actorPay.service.LoyaltyRewardService;
import com.octal.actorPay.service.PaymentService;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author - Nishant Saraswat
 * this class contain all the logics for loyalty rewards
 */

@Service
public class LoyaltyRewardServiceImpl implements LoyaltyRewardService {

    @Autowired
    LoyaltyRewardsRepository loyaltyRewardsRepository;

    @Autowired
    LoyaltyRewardsHistoryRepository loyaltyRewardsHistoryRepository;

    @Autowired
    LoyaltyRewardsUserRepository loyaltyRewardsUserRepository;

    @Autowired
    SystemConfigurationServiceImpl systemConfigurationService;

    @Autowired
    private SpecificationFactory<LoyaltyRewards> specificationFactory;

    @Autowired
    private SpecificationFactory<LoyaltyRewardsHistory> specificationFactoryHistory;

    @Autowired
    private AdminPaymentClient adminPaymentClient;

    @Override
    public LoyaltyRewardsRequest addLoyaltyRewards(LoyaltyRewardsRequest rewardsRequest, User user) {
        if (rewardsRequest == null) {
            throw new RuntimeException("Invalid Request body");
        }

        LoyaltyRewards loyaltyRewards = new LoyaltyRewards();
        if (Objects.nonNull(rewardsRequest.getId()) && !rewardsRequest.getId().isEmpty()) {
            loyaltyRewards = loyaltyRewardsRepository.findById(rewardsRequest.getId()).orElse(null);

            if (loyaltyRewards == null) {
                throw new RuntimeException("No reward data found");
            }
        }

        if (Objects.isNull(rewardsRequest.getId()) || rewardsRequest.getId().isEmpty()) {
            LoyaltyRewards existedByEvent = loyaltyRewardsRepository.findByEvent(Event.valueOf(rewardsRequest.getEvent()));
            if (existedByEvent != null) {
                throw new RuntimeException("Loyalty config is already existed with this event : " + rewardsRequest.getEvent());
            }
            loyaltyRewards.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        }

            loyaltyRewards.setRewardPoint(rewardsRequest.getRewardPoint());
            loyaltyRewards.setSingleUserLimit(rewardsRequest.getSingleUserLimit());
            loyaltyRewards.setPriceLimit(rewardsRequest.getPriceLimit());
            loyaltyRewards.setEvent(Event.valueOf(rewardsRequest.getEvent()));
            loyaltyRewards.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            LoyaltyRewards save = loyaltyRewardsRepository.save(loyaltyRewards);

            LoyaltyRewardsRequest request = new LoyaltyRewardsRequest();
            request.setId(save.getId());
            request.setRewardPoint(save.getRewardPoint());
            request.setEvent(save.getEvent().toString());
            request.setPriceLimit(save.getPriceLimit());
            request.setSingleUserLimit(save.getSingleUserLimit());
            request.setCreatedAt(save.getCreatedAt());
            request.setUpdatedAt(save.getUpdatedAt());
            return request;
    }

    @Override
    public PageItem<LoyaltyRewardsRequest> getAllLoyaltyRewards(PagedItemInfo pagedInfo, User user) {
        List<LoyaltyRewardsRequest> rewardsRequestList = new ArrayList<>();
        GenericSpecificationsBuilder<LoyaltyRewards> builder = new GenericSpecificationsBuilder<>();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(LoyaltyRewards.class, pagedInfo);
//        prepareOfferSearchQuery(filterRequest, builder);
        Page<LoyaltyRewards> pageResult = loyaltyRewardsRepository.findAll(builder.build(), pageRequest);
        for (LoyaltyRewards reward : pageResult) {
            LoyaltyRewardsRequest rewardsRequest = new LoyaltyRewardsRequest();
            rewardsRequest.setId(reward.getId());
            rewardsRequest.setRewardPoint(reward.getRewardPoint());
            rewardsRequest.setEvent(reward.getEvent().toString());
            rewardsRequest.setPriceLimit(reward.getPriceLimit());
            rewardsRequest.setSingleUserLimit(reward.getSingleUserLimit());
            rewardsRequest.setCreatedAt(reward.getCreatedAt());
            rewardsRequest.setUpdatedAt(reward.getUpdatedAt());
            rewardsRequest.setActive(reward.getActive());
            rewardsRequestList.add(rewardsRequest);
        }
        return new PageItem<>(pageResult.getTotalPages(), pageResult.getTotalElements(), rewardsRequestList, pagedInfo.page,
                pagedInfo.items);
    }

    @Override
    public PageItem<LoyaltyRewardsRequest> getAvailableLoyaltyRewards(PagedItemInfo pagedInfo, boolean b) {
        List<LoyaltyRewardsRequest> rewardsRequestList = new ArrayList<>();
        GenericSpecificationsBuilder<LoyaltyRewards> builder = new GenericSpecificationsBuilder<>();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(LoyaltyRewards.class, pagedInfo);
        builder.with(specificationFactory.isEqual("isActive", true));
//        prepareOfferSearchQuery(filterRequest, builder);
        Page<LoyaltyRewards> pageResult = loyaltyRewardsRepository.findAll(builder.build(), pageRequest);
        for (LoyaltyRewards reward : pageResult) {
            LoyaltyRewardsRequest rewardsRequest = new LoyaltyRewardsRequest();
            rewardsRequest.setId(reward.getId());
            rewardsRequest.setRewardPoint(reward.getRewardPoint());
            rewardsRequest.setEvent(reward.getEvent().toString());
            rewardsRequest.setPriceLimit(reward.getPriceLimit());
            rewardsRequest.setSingleUserLimit(reward.getSingleUserLimit());
            rewardsRequest.setCreatedAt(reward.getCreatedAt());
            rewardsRequest.setUpdatedAt(reward.getUpdatedAt());
            rewardsRequest.setActive(reward.getActive());
            rewardsRequestList.add(rewardsRequest);
        }
        return new PageItem<>(pageResult.getTotalPages(), pageResult.getTotalElements(), rewardsRequestList, pagedInfo.page,
                pagedInfo.items);
    }

    @Override
    public LoyaltyRewardsRequest getRewardById(String rewardId, User user) {
        LoyaltyRewards loyaltyRewards = loyaltyRewardsRepository.findById(rewardId).orElse(null);
        LoyaltyRewardsRequest rewardsRequest = new LoyaltyRewardsRequest();
        if (loyaltyRewards != null) {
            rewardsRequest.setId(loyaltyRewards.getId());
            rewardsRequest.setRewardPoint(loyaltyRewards.getRewardPoint());
            rewardsRequest.setEvent(loyaltyRewards.getEvent().toString());
            rewardsRequest.setPriceLimit(loyaltyRewards.getPriceLimit());
            rewardsRequest.setCreatedAt(loyaltyRewards.getCreatedAt());
            rewardsRequest.setUpdatedAt(loyaltyRewards.getUpdatedAt());
            rewardsRequest.setSingleUserLimit(loyaltyRewards.getSingleUserLimit());
            rewardsRequest.setActive(loyaltyRewards.getActive());
        }
        return rewardsRequest;
    }

    @Override
    public LoyaltyRewardsRequest getRewardByEvent(String event, String userId) {
        LoyaltyRewards loyaltyRewards = loyaltyRewardsRepository.findByEvent(Event.valueOf(event));
        LoyaltyRewardsRequest rewardsRequest = null;
        if (loyaltyRewards != null) {

            if (!loyaltyRewards.getActive()) {
                return null;
            }

            List<LoyaltyRewardsHistory> history = loyaltyRewardsHistoryRepository.findByEventAndUserId(event, userId);
            if (history.size() > loyaltyRewards.getPriceLimit()) {
                return null;
            }
            rewardsRequest = new LoyaltyRewardsRequest();
            rewardsRequest.setId(loyaltyRewards.getId());
            rewardsRequest.setRewardPoint(loyaltyRewards.getRewardPoint());
            rewardsRequest.setEvent(loyaltyRewards.getEvent().toString());
            rewardsRequest.setPriceLimit(loyaltyRewards.getPriceLimit());
            rewardsRequest.setSingleUserLimit(loyaltyRewards.getSingleUserLimit());
            rewardsRequest.setActive(loyaltyRewards.getActive());
        }
        return rewardsRequest;
    }

    @Override
    public void deleteRewardById(DeactivateLoyaltyRewardRequest request) {
        LoyaltyRewards loyaltyRewards = loyaltyRewardsRepository.findById(request.getRewardId()).orElse(null);
        if (loyaltyRewards == null) {
            throw new RuntimeException("No data found with this id : " + request);
        }
        loyaltyRewards.setActive(request.isActive());
        loyaltyRewardsRepository.save(loyaltyRewards);
    }

    @Override
    public PageItem<LoyaltyRewardHistoryResponse> getRewardPointsByUserId(PagedItemInfo pagedInfo, String userId) {
        List<LoyaltyRewardHistoryResponse> rewardsRequestList = new ArrayList<>();
        GenericSpecificationsBuilder<LoyaltyRewardsHistory> builder = new GenericSpecificationsBuilder<>();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(LoyaltyRewardsHistory.class, pagedInfo);
        builder.with(specificationFactoryHistory.isEqual("userId", userId));
//        prepareOfferSearchQuery(filterRequest, builder);
        Page<LoyaltyRewardsHistory> pageResult = loyaltyRewardsHistoryRepository.findAll(builder.build(), pageRequest);
        for (LoyaltyRewardsHistory reward : pageResult) {
            LoyaltyRewardHistoryResponse rewardsResponse = new LoyaltyRewardHistoryResponse();
            rewardsResponse.setId(reward.getId());
            rewardsResponse.setRewardPoint(Long.valueOf(reward.getRewardPoint()));
            rewardsResponse.setEvent(reward.getEvent());
            rewardsResponse.setTransactionId(reward.getTransactionId());
            rewardsResponse.setOrderId(reward.getOrderId());
            rewardsResponse.setReason(reward.getReason());
            rewardsResponse.setCreatedAt(reward.getCreatedAt());
            rewardsResponse.setUpdatedAt(reward.getUpdatedAt());
            rewardsRequestList.add(rewardsResponse);
        }
        return new PageItem<>(pageResult.getTotalPages(), pageResult.getTotalElements(), rewardsRequestList, pagedInfo.page,
                pagedInfo.items);
    }

    @Override
    public LoyaltyRewardHistoryResponse getTotalRewards(String userId) {
        LoyaltyRewardHistoryResponse response = new LoyaltyRewardHistoryResponse();
        LoyaltyRewardsUser userReward = loyaltyRewardsUserRepository.findByUserId(userId).orElse(null);

        if (userReward == null) {
            response.setCurrentValue(0L);
            response.setRewardPoint(0L);
        } else {
            SystemConfigurationDTO systemConfiguration = systemConfigurationService
                    .getSystemConfigurationDetailsByKey(CommonConstant.LOYALTY_REWARD_CONVERSION_RATE);
            if (systemConfiguration == null) {
                throw new RuntimeException("No conversion rate available");
            }
            Long currentValue = Long.parseLong(systemConfiguration.getParamValue()) * userReward.getTotalRewards();
            response.setCurrentValue(currentValue);
            response.setRewardPoint(userReward.getTotalRewards());
        }

        return response;
    }

    @Override
    public CovertRewardsRequest convertReward(CovertRewardsRequest conversionRequest) {
        CovertRewardsRequest response = new CovertRewardsRequest();
        LoyaltyRewardsUser userReward = loyaltyRewardsUserRepository.findByUserId(conversionRequest.getUserId()).orElse(null);

        if (userReward == null) {
            response.setRewardPoint(0L);
            response.setTransferredAmount(0.0);
        } else {
            List<SystemConfigurationDTO> systemConfiguration = systemConfigurationService
                    .getSystemConfigurationDetailsByKeys(List.of(CommonConstant.LOYALTY_REWARD_CONVERSION_RATE, CommonConstant.LOYALTY_REWARD_THRESHOLD_VALUE, CommonConstant.ADMIN_ID));

            if (systemConfiguration.stream().noneMatch(v -> v.getParamName().equals(CommonConstant.LOYALTY_REWARD_CONVERSION_RATE))) {
                throw new RuntimeException("No conversion rate available");
            }

            SystemConfigurationDTO systemConfigurationByValue = systemConfiguration.stream().filter(v->v.getParamName().equals(CommonConstant.LOYALTY_REWARD_THRESHOLD_VALUE))
                    .findAny().orElse(null);

            if (systemConfigurationByValue != null) {
                if (userReward.getTotalRewards() < Long.parseLong(systemConfigurationByValue.getParamValue())) {
                    throw new RuntimeException("You have less reward points");
                }
            }

            SystemConfigurationDTO systemConfigurationByRate = systemConfiguration.stream().filter(v->v.getParamName().equals(CommonConstant.LOYALTY_REWARD_CONVERSION_RATE))
                    .findFirst().get();

            Double currentValue = Double.parseDouble(systemConfigurationByRate.getParamValue()) * userReward.getTotalRewards();

            WalletRequest walletRequest = new WalletRequest();
            walletRequest.setAmount(currentValue);
            walletRequest.setCurrentUserId(conversionRequest.getUserId());
            SystemConfigurationDTO systemConfigurationAdmin = systemConfiguration.stream().filter(v->v.getParamName().equals(CommonConstant.ADMIN_ID))
                    .findAny().get();
            walletRequest.setAdminUserId(systemConfigurationAdmin.getParamValue());

            ApiResponse apiResponse = adminPaymentClient.addRewardToWallet(walletRequest).getBody();

            if (Objects.nonNull(apiResponse)) {
                WalletTransactionResponse walletResponse = new ObjectMapper().convertValue(apiResponse.getData(), WalletTransactionResponse.class);
                LoyaltyRewardHistoryResponse rewardHistoryResponse = new LoyaltyRewardHistoryResponse();
                rewardHistoryResponse.setType("DEBIT");
                rewardHistoryResponse.setRewardPoint(userReward.getTotalRewards());
                rewardHistoryResponse.setTransactionId(walletResponse.getParentTransactionId());
                rewardHistoryResponse.setCurrentValue(currentValue);
                rewardHistoryResponse.setReason(Event.CONVERT_REWARD.name());
                rewardHistoryResponse.setEvent(Event.CONVERT_REWARD.name());
                rewardHistoryResponse.setUserId(conversionRequest.getUserId());
                updateReward(rewardHistoryResponse);
            }
            response.setRewardPoint(userReward.getTotalRewards());
            response.setTransferredAmount(currentValue);
        }
        return response;
    }

    @Override
    public LoyaltyRewardHistoryResponse updateReward(LoyaltyRewardHistoryResponse updateRequest) {
        LoyaltyRewardsHistory history = new LoyaltyRewardsHistory();
        history.setEvent(updateRequest.getEvent());
        history.setRewardPoint(updateRequest.getRewardPoint().toString());
        history.setReason(updateRequest.getReason());
        history.setOrderId(updateRequest.getOrderId());
        history.setUserId(updateRequest.getUserId());
        history.setTransactionId(updateRequest.getTransactionId());
        history.setType(updateRequest.getType());
        history.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        history.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        loyaltyRewardsHistoryRepository.save(history);

        LoyaltyRewardsUser userReward = loyaltyRewardsUserRepository.findByUserId(updateRequest.getUserId()).orElse(null);
        if (userReward == null) {
            userReward = new LoyaltyRewardsUser();
        }
        userReward.setUserId(updateRequest.getUserId());
        if (updateRequest.getType().equals("DEBIT")) {
            userReward.setTotalRewards(userReward.getTotalRewards() - updateRequest.getRewardPoint());
        } else {
            userReward.setTotalRewards(userReward.getTotalRewards() + updateRequest.getRewardPoint());
        }
        userReward.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        loyaltyRewardsUserRepository.save(userReward);
        return updateRequest;
    }
}