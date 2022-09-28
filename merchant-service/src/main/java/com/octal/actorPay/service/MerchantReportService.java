package com.octal.actorPay.service;

import com.octal.actorPay.dto.MerchantReportHistoryDTO;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.ReportFilter;
import com.octal.actorPay.dto.ReportURLResponse;

public interface MerchantReportService {
    ReportURLResponse createReport(ReportFilter reportFilter);

    PageItem<MerchantReportHistoryDTO> searchMerchantReport(PagedItemInfo pagedInfo, ReportFilter reportFilter);
}
