package com.octal.actorPay.service;

import com.octal.actorPay.dto.LoyaltyRewardHistoryResponse;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.request.CovertRewardsRequest;
import com.octal.actorPay.dto.request.DeactivateLoyaltyRewardRequest;
import com.octal.actorPay.dto.request.LoyaltyRewardsRequest;
import com.octal.actorPay.entities.User;
import org.springframework.stereotype.Service;

@Service
public interface LoyaltyRewardService {
    LoyaltyRewardsRequest addLoyaltyRewards(LoyaltyRewardsRequest rewardsRequest, User user);

    PageItem<LoyaltyRewardsRequest> getAllLoyaltyRewards(PagedItemInfo pagedInfo, User user);

    PageItem<LoyaltyRewardsRequest> getAvailableLoyaltyRewards(PagedItemInfo pagedInfo, boolean b);

    LoyaltyRewardsRequest getRewardById(String rewardId, User user);

    LoyaltyRewardsRequest getRewardByEvent(String event, String userId);

    void deleteRewardById(DeactivateLoyaltyRewardRequest request);

    PageItem<LoyaltyRewardHistoryResponse> getRewardPointsByUserId(PagedItemInfo pagedInfo, String userId);

    LoyaltyRewardHistoryResponse getTotalRewards(String userId);

    CovertRewardsRequest convertReward(CovertRewardsRequest conversionRequest);

    LoyaltyRewardHistoryResponse updateReward(LoyaltyRewardHistoryResponse updateRequest);
}
