package com.octal.actorPay.service;

import com.octal.actorPay.dto.EkycFilterRequest;
import com.octal.actorPay.dto.EkycUserResponse;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.UserDocumentDTO;
import com.octal.actorPay.dto.request.EkycRequest;
import com.octal.actorPay.dto.request.EkycUpdateRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserDocumentService {

    public EkycUserResponse verify(MultipartFile frontPart, MultipartFile backPart, User user) throws Exception;

    boolean isKycVerified(String userId);

    UserDocument getKYCByDocType(String userId, String docType);

    List<UserDocument> findByUser(String userId);

    UserDocumentDTO updateEkycStatus(EkycUpdateRequest ekycUpdateRequest);

    PageItem<EkycFilterRequest> getAllEkyc(PagedItemInfo pagedInfo, EkycFilterRequest filterRequest);


}
