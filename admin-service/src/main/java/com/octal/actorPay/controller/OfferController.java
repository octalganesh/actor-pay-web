package com.octal.actorPay.controller;

import com.octal.actorPay.constants.OfferVisibility;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.OfferDTO;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.request.OfferFilterRequest;
import com.octal.actorPay.entities.Offer;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.listener.events.AttacheOfferEvent;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.service.OfferService;
import javassist.tools.rmi.ObjectNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/offers")
public class OfferController extends PagedItemsController {

    private OfferService offerService;

    private UserRepository userRepository;


    private ApplicationEventPublisher eventPublisher;

    public OfferController(OfferService offerService, UserRepository userRepository, ApplicationEventPublisher eventPublisher) {
        this.offerService = offerService;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Secured("ROLE_OFFER_ADD")
    @PostMapping
    public ResponseEntity<ApiResponse> addOffer(@Valid @RequestBody OfferDTO offerDTO, HttpServletRequest request) {
        User user = getUser(request);

        OfferDTO newOfferDto = offerService.save(offerDTO, user);
        eventPublisher.publishEvent(new AttacheOfferEvent(newOfferDto, Locale.ENGLISH));
        return new ResponseEntity<>(new ApiResponse(String.format("Offer Created successfully Offer Id : %s ",
                newOfferDto.getOfferId()), newOfferDto, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_OFFER_UPDATE")
    @PutMapping("/{offerId}")
    public ResponseEntity<ApiResponse> updateOffer(@Valid @RequestBody OfferDTO offerDTO, HttpServletRequest request) throws ObjectNotFoundException {
        User user = getUser(request);
        if (user == null) {
            throw new ObjectNotFoundException("Invalid user to access the offer");
        }
        offerService.save(offerDTO, user);
        return new ResponseEntity<>(new ApiResponse(String.format("Offer Updated successfully Offer Id : %s ", ""), offerDTO,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_OFFER_VIEW_LIST")
    @PostMapping("/list/paged")
    public ResponseEntity<ApiResponse> getAllOffer(@RequestParam(defaultValue = "0") Integer pageNo,
                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                   @RequestParam(defaultValue = "createdAt") String sortBy,
                                                   @RequestParam(defaultValue = "false") boolean asc, HttpServletRequest request,
                                                   @RequestBody OfferFilterRequest filterRequest) {
        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        User user = getUser(request);
        PageItem<OfferDTO> pageResult = offerService.getAllOffer(pagedInfo, user, filterRequest);
//        PageItem<OfferDTO> offerDTOList = orderService.getAllOrdersByOrderStatus(pagedInfo, user, status);
        return new ResponseEntity<>(new ApiResponse("Offer List ", pageResult,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_OFFER_AVAILABLE_VIEW_LIST")
    @PostMapping("/available")
    public ResponseEntity<ApiResponse> getAvailableOffers(@RequestParam(defaultValue = "0") Integer pageNo,
                                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                                          @RequestParam(defaultValue = "createdAt") String sortBy,
                                                          @RequestParam(defaultValue = "true") boolean asc,
                                                          @RequestBody OfferFilterRequest filterRequest) {
        try{
            final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
            PageItem<OfferDTO> pageResult = offerService.getAvailableOffers(pagedInfo, true, OfferVisibility.CATEGORY_LEVEL.name(), filterRequest);
            return new ResponseEntity<>(new ApiResponse("Offer List ", pageResult,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new ApiResponse("Offer List is not available ", null,
                    String.valueOf(101), HttpStatus.OK), HttpStatus.OK);
        }

    }

    @Secured("ROLE_OFFER_VIEW_BY_ID")
    @GetMapping("/id/{offerId}")
    public ResponseEntity<ApiResponse> getAllOfferById(@PathVariable("offerId") String offerId, HttpServletRequest request) {
        User user = getUser(request);
        OfferDTO offerDTO = offerService.getOfferById(offerId, user);
        return new ResponseEntity<>(new ApiResponse("Offer Details", offerDTO,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/code/{promoCode}")
    public ResponseEntity<ApiResponse> getAllOfferByPromoCode(@PathVariable("promoCode") String promoCode, HttpServletRequest request) {
        OfferDTO offerDTO = offerService.getOfferByPromoCode(promoCode);
        return new ResponseEntity<>(new ApiResponse("Offer Details", offerDTO,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/title")
    public ResponseEntity<ApiResponse> getOfferByTitle(@RequestParam("offerTitle") String offerTitle, HttpServletRequest request) {
        User user = getUser(request);
        OfferDTO offerDTO = offerService.getOfferByTitle(offerTitle, user);
        return new ResponseEntity<>(new ApiResponse("Offer Details", offerDTO,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

//    @PutMapping("/change/status")
//    public ResponseEntity<ApiResponse> activateOrDeactivateOffer(
//            @RequestParam("offerIds") List<String> offerIds,
//            @RequestParam("activate") boolean isActivate,
//            HttpServletRequest request) {
//        User user = getUser(request);
//        offerService.activateOrDeactivateOffer(offerIds,isActivate,user);
//        String message = isActivate? "Offer(s) Activated Successfully"  : "Offer(s) Deactivated Successfully";
//        return new ResponseEntity<>(new ApiResponse(message, "",
//                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
//    }

    @Secured("ROLE_OFFER_UPDATE_STATUS")
    @PutMapping("/change/status")
    public ResponseEntity<ApiResponse> activateOrDeactivateOffer(
            @RequestParam("offerIds") List<String> offerIds,
            @RequestParam("activate") boolean isActivate,
            HttpServletRequest request) {
        User user = getUser(request);
        List<String> validProducts = new ArrayList<>();
        Map<String, List<String>> listMap = offerService.activateOrDeactivateOffer(offerIds, isActivate,user);
        List<String> availableOffers= listMap.get("availableOffers");
        List<String> notAvailableOffers = listMap.get("notAvailableOffers");
        if (!availableOffers.isEmpty() && !notAvailableOffers.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse("Offers(s) are updated successfully " + availableOffers
                    , "Following Offer Id(s) are Invalid " + notAvailableOffers,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            if (!availableOffers.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse("Offers(s) are updated successfully " + availableOffers
                        , "", String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
            }
            if (!notAvailableOffers.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse("Following Offer Id(s) are Invalid " + notAvailableOffers
                        , "", String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
            }
        }
        return null;
    }

    @Secured("ROLE_OFFER_DELETE")
    @DeleteMapping("/delete/{offerId}")
    public ResponseEntity<ApiResponse> deleteOfferById(@PathVariable("offerId") String offerId, HttpServletRequest request) {

        User user = getUser(request);
        offerService.deleteOfferById(offerId, user);
        return new ResponseEntity<>(new ApiResponse("Offer Deleted Successfully : %s ", "",
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    private User getUser(HttpServletRequest request) {
        String userName = request.getHeader("userName");
        User user = userRepository.findByEmailAndIsActiveTrueAndDeletedFalse(userName).orElse(null);
        return user;
    }
}
