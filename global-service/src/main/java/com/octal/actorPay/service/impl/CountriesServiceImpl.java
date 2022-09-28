package com.octal.actorPay.service.impl;

import com.octal.actorPay.dto.CountriesDTO;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.entities.Countries;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.repositories.CountriesRepository;
import com.octal.actorPay.service.CountriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CountriesServiceImpl implements CountriesService {


    @Autowired
    private CountriesRepository countriesRepository;


    @Override
    public Boolean delete(@NotBlank String id) {
        return null;
    }

    @Override
    public CountriesDTO read(String id, String actor) {
        Optional<Countries> country = countriesRepository.findById(id);
        if (country.isPresent()) {
            return mapCountryObjectToDto(country.get());
        } else {
            throw new ObjectNotFoundException("Country details not found for given id: " + id);
        }
    }

    @Override
    public List<CountriesDTO> list(String actor) {
        return countriesRepository.findAll().stream().map(this::mapCountryObjectToDto).collect(Collectors.toList());
    }

    @Override
    public @NotBlank String create(@NotNull @Valid CountriesDTO countriesDTO, String actor) {
        return null;
    }

    @Override
    public void update(@NotBlank String id, @NotNull @Valid CountriesDTO entity) {

    }

    private CountriesDTO mapCountryObjectToDto(Countries countries) {
        CountriesDTO dto = new CountriesDTO();
        dto.setActive(countries.isActive());
        dto.setDefault(countries.isDefault());
        dto.setCountry(countries.getCountry());
        dto.setCountryCode(countries.getCountryCode());
        dto.setCountryFlag(countries.getCountryFlag());
        dto.setId(countries.getId());
        dto.setCode(countries.getCode());
        return dto;
    }
}
