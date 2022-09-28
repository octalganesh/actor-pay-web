package com.octal.actorPay.service.impl;

import com.octal.actorPay.dto.*;
import com.octal.actorPay.entities.Modules;
import com.octal.actorPay.entities.Screens;
import com.octal.actorPay.repositories.ScreensRepository;
import com.octal.actorPay.service.ModulesService;
import com.octal.actorPay.transformer.AdminTransformer;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModulesServiceImpl implements ModulesService {

    @Autowired
    private ScreensRepository screensRepository;

    @Override
    public PageItem<ScreenDTO>  getModules(PagedItemInfo pagedInfo, String userName) {
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(Screens.class, pagedInfo);
        Page<Screens> pagedResult = screensRepository.findAll(pageRequest);
        List<ScreenDTO> content = pagedResult.getContent().stream().map(AdminTransformer.SCREENS_TO_DTO)
                .collect(Collectors.toList());

        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), content, pagedInfo.page,
                pagedInfo.items);
    }
}