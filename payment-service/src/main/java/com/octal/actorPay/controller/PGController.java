package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.PgDetailsDTO;
import com.octal.actorPay.dto.payments.*;
import com.octal.actorPay.service.PGService;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class PGController {

    @Autowired
    PGService pgService;

    @PostMapping("/v1/pg/order/create")
    public ResponseEntity<ApiResponse> createOrder(@RequestBody OrderRequest orderRequest) throws Exception {
        OrderResponse orderResponse = pgService.createOrder(orderRequest);
        return new ResponseEntity<>(new ApiResponse("Order Details from Payment Gateway", orderResponse,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/v1/pg/order/verify")
    public ResponseEntity<ApiResponse> createOrder(@RequestBody PaymentGatewayResponse paymentGatewayResponse) throws Exception {
        PgSignatureRequest request = new PgSignatureRequest();
        request.setOrderId(paymentGatewayResponse.getRazorpayOrderId());
        request.setSignature(paymentGatewayResponse.getRazorpaySignature());
        request.setPaymentId(paymentGatewayResponse.getRazorpayPaymentId());
        boolean isVerified = pgService.verifySignature(request);
        return new ResponseEntity<>(new ApiResponse("Payment Verification Details", isVerified,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/v1/pg/order/pgdetails")
    public ResponseEntity<ApiResponse> savePgDetails(@RequestBody PgDetailsDTO pgDetailsDTO) {
        pgService.savePGDetails(pgDetailsDTO);
        return new ResponseEntity<>(new ApiResponse("Payment Gateway Details are saved", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/v1/pg/qrcode/create")
    public ResponseEntity<ApiResponse> createQrCode(@RequestBody QrCodeCreateRequest request) throws Exception {
        QRCodeResponse qrCode = pgService.createQRCode(request);
        return new ResponseEntity<>(new ApiResponse("QR Code Created Successfully", qrCode,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/v1/pg/refund")
    public ResponseEntity<ApiResponse> doRefund(@RequestBody RefundRequest refundRequest) throws Exception {
        RefundResponse refundResponse = pgService.doRefund(refundRequest);
        return new ResponseEntity<ApiResponse>(new ApiResponse("Refund Details", refundResponse,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/v1/pg/payment/{paymentTypeId}")
    public ResponseEntity<ApiResponse> getPGPaymentDetails(@PathVariable("paymentTypeId") String paymentTypeId) {
        PgDetailsDTO pgDetails = pgService.getPGPaymentDetails(paymentTypeId);
        return new ResponseEntity<ApiResponse>(new ApiResponse("PG Payment Details", pgDetails,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/v1/pg/contacts/create")
    public ResponseEntity<ApiResponse> createContact(@RequestBody ContactRequest contactRequest) throws Exception {
        ContactResponse contactResponse = pgService.createContact(contactRequest);
        return new ResponseEntity<ApiResponse>(new ApiResponse("Contact Details", contactResponse,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/v1/pg/fundaccount/create")
    public ResponseEntity<ApiResponse> createFundAccount(@Valid @RequestBody BankAccountRequest addBankRequest) throws Exception {
        BankAccountResponse addBankResponse = pgService.addBankAccount(addBankRequest);
        return new ResponseEntity<ApiResponse>(new ApiResponse("Fund Account Details", addBankResponse,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/v1/pg/fundaccount/{userId}")
    public ResponseEntity<ApiResponse> getUsersFundAccounts(@PathVariable(name = "userId") String userId) throws Exception {
        List<BankAccountResponse> bankAccountResponses = pgService.getUsersFundAccount(userId);
        if (bankAccountResponses != null || !bankAccountResponses.isEmpty())
            return new ResponseEntity<ApiResponse>(new ApiResponse("Fund Account Details", bankAccountResponses,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        else
            return new ResponseEntity<ApiResponse>(new ApiResponse("Fund Account Not Added", bankAccountResponses,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/v1/pg/fundaccount/self/{userId}")
    public ResponseEntity<ApiResponse> getUsersSelfFundAccounts(@PathVariable(name = "userId") String userId) throws Exception {
        List<BankAccountResponse> bankAccountResponses = pgService.getUsersSelfFundAccount(userId);
        if (bankAccountResponses != null || !bankAccountResponses.isEmpty())
            return new ResponseEntity<ApiResponse>(new ApiResponse("Fund Account Details", bankAccountResponses,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        else
            return new ResponseEntity<ApiResponse>(new ApiResponse("Fund Account Not Added", bankAccountResponses,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/v1/pg/fundaccount/beneficiary/{userId}")
    public ResponseEntity<ApiResponse> getUsersBeneficiaryAccounts(@PathVariable(name = "userId") String userId) throws Exception {
        List<BankAccountResponse> bankAccountResponses = pgService.getUsersBeneficiaryAccounts(userId);
        if (bankAccountResponses != null || !bankAccountResponses.isEmpty())
            return new ResponseEntity<ApiResponse>(new ApiResponse("Fund Account Details", bankAccountResponses,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        else
            return new ResponseEntity<ApiResponse>(new ApiResponse("Fund Account Not Added", bankAccountResponses,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/v1/pg/fundaccount/{userId}/{fundAccountId}")
    public ResponseEntity<ApiResponse> getUserFundAccountByFundAccountId(@PathVariable(name = "userId") String userId,
                                                                   @PathVariable(name = "fundAccountId") String fundAccountId) throws Exception {
        BankAccountResponse bankAccountResponse = pgService.getUserFundAccountByFundAccountId(userId, fundAccountId);
        if (bankAccountResponse != null)
            return new ResponseEntity<ApiResponse>(new ApiResponse("Fund Account Details", bankAccountResponse,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        else
            return new ResponseEntity<ApiResponse>(new ApiResponse("Fund Account Not Added", bankAccountResponse,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping("/v1/pg/fundaccount/activeordeactive")
    public ResponseEntity<ApiResponse> activeOrDeActiveAccount(@RequestBody DeactivateRequest deactivateRequest) throws RazorpayException {
        DeactivateResponse deactivateResponse = pgService.activeOrDeActiveAccount(deactivateRequest);
        return new ResponseEntity<ApiResponse>(new ApiResponse("Fund Account Details", deactivateResponse,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping("/v1/pg/fundaccount/update")
    public ResponseEntity<ApiResponse> setPrimaryOrSecondaryAccount(@RequestBody UpdateFundAccountRequest request) throws RazorpayException {
        pgService.setPrimaryOrSecondaryAccount(request);
        return new ResponseEntity<ApiResponse>(new ApiResponse("Your account updated successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping("/v1/pg/fundaccount/update-self")
    public ResponseEntity<ApiResponse> setSelfAccount(@RequestBody UpdateFundAccountRequest request) throws Exception {
        pgService.setSelfAccount(request);
        return new ResponseEntity<ApiResponse>(new ApiResponse("Your account updated successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/v1/pg/payout/create")
    public ResponseEntity<ApiResponse> createPayout(@RequestBody PayoutRequest payoutRequest) throws RazorpayException {
        PayoutResponse payoutResponse = pgService.createPayout(payoutRequest);
        return new ResponseEntity<ApiResponse>(new ApiResponse("Create Payout Details", payoutResponse,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }
}
