package com.octal.actorPay.controller;

import com.octal.actorPay.dto.AdminReportHistoryDTO;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.ReportFilter;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.service.AdminReportService;
import com.octal.actorPay.service.AdminService;
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
public class AdminReportController extends PagedItemsController {

    private final AdminService adminService;
    private final AdminReportService adminReportService;

    public AdminReportController(AdminService adminService, AdminReportService adminReportService) {
        this.adminService = adminService;
        this.adminReportService = adminReportService;
    }


    @Secured("ROLE_ADMIN_REPORT_CREATE")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createReport(@RequestBody ReportFilter reportFilter, HttpServletRequest request) throws Exception {
        String userName = request.getHeader("userName");
        User user = adminService.getUserByEmailId(userName);
        if (user == null || !user.getActive()) {
            throw new RuntimeException("User is not Active to Request Transaction");
        }
        reportFilter.setUserId(user.getId());
        reportFilter.setAdminId(user.getId());
        reportFilter.setEmail(user.getEmail());
        reportFilter.setContact(user.getContactNumber());
        reportFilter.setName(user.getFirstName() + "  " + user.getLastName());
        return new ResponseEntity<>(new ApiResponse("report url" , adminReportService.createReport(reportFilter),String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK );
    }

    @Secured("ROLE_ADMIN_REPORT_LIST_VIEW")
    @PostMapping(value = "/list/paged")
    public ResponseEntity<ApiResponse> searchMerchantReport(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                            @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                            @RequestParam(name = "userType", required = false, defaultValue = "merchant") String userType,
                                                            @RequestBody ReportFilter reportFilter,
                                                            HttpServletRequest request) throws Exception {

        String userName = request.getHeader("userName");
        User user = adminService.getUserByEmailId(userName);
        if (user == null || !user.getActive()) {
            throw new RuntimeException("User is not Active to Request Transaction");
        }

        reportFilter.setUserId(user.getId());
        reportFilter.setAdminId(user.getId());
        reportFilter.setEmail(user.getEmail());
        reportFilter.setContact(user.getContactNumber());
        reportFilter.setName(user.getFirstName() + "  " + user.getLastName());


        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        PageItem<AdminReportHistoryDTO> searchReportDetails =
                adminReportService.searchAdminReport(pagedInfo, reportFilter);
        ApiResponse apiResponse = new ApiResponse("Report History Result: ", searchReportDetails, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
