package com.octal.actorPay.service;

import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.EkycRequest;
import com.octal.actorPay.dto.request.EkycUpdateRequest;
import com.octal.actorPay.dto.request.MerchantFilterRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserDocumentService {

    public EkycUserResponse verify(MultipartFile frontPart, MultipartFile backPart, User user) throws Exception;

    boolean isKycVerified(String userId);

    UserDocument getKYCByDocType(String userId, String docType);

    List<UserDocument> findByUser(String userId);

    UserDocument updateEkycStatus(EkycUpdateRequest ekycUpdateRequest);

    PageItem<EkycFilterRequest> getAllEkyc(PagedItemInfo pagedInfo, EkycFilterRequest filterRequest);
}
