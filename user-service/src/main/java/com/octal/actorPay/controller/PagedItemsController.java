package com.octal.actorPay.controller;

import com.octal.actorPay.dto.PagedItemInfo;

public class PagedItemsController  {
    protected PagedItemInfo     createPagedInfo(int pageNo, int pageSize, String sortingField, boolean asc, String filterQuery) {
        // Requested pages started from 0 if you want to start from one then do pageNo - 1
        return new PagedItemInfo(pageNo, pageSize, sortingField, asc, filterQuery);
    }

}