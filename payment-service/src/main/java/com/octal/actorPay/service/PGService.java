package com.octal.actorPay.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.constants.PGConstant;
import com.octal.actorPay.dto.PgDetailsDTO;
import com.octal.actorPay.dto.payments.BankAccountRequest;
import com.octal.actorPay.dto.payments.BankAccountResponse;
import com.octal.actorPay.dto.payments.ContactRequest;
import com.octal.actorPay.dto.payments.ContactResponse;
import com.octal.actorPay.dto.payments.DeactivatePgRequest;
import com.octal.actorPay.dto.payments.DeactivateRequest;
import com.octal.actorPay.dto.payments.DeactivateResponse;
import com.octal.actorPay.dto.payments.OrderRequest;
import com.octal.actorPay.dto.payments.OrderResponse;
import com.octal.actorPay.dto.payments.PayoutRequest;
import com.octal.actorPay.dto.payments.PayoutResponse;
import com.octal.actorPay.dto.payments.PgSignatureRequest;
import com.octal.actorPay.dto.payments.QRCodeResponse;
import com.octal.actorPay.dto.payments.QrCodeCreateRequest;
import com.octal.actorPay.dto.payments.QrCodeCreditRequest;
import com.octal.actorPay.dto.payments.RefundRequest;
import com.octal.actorPay.dto.payments.RefundResponse;
import com.octal.actorPay.dto.payments.UpdateFundAccountRequest;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.model.entities.PgDetails;
import com.octal.actorPay.model.entities.PgUserAccount;
import com.octal.actorPay.model.entities.QrCodeDetails;
import com.octal.actorPay.repositories.PgDetailsRepository;
import com.octal.actorPay.repositories.PgUserAccountRepository;
import com.octal.actorPay.repositories.QrCodeDetailsRepository;
import com.razorpay.FundAccount;
import com.razorpay.Order;
import com.razorpay.QrCode;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;
import com.razorpay.Utils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PGService {

    @Value("${pg.razor.key}")
    private String pgKey;

    @Value("${pg.razor.secret}")
    private String pgSecret;

    @Value("${pg.razor.base-endpoint}")
    private String baseEndpoint;

    @Value("${pg.razor.api-deactivate}")
    private String deactivateAPI;

    @Value("${pg.razor.api-contact}")
    private String contactAPI;

    @Value("${pg.razor.api-payout}")
    private String payoutAPI;

    @Value("${pg.razor.queue-if-low-balance}")
    private String queueIfLowBalance;

    @Value("${pg.razor.pay-out-account-no}")
    private String razorpayXAccountNo;

    @Autowired
    private PgDetailsRepository pgDetailsRepository;

    @Autowired
    private PgConnectorService pgConnectorService;

    @Autowired
    private PgUserAccountRepository pgUserAccountRepository;

    @Autowired
    private QrCodeDetailsRepository qrCodeDetailsRepository;

    public OrderResponse createOrder(OrderRequest orderRequest) throws Exception {

        RazorpayClient razorpayClient = new RazorpayClient(pgKey, pgSecret);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PGConstant.AMOUNT, orderRequest.getAmount());
        jsonObject.put(PGConstant.CURRENCY, orderRequest.getCurrency());
        jsonObject.put(PGConstant.RECEIPT, orderRequest.getReceipt());
        Order order = razorpayClient.orders.create(jsonObject);
        if (order != null) {
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setAmount(order.get("amount").toString());
            orderResponse.setCurrency(order.get("currency"));
            orderResponse.setReceipt(order.get("receipt"));
            orderResponse.setId(order.get("id"));
            orderResponse.setStatus(order.get("status"));
            return orderResponse;
        } else {
            throw new ActorPayException("Error while creating order from Gateway");
        }
    }

    public boolean verifySignature(PgSignatureRequest request) throws Exception {
        System.out.println("@Verification method ##### Payment Id ####  " + request.getPaymentId());
        System.out.println("@Verification method ##### Order Id ####  " + request.getOrderId());
        System.out.println("@Verification method ##### Signature ####  " + request.getSignature());
//        RazorpayClient razorpayClient = new RazorpayClient(pgKey, pgSecret);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PGConstant.PAYMENT_ID, request.getPaymentId());
        jsonObject.put(PGConstant.ORDER_ID, request.getOrderId());
        jsonObject.put(PGConstant.SIGNATURE, request.getSignature());
        boolean sigRes = Utils.verifyPaymentSignature(jsonObject, pgSecret); // API SECRET
        return sigRes;
    }

    public void savePGDetails(PgDetailsDTO pgDetailsDTO) {
        PgDetails pgDetails = new PgDetails();
        pgDetails.setActive(Boolean.TRUE);
        pgDetails.setDeleted(Boolean.FALSE);
        pgDetails.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        pgDetails.setPaymentId(pgDetailsDTO.getPaymentId());
        pgDetails.setPaymentTypeId(pgDetailsDTO.getPaymentTypeId());
        pgDetails.setPgOrderId(pgDetailsDTO.getPgOrderId());
        pgDetails.setPaymentMethod(pgDetailsDTO.getPaymentMethod());
        pgDetails.setPgSignature(pgDetailsDTO.getPgSignature());
        pgDetailsRepository.save(pgDetails);

    }

    public QRCodeResponse createQRCode(QrCodeCreateRequest request) throws Exception {
        QRCodeResponse response = new QRCodeResponse();
        QrCodeDetails qrCodeDetails = qrCodeDetailsRepository.findByUserIdAndIfscAndAccountNumber(request.getUserId(),
                request.getIfscCode(), request.getAccountNumber());

        if (qrCodeDetails != null) {
            response.setId(qrCodeDetails.getQrCodeId());
            response.setEntity("qr_code");
            response.setCustomerId("");
            response.setFixedAmount(false);
            response.setImageURL(qrCodeDetails.getImageURL());
            response.setName(request.getName());
            response.setUsage("multiple_use");
            response.setStatus("active");
            response.setDescription(request.getDescription());
            response.setType("upi_qr");
            response.setIfscCode(request.getIfscCode());
            response.setAccountNumber(request.getAccountNumber());
            response.setAccountHolderName(request.getAccountHolderName());
            return response;
        }
        BankAccountResponse bankAccountResponse = null;
        if (!request.getAccountNumber().isEmpty()) {
            List<BankAccountResponse> bankAccountList = getUsersFundAccount(request.getUserId());
            if (!bankAccountList.isEmpty()) {
                bankAccountResponse = bankAccountList.stream().filter(v-> v.getAccountNumber().equalsIgnoreCase(request.getAccountNumber())
                        && v.getIfscCode().equalsIgnoreCase(request.getIfscCode())).findFirst().orElse(null);
            }
            if (bankAccountResponse == null) {
                throw new RuntimeException("No such account is related to user");
            }
        }

        RazorpayClient razorpayClient = new RazorpayClient(pgKey, pgSecret);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "upi_qr");
        jsonObject.put("name", request.getName());
        jsonObject.put("usage", "multiple_use");
        jsonObject.put("fixed_amount", false);
