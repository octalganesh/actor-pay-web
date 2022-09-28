package com.octal.actorPay.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.PGConstant;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.enums.NotificationTypeEnum;
import com.octal.actorPay.feign.clients.MerchantClient;
import com.octal.actorPay.listners.events.BankTransferMerchantEvent;
import org.springframework.context.ApplicationEventPublisher;
import com.octal.actorPay.dto.payments.BankAccountResponse;
import com.octal.actorPay.dto.payments.BankTransferRequest;
import com.octal.actorPay.dto.payments.OrderRequest;
import com.octal.actorPay.dto.payments.OrderResponse;
import com.octal.actorPay.dto.payments.PaymentGatewayResponse;
import com.octal.actorPay.dto.payments.PayoutRequest;
import com.octal.actorPay.dto.payments.PayoutResponse;
import com.octal.actorPay.dto.payments.RazorpayPayoutRequest;
import com.octal.actorPay.dto.payments.RefundRequest;
import com.octal.actorPay.dto.payments.RefundResponse;
import com.octal.actorPay.dto.payments.WalletRequest;
import com.octal.actorPay.dto.request.BankTransactionFilterRequest;
import com.octal.actorPay.entities.BankTransactions;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.listners.events.BankTransferEvent;
import com.octal.actorPay.repositories.BankTransactionsRepository;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.service.*;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import com.octal.actorPay.utils.UserFeignHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserBankServiceImpl implements UserBankService {

    @Autowired
    private UserPGService userPGService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private MerchantClient merchantClient;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankTransactionsRepository bankTransactionsRepository;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserFeignHelper userFeignHelper;

    @Autowired
    private SpecificationFactory<BankTransactions> specificationFactory;


    @Override
    public ApiResponse transferToAnotherBank(RazorpayPayoutRequest request, boolean isWallet) throws Exception {
        ApiResponse response = userPGService.getUserFundAccounts(request.getUserId());
        BankTransactions bankTransactions = bankTransactionsRepository.findByOrderId(request.getGatewayResponse().getRazorpayOrderId());
        BankAccountResponse bankAccountResponse = null;
        if (response.getData() != null && bankTransactions != null) {
            List<BankAccountResponse> responseList = objectMapper.convertValue(response.getData(),
                    new TypeReference<List<BankAccountResponse>>() {
                    });
            if (!responseList.isEmpty()) {
                BankTransactions finalBankTransactions = bankTransactions;
                bankAccountResponse = responseList.stream().filter(v -> v.getAccountNumber().equalsIgnoreCase(finalBankTransactions.getAccountNumber())
                        && v.getIfscCode().equalsIgnoreCase(finalBankTransactions.getIfsc())).findFirst().orElse(null);
            }
        }

        if (bankAccountResponse != null) {
            PayoutRequest payoutRequest = new PayoutRequest();
            payoutRequest.setAmount((int) request.getAmount());
            payoutRequest.setCurrency(PGConstant.IND_CURRENCY);
            payoutRequest.setMode("IMPS");
            payoutRequest.setPurpose("payout");
            payoutRequest.setNarration(bankTransactions.getReason());
            payoutRequest.setReferenceId(bankTransactions.getId());
            payoutRequest.setFundAccountId(bankAccountResponse.getFundAccountId());
            try {
                ApiResponse payoutAPIResponse = userPGService.createPayout(payoutRequest);

                if (payoutAPIResponse.getData() != null) {
                    PayoutResponse payoutResponse = objectMapper.convertValue(payoutAPIResponse.getData(), PayoutResponse.class);
                    payoutResponse.setWalletTransfer(isWallet);
                    bankTransactions.setStatus("PAYOUT_SUCCEED");
                    bankTransactions.setPayoutId(payoutResponse.getPayoutId());
                    bankTransactions.setPayoutResponse(payoutResponse);
                    bankTransactions.setUpdatedAt(LocalDateTime.now());
                    bankTransactionsRepository.save(bankTransactions);
                    try {
                        Optional<User> user = userRepository.findById(request.getUserId());
                        if (user.isPresent()) {
                            FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
                            fcmRequest.setNotificationType(NotificationTypeEnum.WITHDRAW);
                            fcmRequest.setNotificationTypeId(bankTransactions.getOrderId());
                            WalletTransactionResponse walletTransactionResponse = new WalletTransactionResponse();
                            walletTransactionResponse.setCustomerName(user.get().getFirstName());
                            walletTransactionResponse.setTransferredAmount(Double.valueOf(payoutRequest.getAmount()));
                            walletTransactionResponse.setModeOfTransaction("Transfer");
                            walletTransactionResponse.setParentTransactionId(bankTransactions.getOrderId());
                            applicationEventPublisher.publishEvent(new BankTransferEvent(user.get(), walletTransactionResponse, fcmRequest));
                        }else{
                            MerchantDTO merchantDTO = userFeignHelper.getMerchantByUserId(request.getUserId());
                            if(merchantDTO != null){
                                FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
                                fcmRequest.setNotificationType(NotificationTypeEnum.WITHDRAW);
                                fcmRequest.setNotificationTypeId(bankTransactions.getOrderId());
                                WalletTransactionResponse walletTransactionResponse = new WalletTransactionResponse();
                                walletTransactionResponse.setCustomerName(merchantDTO.getFirstName());
                                walletTransactionResponse.setTransferredAmount(Double.valueOf(payoutRequest.getAmount()));
                                walletTransactionResponse.setModeOfTransaction("Transfer");
                                walletTransactionResponse.setParentTransactionId(bankTransactions.getOrderId());
                                applicationEventPublisher.publishEvent(new BankTransferMerchantEvent(merchantDTO, walletTransactionResponse, fcmRequest));
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error while sending notification: " + e.getMessage());
                    } finally {
                        return new ApiResponse("Create Payout Details", payoutResponse,
                                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK);
                    }
                }
                return payoutAPIResponse;
            } catch (Exception e) {
                if (isWallet) {
                    bankTransactions.setStatus("PAYOUT_REFUNDED");
                    ApiResponse refundWalletResponse = userWalletService.addMoneyToWallet(prepareWalletRefundRequest(request));
                    WalletTransactionResponse walletRefundResponse = objectMapper.convertValue(refundWalletResponse.getData(), WalletTransactionResponse.class);
                    bankTransactions.setRefundWalletTransId(walletRefundResponse.getParentTransactionId());
                } else {
                    RefundResponse refundResponse = processRefund(bankTransactions.getAmount(), request.getGatewayResponse().getRazorpayPaymentId());
                    bankTransactions.setStatus("PAYOUT_REFUNDED");
                    bankTransactions.setRefundResponse(refundResponse);
                }
                bankTransactions.setUpdatedAt(LocalDateTime.now());
                bankTransactionsRepository.save(bankTransactions);
                throw new RuntimeException("There is some issue in payout, your refund is initiated ");
            }
        } else {
            throw new RuntimeException("No Fund account available with the given IFSC code and account number");
        }
    }

    @Override
    public ApiResponse transferCheckOut(BankTransferRequest bankTransferRequest) throws Exception {
        ApiResponse apiResponse;
        List<String> keys = new ArrayList<>();
        keys.add(CommonConstant.BANK_TRANSACTION_MIN_AMOUNT);
        keys.add(CommonConstant.BANK_TRANSACTION_MAX_AMOUNT);
        keys.add(CommonConstant.BANK_TRANSACTION_LIMIT);
        keys.add(CommonConstant.BANK_TRANSACTION_AMOUNT_LIMIT);
        keys.add(CommonConstant.BANK_ACCOUNT_COOL_DOWN_PERIOD);
        List<SystemConfigurationDTO> systemConfigurationDTOS = userFeignHelper.getGlobalConfigByKeys(keys);

        if (systemConfigurationDTOS != null && !systemConfigurationDTOS.isEmpty()) {
            Map<String, SystemConfigurationDTO> systemConfigMap = systemConfigurationDTOS.stream()
                    .collect(Collectors.toMap(SystemConfigurationDTO::getParamName, Function.identity()));

            if (bankTransferRequest.getAmount() <
                    Integer.parseInt(systemConfigMap.get(CommonConstant.BANK_TRANSACTION_MIN_AMOUNT).getParamValue())) {
                throw new RuntimeException("Amount should be more than minimum amount");
            }

            if (bankTransferRequest.getAmount() >
                    Integer.parseInt(systemConfigMap.get(CommonConstant.BANK_TRANSACTION_MAX_AMOUNT).getParamValue())) {
                throw new RuntimeException("Amount should be less than maximum amount");
            }

            List<BankTransactions> bankTransactionsList = bankTransactionsRepository.findByCreatedAtBetweenAndUserId(
                    LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIDNIGHT), LocalDateTime.now(), bankTransferRequest.getUserId());

            if (bankTransactionsList.size() >=
                    Integer.parseInt(systemConfigMap.get(CommonConstant.BANK_TRANSACTION_LIMIT).getParamValue())) {
                throw new RuntimeException("Today's number of  transaction limit is completed");
            }

            double totalAmountInCurrentDay = bankTransactionsList.stream().mapToDouble(BankTransactions::getAmount).sum();
            if (totalAmountInCurrentDay > Double.parseDouble(systemConfigMap.get(CommonConstant.BANK_TRANSACTION_AMOUNT_LIMIT).getParamValue())) {
                throw new RuntimeException("Today's transaction limit is completed");
            }
            BankAccountResponse bankAccountResponse = null;
            ApiResponse response = userPGService.getUserFundAccounts(bankTransferRequest.getUserId());
            if (response.getData() != null) {
                List<BankAccountResponse> responseList = objectMapper.convertValue(response.getData(),
                        new TypeReference<List<BankAccountResponse>>() {
                        });
                if (!responseList.isEmpty()) {
                    bankAccountResponse = responseList.stream().filter(v -> v.getAccountNumber().equalsIgnoreCase(bankTransferRequest.getAccountNumber())
                            && v.getIfscCode().equalsIgnoreCase(bankTransferRequest.getIfscCode())).findFirst().orElse(null);

                    long difference = LocalDateTime.now().compareTo(bankAccountResponse.getCreatedAt()) * 24L;

                    if (difference < Integer.parseInt(systemConfigMap.get(CommonConstant.BANK_ACCOUNT_COOL_DOWN_PERIOD).getParamValue())) {
                        throw new RuntimeException("Cool down period is not completed yet");
                    }
                }
            }
        }


        if (bankTransferRequest.getAmount() <= 0) {
            throw new RuntimeException("Amount should be more than zero");
        }


        if (!bankTransferRequest.isWalletTransfer()) {
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setAmount(bankTransferRequest.getAmount());
            apiResponse = orderService.checkout(orderRequest);
        } else {
            WalletRequest withdrawRequest = new WalletRequest();
            withdrawRequest.setCurrentUserId(bankTransferRequest.getUserId());
            withdrawRequest.setAmount(bankTransferRequest.getAmount() / 100);
            withdrawRequest.setUserId(bankTransferRequest.getUserId());
            withdrawRequest.setUserType(bankTransferRequest.getUserType());
            apiResponse = userWalletService.withdraw(withdrawRequest);
        }
        if (apiResponse.getData() != null) {
            BankTransactions transactions = new BankTransactions();
            transactions.setAmount(bankTransferRequest.getAmount() / 100);
            transactions.setUserId(bankTransferRequest.getUserId());
            transactions.setUserType(bankTransferRequest.getUserType());
            transactions.setAccountHolderName(bankTransferRequest.getAccountHolderName());
            transactions.setAccountNumber(bankTransferRequest.getAccountNumber());
            transactions.setIfsc(bankTransferRequest.getIfscCode());
            transactions.setReason(bankTransferRequest.getNarration());
            transactions.setBankTransactionId(getBankTransactionCode());
            transactions.setCreatedAt(LocalDateTime.now());

            if (bankTransferRequest.isWalletTransfer()) {
                WalletTransactionResponse walletTransactionResponse =
                        objectMapper.convertValue(apiResponse.getData(), WalletTransactionResponse.class);
                transactions.setOrderId(walletTransactionResponse.getParentTransactionId());
                transactions.setTransactionType("WALLET_TO_BANK");
            } else {
                OrderResponse orderResponse = objectMapper.convertValue(apiResponse.getData(), OrderResponse.class);
                transactions.setOrderId(orderResponse.getId());
                transactions.setOrderReceipt(orderResponse.getReceipt());
                transactions.setStatus(orderResponse.getStatus());
                transactions.setTransactionType("BANK_TO_BANK");
            }
            bankTransactionsRepository.save(transactions);
            if (bankTransferRequest.isWalletTransfer()) {
                RazorpayPayoutRequest payoutRequest = new RazorpayPayoutRequest();
                payoutRequest.setUserId(bankTransferRequest.getUserId());
                payoutRequest.setEmail(bankTransferRequest.getEmail());
                payoutRequest.setContact(bankTransferRequest.getContact());
                payoutRequest.setName(bankTransferRequest.getName());
                payoutRequest.setAccountType("bank_account");
                payoutRequest.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
                payoutRequest.setAmount(bankTransferRequest.getAmount());
                PaymentGatewayResponse gatewayResponse = new PaymentGatewayResponse();
                gatewayResponse.setRazorpayOrderId(transactions.getOrderId());
                payoutRequest.setGatewayResponse(gatewayResponse);
                try {
                    Optional<User> user = userRepository.findById(bankTransferRequest.getUserId());
                    if (user.isPresent()) {
                        FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
                        fcmRequest.setNotificationType(NotificationTypeEnum.WITHDRAW);
                        fcmRequest.setNotificationTypeId(transactions.getOrderId());
                        WalletTransactionResponse walletTransactionResponse = new WalletTransactionResponse();
                        walletTransactionResponse.setCustomerName(user.get().getFirstName());
                        walletTransactionResponse.setTransferredAmount(transactions.getAmount());
                        walletTransactionResponse.setModeOfTransaction("Transfer");
                        walletTransactionResponse.setParentTransactionId(transactions.getOrderId());
                        applicationEventPublisher.publishEvent(new BankTransferEvent(user.get(), walletTransactionResponse, fcmRequest));
                    }else{
                        MerchantDTO merchantDTO = userFeignHelper.getMerchantByUserId(bankTransferRequest.getUserId());
                        if(merchantDTO != null){
                            FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
                            fcmRequest.setNotificationType(NotificationTypeEnum.WITHDRAW);
                            fcmRequest.setNotificationTypeId(transactions.getOrderId());
                            WalletTransactionResponse walletTransactionResponse = new WalletTransactionResponse();
                            walletTransactionResponse.setCustomerName(merchantDTO.getFirstName());
                            walletTransactionResponse.setTransferredAmount(transactions.getAmount());
                            walletTransactionResponse.setModeOfTransaction("Transfer");
                            walletTransactionResponse.setParentTransactionId(transactions.getOrderId());
                            applicationEventPublisher.publishEvent(new BankTransferMerchantEvent(merchantDTO, walletTransactionResponse, fcmRequest));
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error while sending notification: " + e.getMessage());
                } finally {
                    return transferToAnotherBank(payoutRequest, true);
                }
            }
            return apiResponse;
        } else {
            throw new RuntimeException(apiResponse.getMessage());
        }
    }

    @Override
    public PageItem<BankTransactionDTO> searchBankTransaction(Integer pageNo, Integer pageSize, String sortBy,
                                                              boolean asc, String userType, BankTransactionFilterRequest filterRequest) throws Exception {

        if (pageNo > 0) {
            pageNo = pageNo - 1;
        }
        PagedItemInfo pagedInfo = new PagedItemInfo(pageNo, pageSize, sortBy, asc, null);

        SystemConfigurationDTO systemConfigurationDTO = userFeignHelper.getGlobalConfigByKey(CommonConstant.ADMIN_ID);
        String adminId = systemConfigurationDTO.getParamValue().trim();
        GenericSpecificationsBuilder<BankTransactions> builder = new GenericSpecificationsBuilder<>();
        List<BankTransactionDTO> bankTransactionDTOList = new ArrayList<>();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(BankTransactions.class, pagedInfo);
        prepareBankSearchQuery(filterRequest, builder);
        Page<BankTransactions> pagedResult = bankTransactionsRepository.findAll(builder.build(), pageRequest);
        List<BankTransactions> bankTransactions = pagedResult.getContent();
        for (BankTransactions bankTransaction : bankTransactions) {

            BankTransactionDTO bankTransactionDTO = new BankTransactionDTO();
            bankTransactionDTO.setUserId(bankTransaction.getUserId());
            //  String userId = bankTransactionDTO.getUserId();
            if (Objects.nonNull(filterRequest.getAdminId()) && !filterRequest.getAdminId().isEmpty() && filterRequest.getAdminId().equals(adminId)) {
                AdminDTO adminDTO = userFeignHelper.getAdminUserId(filterRequest.getAdminId());
                bankTransactionDTO.setUserName(new StringBuffer().append(adminDTO.getFirstName())
                        .append("  ").append(adminDTO.getLastName()).toString());
                bankTransactionDTO.setEmail(adminDTO.getEmail());
                bankTransactionDTO.setMobileNo(adminDTO.getContactNumber());
            } else {
                bankTransactionDTO.setUserName(filterRequest.getName());
                bankTransactionDTO.setEmail(filterRequest.getEmail());
                bankTransactionDTO.setMobileNo(filterRequest.getContact());
            }
            bankTransactionDTO.setTransactionRemark(bankTransaction.getReason());
            bankTransactionDTO.setAccountNumber(bankTransaction.getAccountNumber());
            bankTransactionDTO.setAccountHolderName(bankTransaction.getAccountHolderName());
            bankTransactionDTO.setIfsc(bankTransaction.getIfsc());
            bankTransactionDTO.setUserType(bankTransaction.getUserType());
            bankTransactionDTO.setCreatedAt(bankTransaction.getCreatedAt());
            bankTransactionDTO.setBankTransactionId(bankTransaction.getBankTransactionId());
            bankTransactionDTO.setTransactionRemark(bankTransaction.getReason());
            bankTransactionDTO.setAmount(bankTransaction.getAmount());
            bankTransactionDTO.setPayoutResponse(bankTransaction.getPayoutResponse());
            bankTransactionDTO.setUpdatedAt(bankTransaction.getUpdatedAt());
            bankTransactionDTO.setRefundResponse(bankTransaction.getRefundResponse());
            bankTransactionDTO.setStatus(bankTransaction.getStatus());
            bankTransactionDTO.setTransactionType(bankTransaction.getTransactionType());
            if (bankTransaction.getTransactionType().equals("WALLET_TO_BANK")) {
                bankTransactionDTO.setWalletTransactionId(bankTransaction.getOrderId());
            }
            bankTransactionDTOList.add(bankTransactionDTO);
        }
        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), bankTransactionDTOList, pagedInfo.page,
                pagedInfo.items);

    }

    private void prepareBankSearchQuery(BankTransactionFilterRequest filterRequest,
                                        GenericSpecificationsBuilder<BankTransactions> builder) {

        // builder.with(specificationFactory.isEqual("deleted", false));

        if (StringUtils.isNotBlank(filterRequest.getUserId())) {
            builder.with(specificationFactory.isEqual("userId", filterRequest.getUserId()));
        }
        if (StringUtils.isNotBlank(filterRequest.getAccountNumber())) {
            builder.with(specificationFactory.like("accountNumber", filterRequest.getAccountNumber()));
        }

        if (StringUtils.isNotBlank(filterRequest.getTransactionRemark())) {
            builder.with(specificationFactory.like("reason", filterRequest.getTransactionRemark()));
        }

        if (StringUtils.isNotBlank(filterRequest.getAccountHolderNumber())) {
            builder.with(specificationFactory.like("accountHolderName", filterRequest.getAccountHolderNumber()));
        }

        if (StringUtils.isNotBlank(filterRequest.getAccountNumber())) {
            builder.with(specificationFactory.like("accountNumber", filterRequest.getAccountNumber()));
        }

        if (StringUtils.isNotBlank(filterRequest.getIfscCode())) {
            builder.with(specificationFactory.like("ifsc", filterRequest.getIfscCode()));
        }

        if (filterRequest.getStartDate() != null) {
            builder.with(specificationFactory.isGreaterThan("createdAt", filterRequest.getStartDate().atStartOfDay()));
        }
        if (filterRequest.getEndDate() != null) {
            builder.with(specificationFactory.isLessThan("createdAt", filterRequest.getEndDate().plusDays(1).atStartOfDay()));
        }

        if (filterRequest.getTransferAmountFrom() != null) {
            builder.with(specificationFactory
                    .isGreaterThanOrEquals("amount", filterRequest.getTransferAmountFrom()));
        }
        if (filterRequest.getTransferAmountTo() != null) {
            builder.with(specificationFactory
                    .isLessThanOrEquals("amount", filterRequest.getTransferAmountTo()));
        }

        if (StringUtils.isNotBlank(filterRequest.getBankTransactionId())) {
            builder.with(specificationFactory
                    .isEqual("bankTransactionId", filterRequest.getBankTransactionId()));
        }

        if (StringUtils.isNotBlank(filterRequest.getStatus())) {
            builder.with(specificationFactory
                    .isEqual("status", filterRequest.getStatus()));
        }

        if (StringUtils.isNotBlank(filterRequest.getTransactionType())) {
            builder.with(specificationFactory
                    .isEqual("transactionType", filterRequest.getTransactionType()));
        }

    }

    public String getBankTransactionCode() {
        String code = "";
        BankTransactions bankTransaction = null;
        do {
            String newCode = getCode("BNTXN");
            bankTransaction = bankTransactionsRepository.findByBankTransactionId(newCode);
            if (bankTransaction == null) {
                code = newCode;
            }
        } while (bankTransaction != null);
        return code;
    }

    private String getCode(String prefix) {
        Random r = new Random(System.currentTimeMillis());
        int id = ((1 + r.nextInt(2)) * 10000000 + r.nextInt(10000000));
        String codePrefix = prefix;
        return codePrefix + id;
    }

    public RefundResponse processRefund(Double refundAmount, String paymentId) throws Exception {
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setPaymentId(paymentId);
        refundRequest.setRefundAmount(refundAmount);
        ApiResponse refundResponse = userPGService.refund(refundRequest);
        RefundResponse refundDetails = objectMapper.convertValue(refundResponse.getData(), RefundResponse.class);
        refundDetails.setWalletTransfer(false);
        System.out.println("####### Refund Response Amount  " + refundDetails.getAmount());
        System.out.println("####### Refund Response Status " + refundDetails.getStatus());
        return refundDetails;
    }

    private WalletRequest prepareWalletRefundRequest(RazorpayPayoutRequest request) {
        WalletRequest walletRequest = new WalletRequest();
        walletRequest.setAmount(request.getAmount() / 100);
        walletRequest.setCurrentUserId(request.getUserId());
        return walletRequest;
    }

    @Override
    public List<BankTransactionDTO> getAllTransactionForReport(BankTransactionFilterRequest filterRequest) {

        GenericSpecificationsBuilder<BankTransactions> builder = new GenericSpecificationsBuilder<>();
        List<BankTransactionDTO> bankTransactionDTOList = new ArrayList<>();
        prepareBankSearchQuery(filterRequest, builder);
        List<BankTransactions> bankTransactions = bankTransactionsRepository.findAll(builder.build());
        for (BankTransactions bankTransaction : bankTransactions) {

            BankTransactionDTO bankTransactionDTO = new BankTransactionDTO();
            bankTransactionDTO.setUserId(bankTransaction.getUserId());

            bankTransactionDTO.setUserName(filterRequest.getName());
            bankTransactionDTO.setEmail(filterRequest.getEmail());
            bankTransactionDTO.setMobileNo(filterRequest.getContact());

            bankTransactionDTO.setTransactionRemark(bankTransaction.getReason());
            bankTransactionDTO.setAccountNumber(bankTransaction.getAccountNumber());
            bankTransactionDTO.setAccountHolderName(bankTransaction.getAccountHolderName());
            bankTransactionDTO.setIfsc(bankTransaction.getIfsc());
            bankTransactionDTO.setUserType(bankTransaction.getUserType());
            bankTransactionDTO.setCreatedAt(bankTransaction.getCreatedAt());
            bankTransactionDTO.setBankTransactionId(bankTransaction.getBankTransactionId());
            bankTransactionDTO.setTransactionRemark(bankTransaction.getReason());
            bankTransactionDTO.setAmount(bankTransaction.getAmount());
            bankTransactionDTO.setPayoutResponse(bankTransaction.getPayoutResponse());
            bankTransactionDTO.setUpdatedAt(bankTransaction.getUpdatedAt());
            bankTransactionDTO.setRefundResponse(bankTransaction.getRefundResponse());
            bankTransactionDTO.setStatus(bankTransaction.getStatus());
            bankTransactionDTO.setTransactionType(bankTransaction.getTransactionType());
            if (bankTransaction.getTransactionType().equals("WALLET_TO_BANK")) {
                bankTransactionDTO.setWalletTransactionId(bankTransaction.getOrderId());
            }
            bankTransactionDTOList.add(bankTransactionDTO);
        }
        return bankTransactionDTOList;
    }
}
