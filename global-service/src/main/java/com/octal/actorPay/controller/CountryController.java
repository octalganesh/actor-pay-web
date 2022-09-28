package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.service.CountriesService;
import com.octal.actorPay.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/v1/country")
public class CountryController extends PagedItemsController {

    @Autowired
    private CountriesService countriesService;

    @GetMapping(value = "/get/by/{id}")
    public ResponseEntity<ApiResponse> getCountryDetails(@PathVariable("id") String id, HttpServletRequest request) {
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Country details", countriesService.read(id, request.getHeader("userName")), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/get/all")
    public ResponseEntity<ApiResponse> getAllCountries(HttpServletRequest request) {
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Country list", countriesService.list(request.getHeader("userName")), HttpStatus.OK), HttpStatus.OK);
    }




}
