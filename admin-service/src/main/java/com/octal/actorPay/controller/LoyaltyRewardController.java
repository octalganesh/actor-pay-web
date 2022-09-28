package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.LoyaltyRewardHistoryResponse;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.payments.DeactivateRequest;
import com.octal.actorPay.dto.request.CovertRewardsRequest;
import com.octal.actorPay.dto.request.DeactivateLoyaltyRewardRequest;
import com.octal.actorPay.dto.request.LoyaltyRewardsRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.service.LoyaltyRewardService;
import javassist.tools.rmi.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1/loyalty/rewards")
public class LoyaltyRewardController extends PagedItemsController {

    private LoyaltyRewardService loyaltyRewardService;
    private UserRepository userRepository;

    public LoyaltyRewardController (LoyaltyRewardService loyaltyRewardService, UserRepository userRepository) {
        this.loyaltyRewardService = loyaltyRewardService;
        this.userRepository = userRepository;
    }

    @Secured("ROLE_LOYALTY_ADD")
    @PostMapping
    public ResponseEntity<ApiResponse> addLoyaltyRewards(@Valid @RequestBody LoyaltyRewardsRequest rewardsRequest, HttpServletRequest request) {
        User user = getUser(request);
        LoyaltyRewardsRequest response = loyaltyRewardService.addLoyaltyRewards(rewardsRequest, user);
        return new ResponseEntity<>(new ApiResponse("Loyalty reward details", response, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_LOYALTY_UPDATE")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateLoyaltyRewards(@Valid @RequestBody LoyaltyRewardsRequest rewardsRequest, HttpServletRequest request) throws ObjectNotFoundException {
        User user = getUser(request);
        if (user == null) {
            throw new ObjectNotFoundException("Invalid user to access the rewards");
        }
        loyaltyRewardService.addLoyaltyRewards(rewardsRequest, user);
        return new ResponseEntity<>(new ApiResponse(String.format("Offer Updated successfully Offer Id : %s ", ""), rewardsRequest,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_LOYALTY_VIEW_LIST")
    @PostMapping("/list/paged")
    public ResponseEntity<ApiResponse> getAllLoyaltyRewards(@RequestParam(defaultValue = "0") Integer pageNo,
                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                   @RequestParam(defaultValue = "createdAt") String sortBy,
                                                   @RequestParam(defaultValue = "false") boolean asc, HttpServletRequest request) {
        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        User user = getUser(request);
        PageItem<LoyaltyRewardsRequest> pageResult = loyaltyRewardService.getAllLoyaltyRewards(pagedInfo, user);
        return new ResponseEntity<>(new ApiResponse("reward List ", pageResult,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/available")
    public ResponseEntity<ApiResponse> getAvailableLoyaltyRewards(@RequestParam(defaultValue = "0") Integer pageNo,
                                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                                          @RequestParam(defaultValue = "createdAt") String sortBy,
                                                          @RequestParam(defaultValue = "true") boolean asc) {
        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        PageItem<LoyaltyRewardsRequest> pageResult = loyaltyRewardService.getAvailableLoyaltyRewards(pagedInfo, true);
        return new ResponseEntity<>(new ApiResponse("reward List ", pageResult,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/id/{rewardId}")
    public ResponseEntity<ApiResponse> getRewardById(@PathVariable("rewardId") String rewardId, HttpServletRequest request) {
        User user = getUser(request);
        LoyaltyRewardsRequest response = loyaltyRewardService.getRewardById(rewardId, user);
        return new ResponseEntity<>(new ApiResponse("reward Details", response,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/{event}/{userId}")
    public ResponseEntity<ApiResponse> getRewardByEvent(@PathVariable("event") String event, @PathVariable("userId") String userId, HttpServletRequest request) {
        LoyaltyRewardsRequest response = loyaltyRewardService.getRewardByEvent(event, userId);
        return new ResponseEntity<>(new ApiResponse("reward Details", response,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_LOYALTY_DEACTIVATE")
    @PostMapping("/deactivate")
    public ResponseEntity<ApiResponse> deleteOfferById(@RequestBody DeactivateLoyaltyRewardRequest deactivateRequest, HttpServletRequest request) {

        User user = getUser(request);
        loyaltyRewardService.deleteRewardById(deactivateRequest);
        return new ResponseEntity<>(new ApiResponse("Loyalty Event status change Successfully", "",
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/userId/{userId}")
    public ResponseEntity<ApiResponse> getRewardPointsByUserId(@RequestParam(defaultValue = "0") Integer pageNo,
                                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                                               @RequestParam(defaultValue = "createdAt") String sortBy,
                                                               @RequestParam(defaultValue = "true") boolean asc,
                                                               @PathVariable("userId") String userId, HttpServletRequest request) {
        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        PageItem<LoyaltyRewardHistoryResponse> response = loyaltyRewardService.getRewardPointsByUserId(pagedInfo, userId);
        return new ResponseEntity<>(new ApiResponse("reward history", response,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/total/{userId}")
    public ResponseEntity<ApiResponse> getTotalRewards(@PathVariable("userId") String userId) {
        LoyaltyRewardHistoryResponse response = loyaltyRewardService.getTotalRewards(userId);
        return new ResponseEntity<>(new ApiResponse("reward Details", response,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/convert")
    public ResponseEntity<ApiResponse> convertReward(@RequestBody CovertRewardsRequest conversionRequest) {
        CovertRewardsRequest response = loyaltyRewardService.convertReward(conversionRequest);
        return new ResponseEntity<>(new ApiResponse("convert details ", response,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/update/history")
    public ResponseEntity<ApiResponse> updateReward(@RequestBody LoyaltyRewardHistoryResponse updateRequest) {
        LoyaltyRewardHistoryResponse response = loyaltyRewardService.updateReward(updateRequest);
        return new ResponseEntity<>(new ApiResponse("update details ", response,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    private User getUser(HttpServletRequest request) {
        String userName = request.getHeader("userName");
        User user = userRepository.findByEmailAndIsActiveTrueAndDeletedFalse(userName).orElse(null);
        return user;
    }
}