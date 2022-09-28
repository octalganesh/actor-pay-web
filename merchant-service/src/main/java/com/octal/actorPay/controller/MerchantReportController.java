package com.octal.actorPay.controller;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.MerchantReportHistoryDTO;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.ReportFilter;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.service.MerchantReportService;
import com.octal.actorPay.utils.CommonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

/**
 * @author - Nishant Saraswat
 * this class contain all the end points for merhcant reports
 */

@RestController
@RequestMapping("/v1/report")
public class MerchantReportController extends BaseController {

    private final CommonService commonService;
    private final MerchantReportService merchantReportService;

    public MerchantReportController(CommonService commonService, MerchantReportService merchantReportService) {
        this.commonService = commonService;
        this.merchantReportService = merchantReportService;
    }


    @Secured("ROLE_MERCHANT_REPORT_CREATE")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createReport(@RequestBody ReportFilter reportFilter, HttpServletRequest request) throws Exception {
        User user = getAuthorizedUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to Withdraw Money from Wallet");
        }
        reportFilter.setUserId(user.getId());
        reportFilter.setMerchantId(user.getMerchantDetails().getId());
        reportFilter.setEmail(user.getEmail());
        reportFilter.setContact(user.getContactNumber());
        reportFilter.setName(user.getFirstName() + "  " + user.getLastName());
        return new ResponseEntity<>(new ApiResponse("report url" , merchantReportService.createReport(reportFilter),String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK );
    }

    @Secured("ROLE_MERCHANT_REPORT_LIST_VIEW")
    @PostMapping(value = "/list/paged")
    public ResponseEntity<ApiResponse> searchMerchantReport(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                               @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                               @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                               @RequestParam(name = "userType", required = false, defaultValue = "merchant") String userType,
                                                               @RequestBody ReportFilter reportFilter,
                                                               HttpServletRequest request) throws Exception {

        User user = getAuthorizedUser(request);
        if (userType != null && userType.equalsIgnoreCase(CommonConstant.USER_TYPE_MERCHANT)) {
            reportFilter.setUserId(user.getId());
            reportFilter.setMerchantId(user.getMerchantDetails().getId());
            reportFilter.setEmail(user.getEmail());
            reportFilter.setContact(user.getContactNumber());
            reportFilter.setName(user.getFirstName() + "  " + user.getLastName());
        }

        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        PageItem<MerchantReportHistoryDTO> searchReportDetails =
                merchantReportService.searchMerchantReport(pagedInfo, reportFilter);
        ApiResponse apiResponse = new ApiResponse("Report History Result: ", searchReportDetails, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
