package com.octal.actorPay.service;

import com.octal.actorPay.dto.AdminReportHistoryDTO;
import com.octal.actorPay.dto.AdminReportURLResponse;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.ReportFilter;
import org.springframework.stereotype.Service;

@Service
public interface AdminReportService {

    AdminReportURLResponse createReport(ReportFilter reportFilter);

    PageItem<AdminReportHistoryDTO> searchAdminReport(PagedItemInfo pagedInfo, ReportFilter reportFilter);

}
