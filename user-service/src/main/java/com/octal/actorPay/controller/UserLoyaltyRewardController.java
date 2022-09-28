package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.LoyaltyRewardHistoryResponse;
import com.octal.actorPay.dto.request.CovertRewardsRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.feign.clients.AdminClient;
import com.octal.actorPay.utils.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/loyalty/rewards")
public class UserLoyaltyRewardController {

    private AdminClient adminClient;

    private CommonService commonService;

    @Autowired
    public  UserLoyaltyRewardController(AdminClient adminClient, CommonService commonService) {
        this.adminClient = adminClient;
        this.commonService = commonService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getRewardPointsByUserId(@RequestParam(defaultValue = "0") Integer pageNo,
                                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                                               @RequestParam(defaultValue = "createdAt") String sortBy,
                                                               @RequestParam(defaultValue = "true") boolean asc,
                                                               HttpServletRequest request) {
        User user = commonService.getLoggedInUser(request);
        return adminClient.getRewardPointsByUserId(pageNo, pageSize, sortBy, asc, user.getId());
    }

    @GetMapping("/total")
    public ResponseEntity<ApiResponse> getTotalRewards(HttpServletRequest request) {
        User user = commonService.getLoggedInUser(request);
        return adminClient.getTotalRewards(user.getId());
    }

    @PostMapping("/convert")
    public ResponseEntity<ApiResponse> convertReward(HttpServletRequest request) {
        User user = commonService.getLoggedInUser(request);
        CovertRewardsRequest conversionRequest = new CovertRewardsRequest();
        conversionRequest.setUserId(user.getId());
        return adminClient.convertReward(conversionRequest);
    }

    @PostMapping("/update/history")
    public ResponseEntity<ApiResponse> updateReward(@RequestBody LoyaltyRewardHistoryResponse updateRequest, HttpServletRequest request) {
        User user = commonService.getLoggedInUser(request);

        return adminClient.updateReward(updateRequest);
    }
}
