package com.octal.actorPay.service;

import com.octal.actorPay.dto.ScreenDTO;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import org.springframework.stereotype.Service;

@Service
public interface ModulesService {
    PageItem<ScreenDTO> getModules(PagedItemInfo pagedInfo, String userName);
}