package com.octal.actorPay.service;

import com.octal.actorPay.dto.CountriesDTO;
import com.octal.actorPay.entities.Countries;
import org.springframework.stereotype.Service;

@Service
public interface CountriesService extends CrudOperation<Countries, CountriesDTO> {

}
