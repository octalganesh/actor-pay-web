package com.octal.actorPay.service;

import com.octal.actorPay.dto.OutletDto;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.request.OutletFilterRequest;
import com.octal.actorPay.entities.Outlet;
import feign.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface OutletService extends CrudOperation<Outlet,OutletDto> {

    PageItem<OutletDto> listWithPagination(PagedItemInfo pagedInfo, OutletFilterRequest filterRequest, String currentUser);

    Map delete(List<String> ids, String currentUser) throws InterruptedException;


    void changeOutletStatus(String id, Boolean status);

    Long findCountById(String outletId);
}