//        jsonObject.put("payment_amount", 300);
        jsonObject.put("description", request.getDescription());
        QrCode qrCode = razorpayClient.qrCode.create(jsonObject);
        System.out.println(qrCode);
        response.setId(qrCode.get("id"));
        response.setEntity(qrCode.get("entity"));
        response.setCustomerId(qrCode.get("customer_id").toString().equals("null") ? "" : qrCode.get("customer_id"));
        response.setFixedAmount(qrCode.get("fixed_amount"));
        response.setImageURL(qrCode.get("image_url"));
        response.setName(qrCode.get("name"));
        response.setNotes(new ObjectMapper().readValue(qrCode.get("notes").toString(), List.class));
        response.setUsage(qrCode.get("usage"));
        response.setStatus(qrCode.get("status"));
        response.setDescription(qrCode.get("description"));
        response.setType(qrCode.get("type"));
        response.setIfscCode(request.getIfscCode());
        response.setAccountNumber(request.getAccountNumber());
        response.setAccountHolderName(request.getAccountHolderName());
        saveQRCodeDetails(response, request, Objects.nonNull(bankAccountResponse) ? bankAccountResponse.getFundAccountId() : null);
        return response;

    }

    public void saveQRCodeDetails(QRCodeResponse response, QrCodeCreateRequest request, String fundAccountId) {
        QrCodeDetails qrCodeDetails = new QrCodeDetails();
        qrCodeDetails.setActive(Boolean.TRUE);
        qrCodeDetails.setDeleted(Boolean.FALSE);
        qrCodeDetails.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        qrCodeDetails.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        qrCodeDetails.setQrCodeId(response.getId());
        qrCodeDetails.setIfsc(request.getIfscCode());
        qrCodeDetails.setAccountHolderName(request.getAccountHolderName());
        qrCodeDetails.setAccountNumber(request.getAccountNumber());
        qrCodeDetails.setImageURL(response.getImageURL());
        qrCodeDetails.setUserId(request.getUserId());
        qrCodeDetails.setUpiId(Objects.nonNull(request.getUpiId()) ? request.getUpiId() : "");
        qrCodeDetails.setStatus(response.getStatus());
        qrCodeDetails.setFundAccountId(fundAccountId);
        qrCodeDetailsRepository.save(qrCodeDetails);
    }

    public RefundResponse doRefund(RefundRequest request) throws Exception {
        RazorpayClient razorpayClient = new RazorpayClient(pgKey, pgSecret);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PGConstant.AMOUNT, request.getRefundAmount() * 100);
        jsonObject.put(PGConstant.REFUND_PAYMENT_ID, request.getPaymentId());
        Refund refund = razorpayClient.payments.refund(jsonObject);
        Integer amount = refund.get(PGConstant.AMOUNT);
        String referenceId = refund.get("id");
        String currency = refund.get(PGConstant.CURRENCY);
        String paymentId = refund.get(PGConstant.REFUND_PAYMENT_ID);
        String status = refund.get(PGConstant.STATUS);
        RefundResponse refundResponse = new RefundResponse();
        refundResponse.setReferenceId(referenceId);
        refundResponse.setAmount(amount);
        refundResponse.setCurrency(currency);
        refundResponse.setPaymentId(paymentId);
        refundResponse.setStatus(status);
        return refundResponse;
    }

    public PgDetailsDTO getPGPaymentDetails(String paymentTypeId) {
        PgDetails pgDetails = pgDetailsRepository.findByPaymentTypeId(paymentTypeId)
                .orElseThrow(() -> new ActorPayException(String.format("Payment Id is not found %s ", paymentTypeId)));
        PgDetailsDTO pgDetailsDTO = new PgDetailsDTO();
        pgDetailsDTO.setPaymentId(pgDetails.getPaymentId());
        pgDetailsDTO.setPaymentTypeId(pgDetails.getPaymentTypeId());
        pgDetailsDTO.setActive(pgDetails.isActive());
        pgDetailsDTO.setPgSignature(pgDetails.getPgSignature());
        pgDetailsDTO.setPgOrderId(pgDetails.getPgOrderId());
        pgDetailsDTO.setCreatedAt(pgDetails.getCreatedAt());
        pgDetailsDTO.setPaymentMethod(pgDetails.getPaymentMethod());
        return pgDetailsDTO;
    }

    public ContactResponse createContact(ContactRequest contactRequest) throws Exception {
        pgConnectorService.loadPGCredential(baseEndpoint, contactAPI, pgKey, pgSecret);
        ContactResponse response = pgConnectorService.createContact(contactRequest);
        return response;
    }

    public BankAccountResponse addBankAccount(BankAccountRequest fundAccountRequest) throws Exception {
        BankAccountResponse bankAccountResponse = null;
        List<PgUserAccount> pgUserAccount = pgUserAccountRepository.findByUserId(fundAccountRequest.getUserId());
        if (pgUserAccount == null || pgUserAccount.isEmpty()) {
            validateDuplicateCheck(fundAccountRequest);
            bankAccountResponse = newContactAndFundAccount(fundAccountRequest);
        } else {
            String contactId = pgUserAccount.get(0).getPgContactId();
            validateDuplicateCheck(fundAccountRequest);
            fundAccountRequest.setContactId(contactId);
            bankAccountResponse = existingContactAndFundAccount(fundAccountRequest);
        }
        return bankAccountResponse;
    }

    private void validateDuplicateCheck(BankAccountRequest fundAccountRequest) {
        PgUserAccount pgAccount =
                pgUserAccountRepository.findByAccountNumberAndIfscCodeAndUserId(
                        fundAccountRequest.getAccountNumber(), fundAccountRequest.getIfscCode(), fundAccountRequest.getUserId());
        if (pgAccount != null) {
            throw new ActorPayException("The Fund Account you have entered is already available in the System");
        }
    }

    private BankAccountResponse newContactAndFundAccount(BankAccountRequest bankAccountRequest) throws Exception {
        ContactRequest contactRequest = new ContactRequest();
        contactRequest.setEmail(bankAccountRequest.getEmail());
        contactRequest.setName(bankAccountRequest.getAccountHolderName());
        ContactResponse contactResponse = createContact(contactRequest);
        if (StringUtils.isNotBlank(contactResponse.getContactId())) {
            bankAccountRequest.setContactId(contactResponse.getContactId());
            BankAccountResponse bankAccountResponse = createFundAccount(bankAccountRequest);
            return bankAccountResponse;
        } else {
            throw new ActorPayException("Unable to create Fund Account - Please contact support");
        }
    }

    private BankAccountResponse existingContactAndFundAccount(BankAccountRequest bankAccountRequest) throws Exception {
        BankAccountResponse bankAccountResponse = createFundAccount(bankAccountRequest);
        return bankAccountResponse;
    }

    private BankAccountResponse createFundAccount(BankAccountRequest fundAccountRequest) throws Exception {
        RazorpayClient razorpayClient = new RazorpayClient(pgKey, pgSecret);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("contact_id", fundAccountRequest.getContactId());
        jsonObject.put("account_type", fundAccountRequest.getAccountType());
        Map<String, String> bankAccount = new HashMap<>();
        bankAccount.put("name", fundAccountRequest.getAccountHolderName());
        bankAccount.put("ifsc", fundAccountRequest.getIfscCode());
        bankAccount.put("account_number", fundAccountRequest.getAccountNumber());
        jsonObject.put("bank_account", bankAccount);
        System.out.println("##### FUND ACCOUNT REQUEST " + jsonObject.toString());
        FundAccount fundAccount = razorpayClient.fundAccount.create(jsonObject);
        System.out.println("Fund Account id created  " + fundAccount.get("id"));
        System.out.println("Contact id " + fundAccount.get("contact_id"));
        boolean isAcive = fundAccount.get("active");
        BankAccountResponse addBankResponse = new BankAccountResponse();
        addBankResponse.setContactId(fundAccount.get("contact_id"));
        addBankResponse.setFundAccountId(fundAccount.get("id"));
        addBankResponse.setActive(isAcive);
        addBankResponse.setSelf(fundAccountRequest.isSelf());
        addBankResponse.setPrimaryAccount(fundAccountRequest.isSelf() && fundAccountRequest.isPrimaryAccount());
        PgUserAccount pgUserAccount = new PgUserAccount();
        pgUserAccount.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        pgUserAccount.setUserId(fundAccountRequest.getUserId());
        pgUserAccount.setUserType(fundAccountRequest.getUserType());
        pgUserAccount.setPgContactId(addBankResponse.getContactId());
        pgUserAccount.setPgFundId(addBankResponse.getFundAccountId());
        pgUserAccount.setAccountNumber(fundAccountRequest.getAccountNumber());
        pgUserAccount.setIfscCode(fundAccountRequest.getIfscCode());
        pgUserAccount.setAccountHolderName(fundAccountRequest.getAccountHolderName());
        pgUserAccount.setActive(isAcive);
        pgUserAccount.setIsSelf(fundAccountRequest.isSelf());
        if (fundAccountRequest.isSelf() && fundAccountRequest.isPrimaryAccount()) {
            PgUserAccount primaryPgUserAccount = pgUserAccountRepository.
                    findByUserIdAndIsPrimaryAccountTrue(fundAccountRequest.getUserId());
            if (primaryPgUserAccount != null) {
                primaryPgUserAccount.setIsPrimaryAccount(false);
                pgUserAccountRepository.save(primaryPgUserAccount);
            }
        }
        pgUserAccount.setIsPrimaryAccount(fundAccountRequest.isSelf() && fundAccountRequest.isPrimaryAccount());
        pgUserAccountRepository.save(pgUserAccount);
        if (fundAccountRequest.isSelf()) {
            createQRCode(prepareQrCodeRequest(pgUserAccount));
        }
        return addBankResponse;
    }

    public List<BankAccountResponse> getUsersFundAccount(String userId) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(pgKey, pgSecret);
        List<PgUserAccount> pgUserAccounts = pgUserAccountRepository.findByUserId(userId);
        List<BankAccountResponse> bankAccountResponses = new ArrayList<>();
        for (PgUserAccount pgUserAccount : pgUserAccounts) {
            FundAccount fundAccount = razorpayClient.fundAccount.fetch(pgUserAccount.getPgFundId());
            BankAccountResponse bankAccountResponse = buildBankAccountResponse(fundAccount);
            bankAccountResponse.setCreatedAt(pgUserAccount.getCreatedAt());
            bankAccountResponses.add(bankAccountResponse);
        }
        bankAccountResponses.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));
        return bankAccountResponses;
    }

    public List<BankAccountResponse> getUsersSelfFundAccount(String userId) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(pgKey, pgSecret);
        List<PgUserAccount> pgUserAccounts = pgUserAccountRepository.findByUserIdAndIsSelfTrue(userId);
        List<String> fundAccountIds = pgUserAccounts.stream().map(PgUserAccount::getPgFundId).collect(Collectors.toList());
        List<QrCodeDetails> qrCodeDetailsList = qrCodeDetailsRepository.findByFundAccountIdIn(fundAccountIds);
        List<BankAccountResponse> bankAccountResponses = new ArrayList<>();
        for (PgUserAccount pgUserAccount : pgUserAccounts) {
            FundAccount fundAccount = razorpayClient.fundAccount.fetch(pgUserAccount.getPgFundId());
            BankAccountResponse bankAccountResponse = buildBankAccountResponse(fundAccount);
            QrCodeDetails qrCodeDetails = qrCodeDetailsList.stream().filter(v->v.getFundAccountId().equals(pgUserAccount.getPgFundId())).findAny().orElse(null);
            QRCodeResponse qrCodeResponse = new QRCodeResponse();
            if (qrCodeDetails != null) {
                qrCodeResponse.setId(qrCodeDetails.getQrCodeId());
                qrCodeResponse.setEntity("qr_code");
                qrCodeResponse.setCustomerId("");
                qrCodeResponse.setFixedAmount(false);
                qrCodeResponse.setImageURL(qrCodeDetails.getImageURL());
                qrCodeResponse.setName(qrCodeDetails.getAccountHolderName());
                qrCodeResponse.setUsage("multiple_use");
                qrCodeResponse.setStatus("active");
                qrCodeResponse.setDescription("QrCode");
                qrCodeResponse.setType("upi_qr");
                qrCodeResponse.setIfscCode(qrCodeDetails.getIfsc());
                qrCodeResponse.setAccountNumber(qrCodeDetails.getAccountNumber());
                qrCodeResponse.setAccountHolderName(qrCodeDetails.getAccountHolderName());
            }
            bankAccountResponse.setQrCodeResponse(qrCodeResponse);
            bankAccountResponse.setPrimaryAccount(pgUserAccount.getIsPrimaryAccount());
            bankAccountResponse.setSelf(pgUserAccount.getIsSelf());
            bankAccountResponse.setCreatedAt(pgUserAccount.getCreatedAt());
            bankAccountResponses.add(bankAccountResponse);
        }
        bankAccountResponses.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));
        return bankAccountResponses;
    }

    public List<BankAccountResponse> getUsersBeneficiaryAccounts(String userId) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(pgKey, pgSecret);
        List<PgUserAccount> pgUserAccounts = pgUserAccountRepository.findByUserIdAndIsSelfFalse(userId);
        List<BankAccountResponse> bankAccountResponses = new ArrayList<>();
        for (PgUserAccount pgUserAccount : pgUserAccounts) {
            FundAccount fundAccount = razorpayClient.fundAccount.fetch(pgUserAccount.getPgFundId());
            BankAccountResponse bankAccountResponse = buildBankAccountResponse(fundAccount);
            bankAccountResponse.setCreatedAt(pgUserAccount.getCreatedAt());
            bankAccountResponses.add(bankAccountResponse);
        }
        bankAccountResponses.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));
        return bankAccountResponses;
    }

    public DeactivateResponse activeOrDeActiveAccount(DeactivateRequest deactivateRequest) throws RazorpayException {
        PgUserAccount pgUserAccount = pgUserAccountRepository.
                findByUserIdAndPgFundId(deactivateRequest.getUserId(), deactivateRequest.getFundAccountId());
        pgConnectorService.loadPGCredential(baseEndpoint, deactivateAPI, pgKey, pgSecret);
        DeactivatePgRequest deactivatePgRequest = new DeactivatePgRequest();
        deactivatePgRequest.setActive(deactivateRequest.isActive());
        DeactivateResponse deactivateResponse = pgConnectorService.activeOrDeActiveAccount(deactivatePgRequest, deactivateRequest.getFundAccountId());
        pgUserAccount.setActive(deactivateResponse.isActive());
        pgUserAccountRepository.save(pgUserAccount);
        return deactivateResponse;
    }

    public void setPrimaryOrSecondaryAccount(UpdateFundAccountRequest request) throws RazorpayException {
        if (request.isPrimaryAccount()) {
            PgUserAccount primaryPgUserAccount = pgUserAccountRepository.
                    findByUserIdAndIsPrimaryAccountTrue(request.getUserId());

            if (primaryPgUserAccount != null) {
                if (primaryPgUserAccount.getPgFundId().equals(request.getFundAccountId())) {
                    throw new ActorPayException("This Account is already primary");
                }
                primaryPgUserAccount.setIsPrimaryAccount(false);
                pgUserAccountRepository.save(primaryPgUserAccount);
            }
        }
        PgUserAccount pgUserAccount = pgUserAccountRepository.
                findByUserIdAndPgFundId(request.getUserId(), request.getFundAccountId());
        pgUserAccount.setIsPrimaryAccount(request.isPrimaryAccount());
        pgUserAccountRepository.save(pgUserAccount);
    }

    public void setSelfAccount(UpdateFundAccountRequest request) throws Exception {

        if (request.isPrimaryAccount()) {
            PgUserAccount primaryPgUserAccount = pgUserAccountRepository.
                    findByUserIdAndIsPrimaryAccountTrue(request.getUserId());

            if (primaryPgUserAccount != null) {
                primaryPgUserAccount.setIsPrimaryAccount(false);
                pgUserAccountRepository.save(primaryPgUserAccount);
            }
        }

        PgUserAccount pgUserAccount = pgUserAccountRepository.
                findByUserIdAndPgFundId(request.getUserId(), request.getFundAccountId());
        pgUserAccount.setIsSelf(request.isSelf());
        pgUserAccount.setIsPrimaryAccount(request.isPrimaryAccount());
        pgUserAccountRepository.save(pgUserAccount);
        createQRCode( prepareQrCodeRequest(pgUserAccount));
    }

    private QrCodeCreateRequest prepareQrCodeRequest(PgUserAccount request) {
        QrCodeCreateRequest qrCodeCreateRequest = new QrCodeCreateRequest();
        qrCodeCreateRequest.setUserId(request.getUserId());
        qrCodeCreateRequest.setIfscCode(request.getIfscCode());
        qrCodeCreateRequest.setAccountHolderName(request.getAccountHolderName());
        qrCodeCreateRequest.setAccountNumber(request.getAccountNumber());
        qrCodeCreateRequest.setDescription("QrCode");
        qrCodeCreateRequest.setName(request.getAccountHolderName());
        return qrCodeCreateRequest;
    }

    public BankAccountResponse getUserFundAccountByFundAccountId(String userId, String fundAccountId) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(pgKey, pgSecret);
        PgUserAccount pgUserAccount = pgUserAccountRepository.findByUserIdAndPgFundId(userId, fundAccountId);
        FundAccount fundAccount = razorpayClient.fundAccount.fetch(pgUserAccount.getPgFundId());
        BankAccountResponse bankAccountResponse = buildBankAccountResponse(fundAccount);
        return bankAccountResponse;
    }

    private BankAccountResponse buildBankAccountResponse(FundAccount fundAccount) {
        BankAccountResponse bankAccountResponse = new BankAccountResponse();
        String id = fundAccount.get("id");
        String contactId = fundAccount.get("contact_id");
        String accountType = fundAccount.get("account_type");
        JSONObject jsonObject = fundAccount.get("bank_account");
        String ifscCode = (String) jsonObject.get("ifsc");
        String bankName = (String) jsonObject.get("bank_name");
        String name = (String) jsonObject.get("name");
        String accountNumber = (String) jsonObject.get("account_number");
        boolean active = fundAccount.get("active");
        bankAccountResponse.setActive(active);
        bankAccountResponse.setFundAccountId(id);
        bankAccountResponse.setContactId(contactId);
        bankAccountResponse.setAccountNumber(accountNumber);
        bankAccountResponse.setName(name);
        bankAccountResponse.setIfscCode(ifscCode);
        bankAccountResponse.setBankName(bankName);
        bankAccountResponse.setAccountType(accountType);
        return bankAccountResponse;
    }

    public PayoutResponse createPayout(PayoutRequest payoutRequest) throws RazorpayException {
        pgConnectorService.loadPGCredential(baseEndpoint, payoutAPI, pgKey, pgSecret);
        payoutRequest.setAccountNumber(razorpayXAccountNo);
        payoutRequest.setQueueIfLowBalance(Boolean.valueOf(queueIfLowBalance));
 //       PayoutResponse response = pgConnectorService.createPayout(payoutRequest);
 //       response.setAmount(response.getAmount());
        return  pgConnectorService.createPayout(payoutRequest);
    }
}