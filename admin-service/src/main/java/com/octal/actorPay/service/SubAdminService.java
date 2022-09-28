package com.octal.actorPay.service;

import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.SubAdminFilterRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserPage;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface SubAdminService {

    void createSubAdmin(SubAdminDTO subAdminDTO, String currentUser);

    void updateSubAdmin(SubAdminDTO subAdminDTO, String currentUser);

    void deleteSubAdmin(List<String> ids, String currentUse ) throws InterruptedException;

    SubAdminDTO getSubAdminInfo(String id, String currentUser);

    /**
     * get all the sub admin list
     * @param pagedInfo - contains pagination parameters
     * @return - returns pageItem Sub admin dto that contains list of sub admins
     */

    PageItem<SubAdminDTO> getAllSubAdminPaged(PagedItemInfo pagedInfo, String currentUser, SubAdminFilterRequest subAdminFilterRequest);

    void changeSubAdminPassword(ChangePasswordDTO changePasswordDTO, String userId);

    void changeSubAdminStatus(String id, Boolean status);

    /* */
    List<User> findUserWithKey(UserDTO userReq);

    Map<String, Object> findUserWithPaginationAndSorting(int pageNo, int pageSize);





}
