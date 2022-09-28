package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.request.ReferralHistoryResponse;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.service.ReferralUserService;
import com.octal.actorPay.utils.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/referral")
public class UserReferralController {

    @Autowired
    private CommonService commonService;

    @Autowired
    private ReferralUserService referralUserService;

    @GetMapping("/history/paged")
    public ResponseEntity<ApiResponse> getReferralHistoryByUserId(@RequestParam(name = "userId", required = false, defaultValue = "") String userId,
                                                                  @RequestParam(defaultValue = "0") Integer pageNo,
                                                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                                                  @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                  @RequestParam(defaultValue = "true") boolean asc,
                                                                  HttpServletRequest request) {
        if (userId.isBlank()) {
            User user = commonService.getLoggedInUser(request);
            userId = user.getId();
        }
        PagedItemInfo pagedInfo = new PagedItemInfo(pageNo, pageSize, sortBy, asc, null);
        PageItem<ReferralHistoryResponse> response = referralUserService.getReferralHistoryByUserId(pagedInfo, userId);
        return new ResponseEntity<>(new ApiResponse("referral history", response,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }
}
