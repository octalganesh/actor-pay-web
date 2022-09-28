package com.octal.actorPay.service;

import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.request.ReferralHistoryResponse;

public interface ReferralUserService {

    PageItem<ReferralHistoryResponse> getReferralHistoryByUserId(PagedItemInfo pagedInfo, String userId);
}
