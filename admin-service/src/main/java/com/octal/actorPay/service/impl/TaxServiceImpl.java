package com.octal.actorPay.service.impl;

import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.TaxDTO;
import com.octal.actorPay.dto.request.TaxFilterRequest;
import com.octal.actorPay.entities.Tax;
import com.octal.actorPay.exceptions.ExceptionUtils;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.repositories.TaxRepository;
import com.octal.actorPay.service.TaxService;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaxServiceImpl implements TaxService {

    private TaxRepository taxRepository;
    private SpecificationFactory<Tax> taxSpecificationFactory;

    public TaxServiceImpl(TaxRepository taxRepository,
                          SpecificationFactory<Tax> taxSpecificationFactory) {
        this.taxRepository = taxRepository;
        this.taxSpecificationFactory = taxSpecificationFactory;
    }

    @Override
    public TaxDTO addTax(TaxDTO taxDTO) {
        try {
            Tax tax = taxDtoToTax(taxDTO, null);
            tax.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            tax.setActive(true);
            Tax newTax = taxRepository.save(tax);
            TaxDTO newTaxDto = taxToTaxDto(newTax);
            return newTaxDto;
        } catch (DataIntegrityViolationException e) {
            ExceptionUtils.duplicateCheckHandleException(e, taxDTO.getHsnCode());
        } catch (Exception e) {
            throw new RuntimeException("Unable to update the Tax data");
        }
        return null;
    }

    @Override
    public TaxDTO updateTax(TaxDTO taxDTO) {
        Tax tax = taxRepository.findById(taxDTO.getId()).orElse(null);
//        if (tax == null) {
//            throw new RuntimeException(String.format("Tax data is not found"));
//        }
//        String hsnCode = tax.getHsnCode();
//        if(!hsnCode.equalsIgnoreCase(taxDTO.getHsnCode())) {
//            Tax taxDup = taxRepository.findByHsnCodeAndDeletedFalse(hsnCode);
//            if (taxDup != null) {
//                throw new RuntimeException(String.format("Tax with HSN Code is already exist  %s", taxDTO.getHsnCode()));
//            }
//        }
        try {
            Tax updateTax = taxDtoToTax(taxDTO, tax);
            tax.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            updateTax = taxRepository.save(updateTax);
            TaxDTO updateTaxDto = taxToTaxDto(updateTax);
            return updateTaxDto;
        } catch (DataIntegrityViolationException e) {
            System.out.println("Most Specific " + e.getMostSpecificCause().getMessage());
            ExceptionUtils.duplicateCheckHandleException(e, taxDTO.getHsnCode());
        } catch (Exception e) {
            throw new RuntimeException("Unable to update the Tax data");
        }
        return null;
    }

    @Override
    public TaxDTO getTaxById(String taxId) throws ObjectNotFoundException {
        Tax tax = taxRepository.findById(taxId).orElse(null);
        if (tax == null) {
            throw new ObjectNotFoundException(String.format("Tax id is not found for the id %s ", taxId));
        }
        TaxDTO taxDTO = taxToTaxDto(tax);
        return taxDTO;
    }

    @Override
    public TaxDTO changeStatus(String taxId, Boolean isActive) throws ObjectNotFoundException {
        Optional<Tax> tax = taxRepository.findById(taxId);
        if (tax.isPresent()) {
            tax.get().setActive(isActive);
            tax.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            Tax updatedTax = taxRepository.save(tax.get());
            return taxToTaxDto(updatedTax);
        } else {
            throw new ObjectNotFoundException(String.format("The Tax data is  not found for the given %s: ", taxId));
        }
    }

    @Override
    public PageItem<TaxDTO> getAllTax(PagedItemInfo pagedItemInfo) {
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(Tax.class, pagedItemInfo);
        List<TaxDTO> taxDTOs = new ArrayList<>();
        Page<Tax> pagedResult = taxRepository.findAll(pageRequest);
        for (Tax tax : pagedResult.getContent()) {
            taxDTOs.add(taxToTaxDto(tax));
        }
        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), taxDTOs, pagedItemInfo.page,
                pagedItemInfo.items);
    }

    @Override
    public PageItem<TaxDTO> getAllTax(PagedItemInfo pagedItemInfo, TaxFilterRequest filterRequest) {
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(Tax.class, pagedItemInfo);
        GenericSpecificationsBuilder<Tax> builder = new GenericSpecificationsBuilder<>();
        // prepare search query
        List<TaxDTO> taxDTOs = new ArrayList<>();
        prepareTaxSearchQuery(filterRequest, builder);
        Page<Tax> pagedResult = taxRepository.findAll(builder.build(), pageRequest);
        for (Tax tax : pagedResult.getContent()) {
            taxDTOs.add(taxToTaxDto(tax));
        }
        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), taxDTOs, pagedItemInfo.page,
                pagedItemInfo.items);
    }

    @Override
    public List<TaxDTO> getAllTax() {
        List<TaxDTO> taxDTOs = new ArrayList<>();
        List<Tax> taxList = taxRepository.findAll();
        for (Tax tax : taxList) {
            taxDTOs.add(taxToTaxDto(tax));
        }
        return taxDTOs;
    }

    @Override
    public PageItem<TaxDTO> getAllTaxByStatus(PagedItemInfo pagedItemInfo, Boolean status) {
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(Tax.class, pagedItemInfo);
        List<TaxDTO> taxDTOs = new ArrayList<>();
        Page<Tax> pagedResult = taxRepository.findByIsActive(pageRequest, status);
        for (Tax tax : pagedResult.getContent()) {
            taxDTOs.add(taxToTaxDto(tax));
        }
        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), taxDTOs, pagedItemInfo.page,
                pagedItemInfo.items);
    }

    @Override
    public List<TaxDTO> getAllTaxByStatus(Boolean isActive, String sortBy, Boolean asc) {
        List<TaxDTO> taxDTOs = new ArrayList<>();
        List<Tax> taxList = null;
        if (asc) {
            taxList = taxRepository.findByIsActive(isActive, Sort.by(sortBy).ascending());
        } else {
            taxList = taxRepository.findByIsActive(isActive, Sort.by(sortBy).descending());
        }

        for (Tax tax : taxList) {
            taxDTOs.add(taxToTaxDto(tax));
        }
        return taxDTOs;
    }

    @Override
    public PageItem<TaxDTO> getAllActiveTaxes(PagedItemInfo pagedItemInfo) {
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(Tax.class, pagedItemInfo);
        List<TaxDTO> taxDTOs = new ArrayList<>();
        Page<Tax> pagedResult = taxRepository.findByIsActive(pageRequest, true);
        for (Tax tax : pagedResult.getContent()) {
            taxDTOs.add(taxToTaxDto(tax));
        }
        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), taxDTOs, pagedItemInfo.page,
                pagedItemInfo.items);
    }

    @Override
    public List<TaxDTO> getAllActiveTaxes() {
        List<TaxDTO> taxDTOs = new ArrayList<>();
        List<Tax> taxList = taxRepository.findByIsActive(true);
        for (Tax tax : taxList) {
            taxDTOs.add(taxToTaxDto(tax));
        }
        return taxDTOs;
    }

    @Override
    public TaxDTO removeTax(String taxId) {
        Tax tax = taxRepository.findById(taxId).orElse(null);
        if (tax == null) {
            throw new ObjectNotFoundException(String.format("Tax id is not found for the id %s ", taxId));
        }
        tax.setActive(false);
        taxRepository.delete(tax);
        TaxDTO taxDTO = taxToTaxDto(tax);
        return taxDTO;
    }

    @Override
    public void removeTaxByHsnCode(String hsnCode) throws ObjectNotFoundException {
        Tax tax = taxRepository.findByHsnCode(hsnCode);
        if (tax == null) {
            throw new ObjectNotFoundException(String.format("Tax date is not found for given hsn code %s ", hsnCode));
        }
        tax.setActive(false);
        taxRepository.delete(tax);

    }

    @Override
    public TaxDTO getTaxByHsnCode(String hsnCode) {
        Tax tax = taxRepository.findByHsnCode(hsnCode);
        if (tax == null) {
            throw new ObjectNotFoundException(String.format("Tax date is not found for given hsn code %s ", hsnCode));
        }
        return taxToTaxDto(tax);
    }

    private Tax taxDtoToTax(TaxDTO taxDTO, Tax tax) {
        if (tax == null) {
            tax = new Tax();
        }
        tax.setChapter(taxDTO.getChapter());
        tax.setTaxPercentage(taxDTO.getTaxPercentage());
        tax.setProductDetails(taxDTO.getProductDetails());
        tax.setHsnCode(taxDTO.getHsnCode());
        tax.setId(taxDTO.getId());
        return tax;
    }

    private TaxDTO taxToTaxDto(Tax tax) {
        TaxDTO taxDto = new TaxDTO();
        taxDto.setChapter(tax.getChapter());
        taxDto.setTaxPercentage(tax.getTaxPercentage());
        taxDto.setProductDetails(tax.getProductDetails());
        taxDto.setHsnCode(tax.getHsnCode());
        taxDto.setId(tax.getId());
        taxDto.setActive(tax.getActive());
        taxDto.setCreatedAt(tax.getCreatedAt());
        taxDto.setUpdatedAt(tax.getUpdatedAt());
        return taxDto;
    }

    private void prepareTaxSearchQuery(TaxFilterRequest filterRequest, GenericSpecificationsBuilder<Tax> builder) {
        builder.with(taxSpecificationFactory.isEqual("deleted", false));

        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getHsnCode())) {
            builder.with(taxSpecificationFactory.like("hsnCode", filterRequest.getHsnCode()));
        }
        if (filterRequest.getPercentage() != null) {
            builder.with(taxSpecificationFactory.isEqual("taxPercentage", filterRequest.getPercentage()));
        }
        if (filterRequest.getStatus() != null) {
            builder.with(taxSpecificationFactory.isEqual("isActive", filterRequest.getStatus()));
        }
    }
}
