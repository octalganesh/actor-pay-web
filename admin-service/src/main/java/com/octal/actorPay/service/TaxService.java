package com.octal.actorPay.service;

import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.TaxDTO;
import com.octal.actorPay.dto.request.TaxFilterRequest;
import com.octal.actorPay.exceptions.ObjectNotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface TaxService {

    TaxDTO addTax(TaxDTO taxDTO);

    TaxDTO updateTax(TaxDTO taxDTO);

    TaxDTO getTaxById(String taxId) throws ObjectNotFoundException;

    PageItem<TaxDTO> getAllTax(PagedItemInfo pagedItemInfo);
    PageItem<TaxDTO> getAllTax(PagedItemInfo pagedItemInfo, TaxFilterRequest filterRequest);
    List<TaxDTO> getAllTax();

    PageItem<TaxDTO> getAllTaxByStatus(PagedItemInfo pagedItemInfo, Boolean status);
    List<TaxDTO> getAllTaxByStatus(Boolean isActive, String sortBy, Boolean asc);
    TaxDTO removeTax(String taxId);

    void removeTaxByHsnCode(String hsnCode) throws ObjectNotFoundException;

    TaxDTO getTaxByHsnCode(String hsnCode);

    TaxDTO changeStatus(String taxId,Boolean isActive) throws ObjectNotFoundException;

    PageItem<TaxDTO> getAllActiveTaxes(PagedItemInfo pagedItemInfo);
    List<TaxDTO> getAllActiveTaxes();

}
