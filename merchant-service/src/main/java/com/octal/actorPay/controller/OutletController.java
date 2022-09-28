package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.OutletDto;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.request.OutletFilterRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.service.OutletService;
import com.octal.actorPay.utils.CommonUtils;
import com.octal.actorPay.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/v1/merchant/outlet")
public class OutletController extends BaseController {

    @Autowired
    private OutletService outletService;

    @Secured("ROLE_OUTLET_CREATE")
    @PostMapping("/create")
    public ResponseEntity<?> createOutlet(@RequestBody @Valid OutletDto dto, final HttpServletRequest request) {
        User user = getAuthorizedUser(request);
        String outletID = outletService.create(dto, user.getEmail());
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Outlet created successfully", outletID,
                HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_OUTLET_UPDATE")
    @PutMapping("/update")
    public ResponseEntity<?> updateOutlet(@RequestBody @Valid OutletDto dto, final HttpServletRequest request) {
        User user = getAuthorizedUser(request);
        outletService.update(user.getEmail(), dto);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Outlet updated successfully", null,
                HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_OUTLET_LIST_BY_ID")
    @GetMapping(value = "/by/id/{id}")
    public ResponseEntity getOutletDetails(@PathVariable("id") String id, final HttpServletRequest request) {
//        String actor = request.getHeader("userName");
        User user = getAuthorizedUser(request);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Outlet details", outletService.read(id, user.getEmail()),
                HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_OUTLET_LIST_VIEW")
    @PostMapping(value = "/list/paged")
    public ResponseEntity getAllUsersPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "createdAt") String sortBy,
                                           @RequestParam(defaultValue = "false") boolean asc,
                                           @RequestBody OutletFilterRequest filterRequest,
                                           HttpServletRequest request) {
        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        User user = getAuthorizedUser(request);
        return new ResponseEntity<>(new ApiResponse("All Outlet fetched Successfully.",
                outletService.listWithPagination(pagedInfo, filterRequest, user.getEmail()),
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_OUTLET_DELETE_BY_ID")
    @DeleteMapping("/delete/by/ids")
    public ResponseEntity<ApiResponse> deleteUserByIds(@RequestBody Map<String, List<String>> userIds, final HttpServletRequest request) throws InterruptedException {
        User user = getAuthorizedUser(request);
        Map data = outletService.delete(userIds.get("ids"), user.getEmail());
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Outlet Deletion Status: ", data,
                HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_OUTLET_CHANGE_STATUS")
    @PutMapping("/change/status")
    public ResponseEntity<ApiResponse> changeUserStatus(@RequestParam(name = "id") String id,
                                                        @RequestParam(name = "status") Boolean status,HttpServletRequest request) {
        User user = getAuthorizedUser(request);
        outletService.changeOutletStatus(id, status);
        return new ResponseEntity<>(new ApiResponse("Outlet status updated successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }
}
