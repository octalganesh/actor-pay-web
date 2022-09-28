package com.octal.actorPay.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.client.PaymentAdminClient;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.OfferType;
import com.octal.actorPay.constants.PurchaseStatus;
import com.octal.actorPay.constants.PurchaseType;
import com.octal.actorPay.constants.RequestMoneyStatus;
import com.octal.actorPay.constants.TransactionTypes;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.enums.NotificationTypeEnum;
import com.octal.actorPay.dto.payments.PaymentGatewayResponse;
import com.octal.actorPay.dto.payments.PgSignatureRequest;
import com.octal.actorPay.dto.payments.RequestMoneyDTO;
import com.octal.actorPay.dto.payments.RequestMoneyResponse;
import com.octal.actorPay.dto.payments.WalletDTO;
import com.octal.actorPay.dto.payments.WalletRequest;
import com.octal.actorPay.dto.payments.WalletTransactionDTO;
import com.octal.actorPay.dto.request.CommonUserResponse;
import com.octal.actorPay.dto.request.LoyaltyRewardsRequest;
import com.octal.actorPay.dto.request.RequestMoneyFilter;
import com.octal.actorPay.dto.request.WalletFilterRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.helper.MessageHelper;
import com.octal.actorPay.listener.TransferVoucherEvent;
import com.octal.actorPay.listener.events.AddMoneyIntoWallet;
import com.octal.actorPay.listener.events.AddMoneyIntoWalletMerchant;
import com.octal.actorPay.mapper.RequestMoneyEntityMapper;
import com.octal.actorPay.mapper.RequestMoneyResponseMapper;
import com.octal.actorPay.mapper.WalletEntityMapper;
import com.octal.actorPay.mapper.WalletResponseMapper;
import com.octal.actorPay.mapper.WalletTransactionResponseMapper;
import com.octal.actorPay.model.entities.PayrollWalletDetails;
import com.octal.actorPay.model.entities.PgDetails;
import com.octal.actorPay.model.entities.PurchaseDetails;
import com.octal.actorPay.model.entities.RequestMoney;
import com.octal.actorPay.model.entities.Wallet;
import com.octal.actorPay.model.entities.WalletTransaction;
import com.octal.actorPay.repositories.PayrollWalletDetailsRepository;
import com.octal.actorPay.repositories.PgDetailsRepository;
import com.octal.actorPay.repositories.PurchaseDetailsRepository;
import com.octal.actorPay.repositories.RequestMoneyRepository;
import com.octal.actorPay.repositories.WalletRepository;
import com.octal.actorPay.repositories.WalletTransactionRepository;
import com.octal.actorPay.service.PGService;
import com.octal.actorPay.service.WalletCommon;
import com.octal.actorPay.service.WalletService;
import com.octal.actorPay.spec.RequestMoneySpec;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import com.octal.actorPay.utils.CommonUtils;
import com.octal.actorPay.utils.PGUtils;
import com.octal.actorPay.utils.PaymentCommon;
import com.octal.actorPay.utils.PaymentConstants;
import com.octal.actorPay.utils.PaymentFeignHelper;
import com.octal.actorPay.utils.PaymentServiceCodeGenerator;
import com.octal.actorPay.utils.PercentageCalculateManager;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class WalletServiceImpl implements WalletService {

    @PersistenceContext
    private EntityManager entityManager;


    private WalletRepository walletRepository;

    private MessageHelper messageHelper;

    private WalletTransactionRepository walletTransactionRepository;

    private WalletEntityMapper walletEntityMapper;

    private WalletResponseMapper walletResponseMapper;

    private PaymentServiceCodeGenerator codeGenerator;

    private PaymentFeignHelper paymentFeignHelper;

    private SpecificationFactory<WalletTransaction> specificationFactory;

    private SpecificationFactory<PayrollWalletDetails> payrollSpecificationFactory;

    private PercentageCalculateManager percentageCalculateManager;

    private WalletTransactionResponseMapper walletTxnResponseMapper;

    private WalletCommon walletCommon;

    private PurchaseDetailsRepository purchaseDetailsRepository;

    private RequestMoneyRepository requestMoneyRepository;

    private RequestMoneyEntityMapper requestMoneyEntityMapper;

    private RequestMoneyResponseMapper requestMoneyResponseMapper;

    private SpecificationFactory<RequestMoney> requestMoneySpecificationFactory;
    private RequestMoneyFilter requestMoneyFilter;

    private PaymentServiceCodeGenerator paymentServiceCodeGenerator;

    private ApplicationEventPublisher applicationEventPublisher;

    private PGUtils pgUtils;

    private PGService pgService;

    private PgDetailsRepository pgDetailsRepository;

    private PaymentAdminClient adminClient;

    private PayrollWalletDetailsRepository payrollWalletDetailsRepository;

    public WalletServiceImpl(WalletRepository walletRepository, MessageHelper messageHelper,
                             WalletTransactionRepository walletTransactionRepository,
                             WalletEntityMapper walletEntityMapper,
                             WalletResponseMapper walletResponseMapper,
                             PaymentServiceCodeGenerator codeGenerator,
                             PercentageCalculateManager percentageCalculateManager,
                             SpecificationFactory<WalletTransaction> specificationFactory,
                             SpecificationFactory<PayrollWalletDetails> payrollSpecificationFactory,
                             PaymentFeignHelper paymentFeignHelper,
                             WalletTransactionResponseMapper walletTxnResponseMapper,
                             WalletCommon walletCommon, PurchaseDetailsRepository purchaseDetailsRepository,
                             RequestMoneyRepository requestMoneyRepository, RequestMoneyEntityMapper requestMoneyEntityMapper,
                             RequestMoneyResponseMapper requestMoneyResponseMapper,
                             SpecificationFactory<RequestMoney> requestMoneySpecificationFactory,
                             PaymentServiceCodeGenerator paymentServiceCodeGenerator,
                             ApplicationEventPublisher applicationEventPublisher,
                             PGUtils pgUtils, PGService pgService, PgDetailsRepository pgDetailsRepository,
                             PaymentAdminClient adminClient,
                             PayrollWalletDetailsRepository payrollWalletDetailsRepository) {
        this.walletRepository = walletRepository;
        this.messageHelper = messageHelper;
        this.walletTransactionRepository = walletTransactionRepository;
        this.walletEntityMapper = walletEntityMapper;
        this.walletResponseMapper = walletResponseMapper;
        this.codeGenerator = codeGenerator;
        this.percentageCalculateManager = percentageCalculateManager;
        this.specificationFactory = specificationFactory;
        this.paymentFeignHelper = paymentFeignHelper;
        this.walletTxnResponseMapper = walletTxnResponseMapper;
        this.walletCommon = walletCommon;
        this.purchaseDetailsRepository = purchaseDetailsRepository;
        this.requestMoneyRepository = requestMoneyRepository;
        this.requestMoneyEntityMapper = requestMoneyEntityMapper;
        this.requestMoneyResponseMapper = requestMoneyResponseMapper;
        this.requestMoneySpecificationFactory = requestMoneySpecificationFactory;
        this.paymentServiceCodeGenerator = paymentServiceCodeGenerator;
        this.applicationEventPublisher = applicationEventPublisher;
        this.pgUtils = pgUtils;
        this.pgService = pgService;
        this.pgDetailsRepository = pgDetailsRepository;
        this.adminClient = adminClient;
        this.payrollWalletDetailsRepository = payrollWalletDetailsRepository;
        this.payrollSpecificationFactory = payrollSpecificationFactory;
    }

    private static final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

    @Override
    public WalletDTO createUserWallet(WalletDTO walletDTO) {
//        Wallet wallet = WalletTransformer.DTO_TO_WALLET_ENTITY.apply(walletDTO);
        Wallet wallet = walletEntityMapper.sourceToDestination(walletDTO);
        wallet.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        Wallet newWallet = createWallet(wallet);
        WalletDTO newWalletDto = walletResponseMapper.sourceToDestination(newWallet);
        return newWalletDto;
    }


    private Wallet createWallet(Wallet wallet) {
        wallet.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        Wallet newWallet = walletRepository.save(wallet);
        return newWallet;
    }

    private Wallet createAdminWallet() throws Exception {
        SystemConfigurationDTO systemConfigurationDTO =
                paymentFeignHelper.getGlobalConfigByKey(CommonConstant.ADMIN_ID);
        Wallet walletObj = new Wallet();
        walletObj.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        walletObj.setUserId(systemConfigurationDTO.getParamValue());
        walletObj.setBalanceAmount(0d);
        walletObj.setUserType(CommonConstant.USER_TYPE_ADMIN);
        walletObj.setActive(Boolean.TRUE);
        walletObj = createWallet(walletObj);
        return walletObj;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public WalletTransactionResponse addMoneyToWallet(WalletRequest request) throws Exception {

       /* SystemConfigurationDTO getCustomerAddMoneyLimit = paymentFeignHelper.getGlobalConfigByKey(CommonConstant.CUSTOMER_ADD_MONEY_LIMIT);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        Calendar cal = Calendar.getInstance();
        String str1 = sdf1.format(cal.getTime());
        String str2 = sdf2.format(cal.getTime());*/

        String currentUserId = request.getCurrentUserId();
        String userName = "";
//        double adminCommission = 0d;
        String userType = request.getUserType();
        CommonUserDTO commonUserDTO = paymentFeignHelper.getCommonUserById(currentUserId, userType);
        if (commonUserDTO == null) {
            throw new RuntimeException(String.format("Invalid User id for %s ", currentUserId));
        }

     /*   List<Double> dayAmount = walletRepository.findAmountByDay(str1, str2);
        System.out.println("StartDay amount  was ===  " + dayAmount);
        double dayAmountData;
        if (dayAmount.listIterator().next() == null) {
            dayAmountData = 0d;
        } else {
            dayAmountData = dayAmount.listIterator().next().doubleValue();
        }*/

        Double commissionPercentage = request.getCommissionPercentage();
        List<WalletTransaction> walletTransactions = new ArrayList<>();
        String adminUserId = request.getAdminUserId();
        Wallet adminWallet = walletRepository.findWalletByUserId(adminUserId);
        if (adminWallet == null) {
            adminWallet = createAdminWallet();
        }

        Wallet walletObj = walletRepository.findWalletByUserId(request.getCurrentUserId());
        if (walletObj == null) {
            walletObj = new Wallet();
            walletObj.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            walletObj.setUserId(request.getCurrentUserId());
            walletObj.setBalanceAmount(0d);
            walletObj.setUserType(request.getUserType());
            walletObj.setActive(Boolean.TRUE);
            walletObj = createWallet(walletObj);
        }

        PercentageCharges percentageCharges =
                percentageCalculateManager.calculatePercentage(request);

        String parentTransaction = codeGenerator.getWalletParentTransactionCode();
        // Crediting Wallet amount to Customer Wallet
        request.setWalletTransactionId(codeGenerator.getWalletTransactionCode());
        request.setParentTransaction(parentTransaction);
        request.setTransactionTypes(TransactionTypes.CREDIT);
        request.setPurchaseType(PurchaseType.ADDED_MONEY_TO_WALLET);
        request.setUserId(request.getCurrentUserId());
        request.setToUserId(request.getAdminUserId());
        request.setTransactionRemark(messageHelper.getMessage(PaymentConstants.MONEY_ADDED_INTO_WALLET));
        WalletTransaction crCustomerWalletTransaction = walletCommon.setWalletTransaction(request, walletObj);

        walletTransactions.add(crCustomerWalletTransaction);


        //Crediting Admin Wallet which Added by Customer
        request.setWalletTransactionId(codeGenerator.getWalletTransactionCode());
        request.setParentTransaction(parentTransaction);
        request.setUserType(CommonConstant.USER_TYPE_ADMIN);
        request.setUserId(adminUserId);
        request.setToUserId(currentUserId);
        request.setTransactionTypes(TransactionTypes.CREDIT);
        request.setPurchaseType(PurchaseType.ADDED_MONEY_TO_WALLET);
        request.setCommissionPercentage(commissionPercentage);
        userName = PaymentCommon.setName(commonUserDTO);
        request.setTransactionRemark(String.format(messageHelper
                .getMessage(PaymentConstants.WALLET_MONEY_RECEIVED_BY_CUSTOMER_TO_ADMIN_WALLET), userName));
        WalletTransaction crAdminWalletTransaction = walletCommon.setWalletTransaction(request, adminWallet);
        walletTransactions.add(crAdminWalletTransaction);

        //Crediting Admin Wallet for Admin Commission
        if (percentageCharges.getPercentageCharges() > 0) {
            request = new WalletRequest();
            request.setWalletTransactionId(codeGenerator.getWalletTransactionCode());
            request.setParentTransaction(parentTransaction);
            request.setAmount(percentageCharges.getPercentageCharges());
            request.setToUserId(currentUserId);
            request.setUserType(CommonConstant.USER_TYPE_ADMIN);
            request.setUserId(adminUserId);
            request.setTransactionTypes(TransactionTypes.CREDIT);
            request.setPurchaseType(PurchaseType.ADMIN_WALLET_COMMISSION);
            request.setTransactionRemark(String.format(messageHelper
                    .getMessage(PaymentConstants.CHARGES_RECEIVED_BY_CUSTOMER_TO_ADMIN), userName));
            request.setCommissionPercentage(null);

            WalletTransaction crAdminWalletCommissionTxn = walletCommon.setWalletTransaction(request, adminWallet);
            walletTransactions.add(crAdminWalletCommissionTxn);
        }

        //Debiting Customer Wallet for Admin Commission
        if (percentageCharges.getPercentageCharges() > 0) {
            request.setTransactionTypes(TransactionTypes.DEBIT);
            request.setWalletTransactionId(codeGenerator.getWalletTransactionCode());
            request.setParentTransaction(parentTransaction);
            request.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
            request.setUserId(currentUserId);
            request.setToUserId(adminUserId);
            request.setTransactionRemark(String.format(messageHelper
                    .getMessage(PaymentConstants.CHARGES_DEDUCTED_ADD_FOR_ADMIN_COMMISSION), userName));
            WalletTransaction drCustomerWalletCommissionTxn = walletCommon.setWalletTransaction(request, walletObj);
            walletTransactions.add(drCustomerWalletCommissionTxn);
        }
        List<WalletTransaction> newWalletTransactions = walletTransactionRepository.saveAll(walletTransactions);

        if (newWalletTransactions != null) {
            List<Wallet> walletList = new ArrayList<>();
            double currentWalletBalance = walletObj.getBalanceAmount();

           /* Double initAmount = 0d;
            initAmount = crCustomerWalletTransaction.getTransactionAmount();
            initAmount = initAmount + dayAmountData;
            if (initAmount > Double.parseDouble(getCustomerAddMoneyLimit.getParamValue())) {
                throw new Exception(String.format(currentUserId + " :  You have crossed the add money limit of day !!"));
            } else {*/

            Double newAmount = crCustomerWalletTransaction.getTransferAmount();
            walletObj.setBalanceAmount(newAmount + currentWalletBalance);
            walletObj.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            walletList.add(walletObj);
            double currentAdminWalletBal = adminWallet.getBalanceAmount() != null ? adminWallet.getBalanceAmount() : 0d;
            Double totalAdminBalance = currentAdminWalletBal +
                    crAdminWalletTransaction.getAdminCommission() + crAdminWalletTransaction.getTransferAmount();
            adminWallet.setBalanceAmount(totalAdminBalance);
            walletList.add(adminWallet);
            walletRepository.saveAll(walletList);
        }
        //}
        // Payment Gateway part 1
        PaymentGatewayResponse gatewayResponse = request.getGatewayResponse();
        if (gatewayResponse != null) {
//            throw new ActorPayException("Payment Verification details are not found");
            PgSignatureRequest pgSignatureRequest = new PgSignatureRequest();
            pgSignatureRequest.setSignature(gatewayResponse.getRazorpaySignature());
            pgSignatureRequest.setPaymentId(gatewayResponse.getRazorpayPaymentId());
            pgSignatureRequest.setOrderId(gatewayResponse.getRazorpayOrderId());
            boolean isVerified = pgService.verifySignature(pgSignatureRequest);
            if (!isVerified) {
                throw new RuntimeException("Error while Add Money :- Payment verification is failure");
            }
            PgDetailsDTO pgDetailsDTO = pgUtils.buildPgDetails(gatewayResponse, request.getPaymentMethod(),
                    parentTransaction);
            pgDetailsRepository.save(buildPgDetailsEntity(pgDetailsDTO));
        }
        // Payment Gateway part 2
        WalletTransactionResponse walletTransactionResponse = new WalletTransactionResponse();
        walletTransactionResponse.setTransactionType(TransactionTypes.CREDIT.name());
        walletTransactionResponse.setWalletBalance(walletObj.getBalanceAmount());
        walletTransactionResponse.setTransferredAmount(crCustomerWalletTransaction.getTransactionAmount());
        walletTransactionResponse.setParentTransactionId(parentTransaction);
        walletTransactionResponse.setCustomerName(userName);
        if (userType.equals(CommonConstant.USER_TYPE_CUSTOMER)) {
            ResponseEntity<ApiResponse> rewardEntity = adminClient.getRewardByEvent("ADD_MONEY", commonUserDTO.getUserId());
            if (Objects.nonNull(rewardEntity) && Objects.nonNull(rewardEntity.getBody()) && Objects.nonNull(rewardEntity.getBody().getData())) {
                ObjectMapper mapper = new ObjectMapper();
                LoyaltyRewardsRequest rewardResponse = mapper.convertValue(rewardEntity.getBody().getData(), LoyaltyRewardsRequest.class);
                if (walletTransactionResponse.getTransferredAmount() >= rewardResponse.getPriceLimit()) {
                    LoyaltyRewardHistoryResponse updateRequest = new LoyaltyRewardHistoryResponse();
                    updateRequest.setReason(rewardResponse.getEvent());
                    updateRequest.setEvent(rewardResponse.getEvent());
                    updateRequest.setTransactionId(walletTransactionResponse.getParentTransactionId());
                    updateRequest.setType("CREDIT");
                    updateRequest.setRewardPoint(rewardResponse.getRewardPoint());
                    updateRequest.setUserId(commonUserDTO.getUserId());
                    updateRequest.setOrderId("");
                    adminClient.updateReward(updateRequest);
                }
            }
        }
        walletTransactionResponse.setFromCustomerEmail(commonUserDTO.getEmail());
        walletTransactionResponse.setModeOfTransaction("Wallet");
        try {
            UserDTO dto = paymentFeignHelper.getUserById(commonUserDTO.getUserId());
            if(dto != null){
                FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
                fcmRequest.setNotificationType(NotificationTypeEnum.ADD_MONEY);
                fcmRequest.setNotificationTypeId(walletTransactionResponse.getParentTransactionId());
                applicationEventPublisher.publishEvent(new AddMoneyIntoWallet(dto,walletTransactionResponse,fcmRequest));
            }
        }catch (Exception e){
            MerchantDTO dto = paymentFeignHelper.getMerchantById(commonUserDTO.getUserId());
            if(dto != null){
                FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
                fcmRequest.setNotificationType(NotificationTypeEnum.ADD_MONEY);
                fcmRequest.setNotificationTypeId(walletTransactionResponse.getParentTransactionId());
                applicationEventPublisher.publishEvent(new AddMoneyIntoWalletMerchant(dto,walletTransactionResponse,fcmRequest));
            }
            System.out.println("Error while sending notification: " + e.getMessage());
        }finally {
            return walletTransactionResponse;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public WalletTransactionResponse addMoneyToAdminWallet(WalletRequest request) throws Exception {


        String currentUserId = request.getCurrentUserId();
        String userName = "";
//        double adminCommission = 0d;
        String userType = request.getUserType();
        CommonUserDTO commonUserDTO = paymentFeignHelper.getCommonUserById(currentUserId, userType);
        if (commonUserDTO == null) {
            throw new RuntimeException(String.format("Invalid User id for %s ", currentUserId));
        }
        List<WalletTransaction> walletTransactions = new ArrayList<>();
        String adminUserId = request.getAdminUserId();
        Wallet adminWallet = walletRepository.findWalletByUserId(adminUserId);
        if (adminWallet == null) {
            adminWallet = createAdminWallet();
        }

        Wallet walletObj = walletRepository.findWalletByUserId(request.getCurrentUserId());
        if (walletObj == null) {
            walletObj = new Wallet();
            walletObj.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            walletObj.setUserId(request.getCurrentUserId());
            walletObj.setBalanceAmount(0d);
            walletObj.setUserType(request.getUserType());
            walletObj.setActive(Boolean.TRUE);
            walletObj = createWallet(walletObj);
        }

        String parentTransaction = codeGenerator.getWalletParentTransactionCode();
        // Crediting Wallet amount to Admin Wallet
        request.setWalletTransactionId(codeGenerator.getWalletTransactionCode());
        request.setParentTransaction(parentTransaction);
        request.setTransactionTypes(TransactionTypes.CREDIT);
        request.setPurchaseType(PurchaseType.ADDED_MONEY_TO_WALLET);
        request.setUserId(request.getCurrentUserId());
        request.setToUserId(request.getAdminUserId());
        request.setTransactionRemark(messageHelper.getMessage(PaymentConstants.MONEY_ADDED_INTO_WALLET));
        WalletTransaction crCustomerWalletTransaction = walletCommon.setWalletTransaction(request, walletObj);
        walletTransactions.add(crCustomerWalletTransaction);
        List<WalletTransaction> newWalletTransactions = walletTransactionRepository.saveAll(walletTransactions);
        userName = PaymentCommon.setName(commonUserDTO);
        if (newWalletTransactions != null) {
            List<Wallet> walletList = new ArrayList<>();
            double currentWalletBalance = walletObj.getBalanceAmount();

            Double newAmount = crCustomerWalletTransaction.getTransferAmount();
            walletObj.setBalanceAmount(newAmount + currentWalletBalance);
            walletObj.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            walletList.add(walletObj);
            double currentAdminWalletBal = adminWallet.getBalanceAmount() != null ? adminWallet.getBalanceAmount() : 0d;
            adminWallet.setBalanceAmount(currentAdminWalletBal);
            walletList.add(adminWallet);
            walletRepository.saveAll(walletList);
        }
        //}
        PaymentGatewayResponse gatewayResponse = request.getGatewayResponse();
        if (gatewayResponse == null) {
            throw new ActorPayException("Payment Verification details are not found");
        }
        PgSignatureRequest pgSignatureRequest = new PgSignatureRequest();
        pgSignatureRequest.setSignature(gatewayResponse.getRazorpaySignature());
        pgSignatureRequest.setPaymentId(gatewayResponse.getRazorpayPaymentId());
        pgSignatureRequest.setOrderId(gatewayResponse.getRazorpayOrderId());
        boolean isVerified = pgService.verifySignature(pgSignatureRequest);
        if (!isVerified) {
            throw new RuntimeException("Error while Add Money :- Payment verification is failure");
        }
        PgDetailsDTO pgDetailsDTO = pgUtils.buildPgDetails(gatewayResponse, request.getPaymentMethod(),
                parentTransaction);
        pgDetailsRepository.save(buildPgDetailsEntity(pgDetailsDTO));
        WalletTransactionResponse walletTransactionResponse = new WalletTransactionResponse();
        walletTransactionResponse.setTransactionType(TransactionTypes.CREDIT.name());
        walletTransactionResponse.setWalletBalance(walletObj.getBalanceAmount());
        walletTransactionResponse.setTransferredAmount(crCustomerWalletTransaction.getTransactionAmount());
        walletTransactionResponse.setParentTransactionId(parentTransaction);
        walletTransactionResponse.setCustomerName(userName);
        return walletTransactionResponse;
    }

    @Override
    public long getTransactionCountByType(PurchaseType type) {
        return walletTransactionRepository.countByPurchaseType(type);
    }

    private PgDetails buildPgDetailsEntity(PgDetailsDTO pgDetailsDTO) {
        PgDetails pgDetails = new PgDetails();
        pgDetails.setPgOrderId(pgDetailsDTO.getPgOrderId());
        pgDetails.setPaymentId(pgDetailsDTO.getPaymentId());
        pgDetails.setPgSignature(pgDetailsDTO.getPgSignature());
        pgDetails.setPaymentMethod(pgDetailsDTO.getPaymentMethod());
        pgDetails.setPaymentTypeId(pgDetailsDTO.getPaymentTypeId());
        pgDetails.setActive(Boolean.TRUE);
        pgDetails.setDeleted(Boolean.FALSE);
        pgDetails.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        return pgDetails;
    }

    private Wallet createWalletUser(String userId, String userType) {
        Wallet currentUserWalletObj = new Wallet();
        currentUserWalletObj.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        currentUserWalletObj.setBalanceAmount(0d);
        currentUserWalletObj.setUserId(userId);
        currentUserWalletObj.setUserType(userType);
        currentUserWalletObj.setActive(Boolean.TRUE);
        currentUserWalletObj = createWallet(currentUserWalletObj);
        return currentUserWalletObj;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public WalletTransactionResponse transferMoney(WalletRequest request) throws Exception {
        List<WalletTransaction> walletTransactions = new ArrayList<>();
        List<Wallet> walletList = new ArrayList<>();
        CommonUserDTO currentUserDTO = paymentFeignHelper.getCommonUserById(request.getCurrentUserId(), request.getUserType());
        if (currentUserDTO == null) {
            throw new RuntimeException(String.format("Invalid User id for %s ", request.getCurrentUserId()));
        }


        Wallet currentUserWalletObj = walletRepository.findWalletByUserId(request.getCurrentUserId());
        if (currentUserWalletObj == null) {
            currentUserWalletObj = createWalletUser(request.getCurrentUserId(), request.getUserType());
            throw new RuntimeException("Don't have sufficient Balance to Transfer");
        }
        Double currentBalance = currentUserWalletObj.getBalanceAmount();
        if (currentBalance == null || currentBalance == 0) {
            throw new RuntimeException("Don't have sufficient Balance to Transfer");
        }
        if (currentBalance < request.getAmount()) {
            throw new RuntimeException("Don't have sufficient Balance to Transfer");
        }
        CommonUserDTO beneficiaryUserDTO = paymentFeignHelper.getCommonUserByIdentity(request.getUserIdentity(), request.getBeneficiaryUserType());

        if (beneficiaryUserDTO == null) {
            throw new RuntimeException(String.format("Invalid Beneficiary Identity %s ", request.getCurrentUserId()));
        }
        if (beneficiaryUserDTO.getUserId().equals(currentUserDTO.getUserId())) {
            throw new RuntimeException(
                    String.format("The Same User can't act as Beneficiary %s ", beneficiaryUserDTO.getUserId()));
        }
        request.setBeneficiaryId(beneficiaryUserDTO.getUserId());
        Wallet beneficiaryWalletObj = walletRepository.findWalletByUserId(beneficiaryUserDTO.getUserId());
        if (beneficiaryWalletObj == null) {
            beneficiaryWalletObj = createWalletUser(request.getBeneficiaryId(), request.getBeneficiaryUserType());
        }
        String parentTransaction = codeGenerator.getWalletParentTransactionCode();
        // Debit Process
        request.setTransactionTypes(TransactionTypes.DEBIT);
        request.setUserId(request.getCurrentUserId());
        request.setToUserId(request.getBeneficiaryId());

        WalletTransaction debitTransaction = debit(request, currentUserWalletObj, currentUserDTO,beneficiaryUserDTO);
        debitTransaction.setWalletTransactionId(codeGenerator.getWalletTransactionCode());
        Double currentWalletBalance = walletRepository.findWalletByUserId(request.getCurrentUserId()).getBalanceAmount();
        currentUserWalletObj.setBalanceAmount(currentWalletBalance - debitTransaction.getTransactionAmount());
        currentUserWalletObj.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));

        // Credit Process for Customer (Beneficiary Customer)
        request.setTransactionTypes(TransactionTypes.CREDIT);
        request.setUserId(request.getBeneficiaryId());
        request.setToUserId(request.getCurrentUserId());
        WalletTransaction creditTransaction = credit(request, beneficiaryWalletObj, beneficiaryUserDTO,currentUserDTO);
        creditTransaction.setWalletTransactionId(codeGenerator.getWalletTransactionCode());
        creditTransaction.setParentTransaction(parentTransaction);
        debitTransaction.setParentTransaction(parentTransaction);
        Double beneficiaryBalance = walletRepository.findWalletByUserId(request.getBeneficiaryId()).getBalanceAmount();
        beneficiaryWalletObj.setBalanceAmount(creditTransaction.getTransactionAmount() + beneficiaryBalance);
        beneficiaryWalletObj.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        //Credit/Debit Transaction for Transaction/Wallet Table.
        walletTransactions.add(debitTransaction);
        walletTransactions.add(creditTransaction);
        walletTransactionRepository.saveAll(walletTransactions);
        walletList.add(currentUserWalletObj);
        walletList.add(beneficiaryWalletObj);
        walletRepository.saveAll(walletList);
        if (request.getPurchaseType() == PurchaseType.SHOPPING) {
            PurchaseDetails purchaseDetails = new PurchaseDetails();
            purchaseDetails.setPurchaseStatus(PurchaseStatus.PENDING);
            purchaseDetails.setOrderNo(request.getOrderNo());
            purchaseDetails.setParentTransaction(parentTransaction);
            purchaseDetailsRepository.save(purchaseDetails);
        }
        WalletTransactionResponse walletTransactionResponse = new WalletTransactionResponse();
        walletTransactionResponse.setTransactionType(debitTransaction.getTransactionTypes().name());
        walletTransactionResponse.setWalletBalance(currentUserWalletObj.getBalanceAmount());
        walletTransactionResponse.setTransferredAmount(debitTransaction.getTransactionAmount());
        walletTransactionResponse.setParentTransactionId(parentTransaction);
        if (currentUserDTO.getUserType().equals(CommonConstant.USER_TYPE_CUSTOMER) || currentUserDTO.equals(CommonConstant.USER_TYPE_ADMIN))
            walletTransactionResponse.setCustomerName(currentUserDTO.getFirstName() + " " + currentUserDTO.getLastName());
        if (currentUserDTO.getUserType().equals(CommonConstant.USER_TYPE_MERCHANT))
            walletTransactionResponse.setCustomerName(currentUserDTO.getBusinessName());

        CommonUserDTO commonUserDTO = paymentFeignHelper.getCommonUserById(request.getToUserId(), request.getUserType());
//        if (commonUserDTO.getUserType().equals(CommonConstant.USER_TYPE_CUSTOMER) || commonUserDTO.equals(CommonConstant.USER_TYPE_ADMIN))
//            walletTransactionResponse.setCustomerName(commonUserDTO.getFirstName() + " " + commonUserDTO.getLastName());
//        if (commonUserDTO.getUserType().equals(CommonConstant.USER_TYPE_MERCHANT))
//            walletTransactionResponse.setCustomerName(commonUserDTO.getBusinessName());
        walletTransactionResponse.setCustomerName(PaymentCommon.setName(commonUserDTO));
        //  getVoucher  if amount >= 1000
        transferVoucher(walletTransactionResponse);

        if (currentUserDTO.getUserType().equals(CommonConstant.USER_TYPE_CUSTOMER)) {
            ResponseEntity<ApiResponse> rewardEntity = adminClient.getRewardByEvent("TRANSFER_WALLET", currentUserDTO.getUserId());
            if (Objects.nonNull(rewardEntity) && Objects.nonNull(rewardEntity.getBody()) && Objects.nonNull(rewardEntity.getBody().getData())) {
                ObjectMapper mapper = new ObjectMapper();
                LoyaltyRewardsRequest rewardResponse = mapper.convertValue(rewardEntity.getBody().getData(), LoyaltyRewardsRequest.class);
                if (walletTransactionResponse.getTransferredAmount() >= rewardResponse.getPriceLimit()) {
                    LoyaltyRewardHistoryResponse updateRequest = new LoyaltyRewardHistoryResponse();
                    updateRequest.setReason(rewardResponse.getEvent());
                    updateRequest.setEvent(rewardResponse.getEvent());
                    updateRequest.setTransactionId(parentTransaction);
                    updateRequest.setType("CREDIT");
                    updateRequest.setRewardPoint(rewardResponse.getRewardPoint());
                    updateRequest.setUserId(currentUserDTO.getUserId());
                    updateRequest.setOrderId("");
                    adminClient.updateReward(updateRequest);
                }
            }
        }
        if(commonUserDTO != null){
            walletTransactionResponse.setFromCustomerEmail(commonUserDTO.getEmail());
        }
        if(beneficiaryUserDTO != null){
            walletTransactionResponse.setToCustomerEmail(beneficiaryUserDTO.getEmail());
        }
        walletTransactionResponse.setModeOfTransaction("Wallet");
        try {
            UserDTO fromUserDto = paymentFeignHelper.getUserById(commonUserDTO.getUserId());
            UserDTO toUserDto = paymentFeignHelper.getUserById(beneficiaryUserDTO.getUserId());
            if(fromUserDto != null){
                FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
                fcmRequest.setNotificationType(NotificationTypeEnum.DEBIT_FROM_WALLET);
                fcmRequest.setNotificationTypeId(walletTransactionResponse.getParentTransactionId());
                applicationEventPublisher.publishEvent(new AddMoneyIntoWallet(fromUserDto,walletTransactionResponse,fcmRequest));
            }
            if(toUserDto != null){
                FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
                fcmRequest.setNotificationType(NotificationTypeEnum.CREDIT_INTO_WALLET);
                fcmRequest.setNotificationTypeId(creditTransaction.getParentTransaction());
                applicationEventPublisher.publishEvent(new AddMoneyIntoWallet(toUserDto,walletTransactionResponse,fcmRequest));
            }
        }catch (Exception e){
            MerchantDTO fromUserDto = paymentFeignHelper.getMerchantById(commonUserDTO.getUserId());
            if(fromUserDto != null){
                FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
                fcmRequest.setNotificationType(NotificationTypeEnum.DEBIT_FROM_WALLET);
                fcmRequest.setNotificationTypeId(walletTransactionResponse.getParentTransactionId());
                applicationEventPublisher.publishEvent(new AddMoneyIntoWalletMerchant(fromUserDto,walletTransactionResponse,fcmRequest));
            }
            try{
                UserDTO toUserDto = paymentFeignHelper.getUserById(beneficiaryUserDTO.getUserId());
                if(toUserDto != null){
                    FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
                    fcmRequest.setNotificationType(NotificationTypeEnum.CREDIT_INTO_WALLET);
                    fcmRequest.setNotificationTypeId(creditTransaction.getParentTransaction());
                    applicationEventPublisher.publishEvent(new AddMoneyIntoWallet(toUserDto,walletTransactionResponse,fcmRequest));
                }
            }catch (Exception ex){
                MerchantDTO toUserDto = paymentFeignHelper.getMerchantById(beneficiaryUserDTO.getUserId());
                if(toUserDto != null){
                    FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
                    fcmRequest.setNotificationType(NotificationTypeEnum.CREDIT_INTO_WALLET);
                    fcmRequest.setNotificationTypeId(walletTransactionResponse.getParentTransactionId());
                    applicationEventPublisher.publishEvent(new AddMoneyIntoWalletMerchant(toUserDto,walletTransactionResponse,fcmRequest));
                }
            }
            System.out.println("Error while sending notification: " + e.getMessage());
        }finally {
            return walletTransactionResponse;
        }
    }



    private void transferVoucher(WalletTransactionResponse walletTransactionResponse) {
        if (walletTransactionResponse.getTransferredAmount() >= 1000) {
            OfferType promoCode = OfferType.PROMO_CODE;
            applicationEventPublisher.publishEvent(new TransferVoucherEvent(promoCode));
        }
    }

    @Transactional(rollbackFor = {ActorPayException.class})
    @Override
    public WalletTransactionResponse withDrawMoney(WalletRequest request) throws Exception {
        double commissionAmount = 0d;
        PercentageCharges percentageCharges = null;
        List<WalletTransaction> walletTransactions = new ArrayList<>();
        List<Wallet> walletList = new ArrayList<>();
        Wallet currentUserWalletObj = walletRepository.findWalletByUserId(request.getCurrentUserId());
        if (currentUserWalletObj == null || currentUserWalletObj.getBalanceAmount() <= 0) {
            createWalletUser(request.getCurrentUserId(), request.getUserType());
        }
        double commissionPercentage = request.getCommissionPercentage();

        double currentBalance = currentUserWalletObj.getBalanceAmount();
        double withdrawAmount = request.getAmount();
        if (commissionPercentage > 0) {
            percentageCharges =
                    percentageCalculateManager.calculatePercentage(request);
            commissionAmount = percentageCharges.getPercentageCharges();
        }
        if (currentBalance < withdrawAmount) {
            throw new RuntimeException("Doesn't have sufficient fund to Withdraw Money");
        }
        String parentTransaction = codeGenerator.getWalletParentTransactionCode();
        String walletTransactionId = codeGenerator.getWalletTransactionCode();
        // DEBIT TRANSACTION FOR CUSTOMER WALLET
        request.setParentTransaction(parentTransaction);
        request.setWalletTransactionId(walletTransactionId);
        request.setTransactionRemark(messageHelper.getMessage(PaymentConstants.MONEY_WITHDRAWN_FROM_CUSTOMER_WALLET));
        request.setTransactionTypes(TransactionTypes.DEBIT);
        request.setUserId(request.getCurrentUserId());
        request.setToUserId(request.getAdminUserId());
        request.setUserType(request.getUserType());
        request.setPurchaseType(PurchaseType.WITHDRAW_FROM_WALLET);
        request.setAmount(request.getAmount());
        request.setCommissionPercentage(commissionPercentage);
        WalletTransaction drCustomerTransaction = walletCommon.setWalletTransaction(request, currentUserWalletObj);
        walletTransactions.add(drCustomerTransaction);

        // DEBIT TRANSACTION FOR ADMIN WALLET
        Wallet adminWallet = walletRepository.findWalletByUserId(request.getAdminUserId());
        if (adminWallet == null)
            adminWallet = createAdminWallet();
        if (adminWallet.getBalanceAmount() <= 0) {
            throw new RuntimeException("No Balance amount in the Admin Wallet");
        }
        double adminCurrentBalance = adminWallet.getBalanceAmount();
        request.setTransactionRemark(PaymentConstants.MONEY_WITHDRAWN_FROM_ADMIN_WALLET);
        request.setUserId(request.getAdminUserId());
        request.setToUserId(request.getCurrentUserId());
        request.setUserType(CommonConstant.USER_TYPE_ADMIN);
        request.setPurchaseType(PurchaseType.WITHDRAW_FROM_WALLET);
        WalletTransaction drAdminTransaction = walletCommon.setWalletTransaction(request, adminWallet);
        walletTransactions.add(drAdminTransaction);

        // COMMISSION CREDIT TRANSACTION FOR ADMIN WALLET
        if (commissionPercentage > 0) {
            request.setTransactionRemark(PaymentConstants.CHARGE_RECEIVED_ADMIN_WITHDRAW_MONEY);
            request.setCommissionPercentage(null);
            request.setAmount(percentageCharges.getPercentageCharges());
            request.setTransferAmount(percentageCharges.getPercentageCharges());
            request.setTransactionTypes(TransactionTypes.CREDIT);
            request.setUserType(CommonConstant.USER_TYPE_ADMIN);
            request.setPurchaseType(PurchaseType.ADMIN_WALLET_COMMISSION);
            WalletTransaction crAdminCommissionTxn = walletCommon.setWalletTransaction(request, adminWallet);
            walletTransactions.add(crAdminCommissionTxn);
            adminCurrentBalance = adminCurrentBalance + crAdminCommissionTxn.getTransferAmount();

            // COMMISSION DEBIT TRANSACTION FROM CUSTOMER WALLET
            request.setTransactionRemark(PaymentConstants.CHARGE_DEDUCTED_FROM_CUSTOMER_WITHDRAW_MONEY);
            request.setCommissionPercentage(null);
            request.setAmount(percentageCharges.getPercentageCharges());
            request.setTransferAmount(percentageCharges.getPercentageCharges());
            request.setTransactionTypes(TransactionTypes.DEBIT);
            request.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
            request.setPurchaseType(PurchaseType.ADMIN_WALLET_COMMISSION);
            WalletTransaction drAdminCommissionTxn = walletCommon.setWalletTransaction(request, adminWallet);
            walletTransactions.add(drAdminCommissionTxn);
        }
        walletTransactionRepository.saveAll(walletTransactions);
        double customerCurrentBalance = currentUserWalletObj.getBalanceAmount();
        adminCurrentBalance = adminCurrentBalance - drAdminTransaction.getTransactionAmount();
        customerCurrentBalance = customerCurrentBalance - drCustomerTransaction.getTransactionAmount();
        adminWallet.setBalanceAmount(adminCurrentBalance);
        currentUserWalletObj.setBalanceAmount(customerCurrentBalance);
        walletList.add(adminWallet);
        walletList.add(currentUserWalletObj);
        walletRepository.saveAll(walletList);

        WalletTransactionResponse walletTransactionResponse = new WalletTransactionResponse();
        walletTransactionResponse.setTransactionType(TransactionTypes.CREDIT.name());
        walletTransactionResponse.setWalletBalance(currentUserWalletObj.getBalanceAmount());
        walletTransactionResponse.setTransferredAmount(drAdminTransaction.getTransferAmount());
        walletTransactionResponse.setParentTransactionId(parentTransaction);
        CommonUserDTO userDTO = paymentFeignHelper.getCommonUserByIdentity(request.getCurrentUserId());
        walletTransactionResponse.setCustomerName(userDTO.getFirstName() + " " + userDTO.getLastName());
        return walletTransactionResponse;
    }

    @Override
    public PageItem<WalletTransactionDTO> searchWalletTransactions(WalletFilterRequest filterRequest, PagedItemInfo pagedInfo) throws Exception {
        String userType = filterRequest.getUserType();
        SystemConfigurationDTO systemConfigurationDTO = paymentFeignHelper.getGlobalConfigByKey(CommonConstant.ADMIN_ID);
        String adminId = systemConfigurationDTO.getParamValue().trim();
        GenericSpecificationsBuilder<WalletTransaction> builder = new GenericSpecificationsBuilder<>();
        List<WalletTransactionDTO> walletTransactionDTOList = new ArrayList<>();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(WalletTransaction.class, pagedInfo);
        prepareWalletSearchQuery(filterRequest, builder);
        Page<WalletTransaction> pagedResult = walletTransactionRepository.findAll(builder.build(), pageRequest);
        List<WalletTransaction> walletTransactions = pagedResult.getContent();
        for (WalletTransaction walletTransaction : walletTransactions) {

            WalletTransactionDTO walletTransactionDTO = walletTxnResponseMapper.sourceToDestination(walletTransaction);
            walletTransactionDTO.setParentTransaction(walletTransaction.getParentTransaction());
            String userId = walletTransactionDTO.getUserId();
            if (userId.equals(adminId)) {
                AdminDTO adminDTO = paymentFeignHelper.getAdminUserId(userId);
                walletTransactionDTO.setUserName(new StringBuffer().append(adminDTO.getFirstName())
                        .append("  ").append(adminDTO.getLastName()).toString());
                walletTransactionDTO.setEmail(adminDTO.getEmail());
                walletTransactionDTO.setMobileNo(adminDTO.getContactNumber());
            } else {
//                UserDTO userDTO = paymentFeignHelper.getUserById(walletTransactionDTO.getUserId());
                CommonUserDTO commonUserDTO = paymentFeignHelper.getCommonUserByIdentity(walletTransactionDTO.getUserId());
                if(commonUserDTO != null){
                    walletTransactionDTO.setUserName(PaymentCommon.setName(commonUserDTO));
//                if (commonUserDTO.getUserType().equals(CommonConstant.USER_TYPE_CUSTOMER)) {
//                    walletTransactionDTO.setUserName(new StringBuffer().append(commonUserDTO.getFirstName())
//                            .append(" , ").append(commonUserDTO.getLastName()).toString());
//                }
//                if (commonUserDTO.getUserType().equals(CommonConstant.USER_TYPE_MERCHANT)) {
//                    walletTransactionDTO.setUserName(commonUserDTO.getBusinessName());
//                }
                    walletTransactionDTO.setEmail(commonUserDTO.getEmail());
                    walletTransactionDTO.setMobileNo(commonUserDTO.getContactNumber());
                }
            }
            String toUserId = walletTransactionDTO.getToUser();
            if (StringUtils.isNotBlank(toUserId)) {
                if (toUserId.equals(adminId)) {
                    AdminDTO adminDTO = paymentFeignHelper.getAdminUserId(toUserId);
                    walletTransactionDTO.setToUserName(new StringBuffer().append(adminDTO.getFirstName()).append(" , ")
                            .append(adminDTO.getLastName()).toString());
                    walletTransactionDTO.setToEmail(adminDTO.getEmail());
                    walletTransactionDTO.setToMobileNo(adminDTO.getContactNumber());
                } else {
//                    UserDTO toUserDto = paymentFeignHelper.getUserById(toUserId);
                    CommonUserDTO commonUserToDTO = paymentFeignHelper.getCommonUserByIdentity(toUserId);
                    if(commonUserToDTO != null){
//                    if (commonUserToDTO.getUserType().equals(CommonConstant.USER_TYPE_CUSTOMER)) {
//                        walletTransactionDTO.setToUserName(new StringBuffer(commonUserToDTO.getFirstName()).append(" , ")
//                                .append(commonUserToDTO.getLastName()).toString());
//                    }
//                    if (commonUserToDTO.getUserType().equals(CommonConstant.USER_TYPE_MERCHANT)) {
//                        walletTransactionDTO.setToUserName(commonUserToDTO.getBusinessName());
//                    }
                        walletTransactionDTO.setToUserName(PaymentCommon.setName(commonUserToDTO));
                        walletTransactionDTO.setToEmail(commonUserToDTO.getEmail());
                        walletTransactionDTO.setToMobileNo(commonUserToDTO.getContactNumber());
                    }
                }
            }
            walletTransactionDTO.setWalletId(walletTransaction.getWallet().getId());
            walletTransactionDTOList.add(walletTransactionDTO);
        }
        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), walletTransactionDTOList, pagedInfo.page,
                pagedInfo.items);
    }

    @Override
    public String getWalletByUserIdAndUserType(String userId, String userType) {
        Wallet wallet = walletRepository.findWalletByUserIdAndUserType(userId, userType);
        if (wallet == null)
            return null;
        else
            return wallet.getUserId();
    }

    @Override
    public WalletDTO getWalletBalanceByUserId(String userName, String userType) throws Exception {

       CommonUserDTO commonUserDTO = paymentFeignHelper.getCommonUserByIdentity(userName);
        Wallet wallet = walletRepository.findWalletByUserId(commonUserDTO.getUserId());
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            wallet.setBalanceAmount(0d);
            wallet.setUserId(commonUserDTO.getUserId());
            wallet.setUserType(userType);
            wallet.setActive(Boolean.TRUE);
            wallet = createWallet(wallet);
        }
        WalletDTO walletDTO = walletEntityMapper.destinationToSource(wallet);
        return walletDTO;
    }

    @Override
    public WalletDTO getWalletBalanceByUserId(String userId) throws Exception {
        Wallet wallet = walletRepository.findWalletByUserId(userId);
        WalletDTO walletDTO = walletEntityMapper.destinationToSource(wallet);
        return walletDTO;
    }

    private Wallet findWallet(String userId, String userType) {

        Wallet wallet = walletRepository.findWalletByUserId(userId);
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            wallet.setBalanceAmount(0d);
            wallet.setUserId(userId);
            wallet.setUserType(userType);
            wallet.setActive(Boolean.TRUE);
            wallet = createWallet(wallet);
        }
        return wallet;
    }

    @Override
    public ApiResponse getBeneficiaryDetails(String userIdentity) {
        return null;
    }

    private WalletTransaction debit(WalletRequest request, Wallet currentUserWalletObj, CommonUserDTO currentUserDTO,CommonUserDTO beneficiaryUserDTO) throws Exception {
        WalletTransaction walletTransaction = walletCommon.setWalletTransaction(request, currentUserWalletObj);
        String fromUser = request.getUserId();
        String adminUserId = request.getAdminUserId();
//        UserDTO userDTO = paymentFeignHelper.getUserById(fromUser);
        if (request.getPurchaseType() == PurchaseType.SHOPPING) {
            walletTransaction.setTransactionRemark(
                    String.format(messageHelper.getMessage(PaymentConstants.ORDER_AMOUNT_DEDUCT), request.getOrderNo()));
        } else {
            String transferMode = request.getTransferMode();
            if (StringUtils.isBlank(transferMode)) {
                walletTransaction.setTransactionRemark(
                        String.format(messageHelper.getMessage(PaymentConstants.MONEY_TRANSFERRED_TO_USER), beneficiaryUserDTO.getFirstName(),
                                beneficiaryUserDTO.getLastName()));
            } else {
                walletTransaction.setTransactionRemark(
                        String.format(messageHelper.getMessage(PaymentConstants.MONEY_TRANSFERRED_TO_USER), beneficiaryUserDTO.getFirstName(),
                                beneficiaryUserDTO.getLastName()) + " - " +
                                messageHelper.getMessage(PaymentConstants.TRANSFER_MODE_REQUEST_MONEY));
            }
        }
        return walletTransaction;
    }

    private WalletTransaction credit(WalletRequest request, Wallet beneficiaryWallet, CommonUserDTO beneficiaryUserDTO,CommonUserDTO currentUserDTO) throws Exception {
        WalletTransaction walletTransaction = walletCommon.setWalletTransaction(request, beneficiaryWallet);
//        UserDTO userDTO = paymentFeignHelper.getUserById(walletTransaction.getToUser());
        if (request.getPurchaseType() == PurchaseType.SHOPPING) {
            walletTransaction.setTransactionRemark(
                    String.format(messageHelper.getMessage(PaymentConstants.ORDER_PLACED_MONEY_DEDUCTED), request.getOrderNo()));
        } else {
            String requestMode = request.getTransferMode();
            if (StringUtils.isBlank(requestMode)) {
                walletTransaction.setTransactionRemark(
                        String.format(messageHelper.getMessage(PaymentConstants.MONEY_RECEIVED_FROM_USER), currentUserDTO.getFirstName(), currentUserDTO.getLastName()));
            } else {
                walletTransaction.setTransactionRemark(
                        String.format(messageHelper.getMessage(PaymentConstants.MONEY_RECEIVED_FROM_USER),
                                currentUserDTO.getFirstName(), currentUserDTO.getLastName()) + " - " +
                                messageHelper.getMessage(PaymentConstants.TRANSFER_MODE_REQUEST_MONEY));
            }
        }
        return walletTransaction;
    }

    @Override
    public RequestMoneyDTO cancelMoneyRequest(String requestId, String toUserId) throws Exception {
        RequestMoney requestMoney = requestMoneyRepository
                .findByIdAndRequestMoneyStatusAndToUserId(requestId, RequestMoneyStatus.MONEY_REQUESTED, toUserId).orElseThrow(() -> new
                        RuntimeException("Invalid Request Money to Cancel"));
        LocalDateTime expirationDate = requestMoney.getExpiryDate();
        if (CommonUtils.isExpired(expirationDate)) {
            throw new RuntimeException("Request Money is already expired");
        }
        requestMoney.setRequestMoneyStatus(RequestMoneyStatus.REQUEST_CANCELLED);
        requestMoney.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        requestMoneyRepository.save(requestMoney);
        RequestMoneyDTO requestMoneyDTO = requestMoneyResponseMapper.sourceToDestination(requestMoney);
        return requestMoneyDTO;
    }

    @Override
    public RequestMoneyDTO requestMoney(RequestMoneyDTO requestMoneyDTO) throws Exception {
        CommonUserDTO toUser = paymentFeignHelper.getCommonUserByIdentity(requestMoneyDTO.getUserIdentity(),
                requestMoneyDTO.getRequestUserTypeTo());
        requestMoneyDTO.setToUserId(toUser.getUserId());
        if (toUser.getUserType().equals(CommonConstant.USER_TYPE_CUSTOMER)) {
            requestMoneyDTO.setToUserName(new StringBuffer().append(toUser.getFirstName()).append("  ")
                    .append(toUser.getLastName()).toString());
        }
        if (toUser.getUserType().equals(CommonConstant.USER_TYPE_MERCHANT)) {
            requestMoneyDTO.setToUserName(toUser.getBusinessName());
        }
        requestMoneyDTO.setToUserEmail(toUser.getEmail());

        SystemConfigurationDTO systemConfigurationDTO =
                paymentFeignHelper.getGlobalConfigByKey(CommonConstant.REQUEST_MONEY_EXPIRATION_TIME);
        Long expirationTime = Long.parseLong(systemConfigurationDTO.getParamValue());

        RequestMoney requestMoney = requestMoneyEntityMapper.sourceToDestination(requestMoneyDTO);
        CommonUserDTO commonUserDTO = paymentFeignHelper.getCommonUserById(requestMoney.getUserId(), requestMoneyDTO.getRequestUserTypeFrom());

        if (commonUserDTO.getEmail().equals(toUser.getEmail())) {
            logger.warn(String.valueOf(new StringBuffer().append(commonUserDTO.getEmail()).append(" : You can not request to self")));
            return null;
        } else {
            requestMoney.setRequestMoneyStatus(RequestMoneyStatus.MONEY_REQUESTED);
            requestMoney.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            requestMoney.setExpiryDate(LocalDateTime.now(ZoneOffset.UTC).plusDays(expirationTime));
            requestMoney.setReason(requestMoneyDTO.getReason());
            requestMoney.setRequestUserTypeFrom(requestMoneyDTO.getRequestUserTypeFrom());
            requestMoney.setRequestUserTypeTo(requestMoneyDTO.getRequestUserTypeTo());
            requestMoney.setRequestCode(paymentServiceCodeGenerator.getRequestMoneyCode());
            requestMoney = requestMoneyRepository.save(requestMoney);
            RequestMoneyDTO requestMoneyResponseDTO = requestMoneyResponseMapper.sourceToDestination(requestMoney);

            if (commonUserDTO.getUserType().equals(CommonConstant.USER_TYPE_CUSTOMER)) {
                requestMoneyResponseDTO.setUserName(new StringBuffer().append(commonUserDTO.getFirstName())
                        .append(" , ").append(commonUserDTO.getLastName()).toString());
            }
            if (commonUserDTO.getUserType().equals(CommonConstant.USER_TYPE_MERCHANT)) {
                requestMoneyResponseDTO.setUserName(commonUserDTO.getBusinessName());
            }
            requestMoneyResponseDTO.setUserEmail(commonUserDTO.getEmail());
            requestMoneyResponseDTO.setRequestMoneyStatus(requestMoney.getRequestMoneyStatus());
            requestMoneyResponseDTO.setToUserEmail(requestMoneyDTO.getToUserEmail());
            requestMoneyResponseDTO.setToUserName(requestMoneyDTO.getToUserName());
            WalletTransactionResponse walletTransactionResponse = new WalletTransactionResponse();
            walletTransactionResponse.setCustomerName(toUser.getFirstName());
            walletTransactionResponse.setTransferredAmount(Double.valueOf(requestMoneyDTO.getAmount()));
            walletTransactionResponse.setModeOfTransaction("REQUEST_MONEY");
            walletTransactionResponse.setParentTransactionId(requestMoney.getParentTransactionId());
            try {
                UserDTO dto = paymentFeignHelper.getUserById(toUser.getUserId());
                if(dto != null){
                    FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
                    fcmRequest.setNotificationType(NotificationTypeEnum.REQUEST_MONEY);
                    fcmRequest.setNotificationTypeId(requestMoney.getParentTransactionId());
                    applicationEventPublisher.publishEvent(new AddMoneyIntoWallet(dto,walletTransactionResponse,fcmRequest));
                }
            }catch (Exception e){
                MerchantDTO dto = paymentFeignHelper.getMerchantById(toUser.getUserId());
                if(dto != null){
                    FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
                    fcmRequest.setNotificationType(NotificationTypeEnum.REQUEST_MONEY);
                    fcmRequest.setNotificationTypeId(requestMoney.getParentTransactionId());
                    applicationEventPublisher.publishEvent(new AddMoneyIntoWalletMerchant(dto,walletTransactionResponse,fcmRequest));
                }
                System.out.println("Error while sending notification: " + e.getMessage());
            }finally {
                return requestMoneyResponseDTO;
            }
        }
    }

    public Specification<RequestMoney> getRequestMoneyFilter(RequestMoneyFilter requestMoneyFilter) {
        Specification<RequestMoney> specifications = null;
        List<Specification<RequestMoney>> specs = new ArrayList<>();
        if (requestMoneyFilter.getUserType().equals(CommonConstant.USER_TYPE_CUSTOMER) ||
                requestMoneyFilter.getUserType().equals(CommonConstant.USER_TYPE_MERCHANT)) {
            specifications = RequestMoneySpec.getUserIdOrToUserId(requestMoneyFilter.getUserId(), requestMoneyFilter.getToUserId());
        } else {
            if (StringUtils.isNotBlank(requestMoneyFilter.getUserId())) {
                Specification<RequestMoney> requestMoneySpecification =
                        RequestMoneySpec.combineSpec("userId", requestMoneyFilter.getUserId());
                specifications = Specification.where(specifications).and(requestMoneySpecification);
            }

            if (StringUtils.isNotBlank(requestMoneyFilter.getToUserId())) {
                Specification<RequestMoney> requestMoneySpecification =
                        RequestMoneySpec.combineSpec("toUserId", requestMoneyFilter.getToUserId());
                specifications = Specification.where(specifications).and(requestMoneySpecification);
            }
        }
        if (StringUtils.isNotBlank(requestMoneyFilter.getRequestId())) {
            Specification<RequestMoney> requestMoneySpecification =
                    RequestMoneySpec.combineSpec("id", requestMoneyFilter.getRequestId());
            specifications = Specification.where(specifications).and(requestMoneySpecification);
        }
        if (requestMoneyFilter.getAmount() != null) {
            Specification<RequestMoney> requestMoneySpecification =
                    RequestMoneySpec.combineSpec("amount", requestMoneyFilter.getAmount());
            specifications = Specification.where(specifications).and(requestMoneySpecification);
        }
        if (requestMoneyFilter.getRequestMoneyStatus() != null) {
            Specification<RequestMoney> requestMoneySpecification =
                    RequestMoneySpec.combineSpec("requestMoneyStatus", requestMoneyFilter.getRequestMoneyStatus());
            specifications = Specification.where(specifications).and(requestMoneySpecification);
        }
        if (StringUtils.isNotBlank(requestMoneyFilter.getParentTransactionId())) {
            Specification<RequestMoney> requestMoneySpecification =
                    RequestMoneySpec.combineSpec("parentTransactionId", requestMoneyFilter.getParentTransactionId());
            specifications = Specification.where(specifications).and(requestMoneySpecification);
        }
        if (requestMoneyFilter.getFromAmount() != null && requestMoneyFilter.getToAmount() != null) {
            Specification<RequestMoney> betweenAmount =
                    RequestMoneySpec.amountBetween(requestMoneyFilter.getFromAmount(), requestMoneyFilter.getToAmount());
            specifications = Specification.where(specifications).and(betweenAmount);
        }
        if (requestMoneyFilter.getStartDate() != null && requestMoneyFilter.getEndDate() != null) {
            Specification<RequestMoney> betweenDate =
                    RequestMoneySpec.dateBetween("createdAt", requestMoneyFilter.getStartDate(), requestMoneyFilter.getEndDate().plusDays(1));
            specifications = Specification.where(specifications).and(betweenDate);
        }
        if (requestMoneyFilter.getExpiryStartDate() != null && requestMoneyFilter.getExpiryEndDate() != null) {
            Specification<RequestMoney> betweenDate =
                    RequestMoneySpec.dateBetween("expiryDate", requestMoneyFilter.getExpiryStartDate(), requestMoneyFilter.getExpiryEndDate().plusDays(1));
            specifications = Specification.where(specifications).and(betweenDate);
        }
        if (StringUtils.isNotBlank(requestMoneyFilter.getRequestUserTypeFrom())) {
            Specification<RequestMoney> requestMoneySpecification =
                    RequestMoneySpec.combineSpec("requestUserTypeFrom", requestMoneyFilter.getRequestUserTypeFrom());
            specifications = Specification.where(specifications).and(requestMoneySpecification);
        }
        if (StringUtils.isNotBlank(requestMoneyFilter.getRequestUserTypeTo())) {
            Specification<RequestMoney> requestMoneySpecification =
                    RequestMoneySpec.combineSpec("requestUserTypeTo", requestMoneyFilter.getRequestUserTypeTo());
            specifications = Specification.where(specifications).and(requestMoneySpecification);
        }
        return specifications;
    }


    @Override
    public PageItem<RequestMoneyDTO> getRequestMoney(RequestMoneyFilter requestMoneyFilter, PagedItemInfo pagedInfo) {

        List<RequestMoneyDTO> requestMoneyDTOS = null;
        GenericSpecificationsBuilder<RequestMoney> builder = new GenericSpecificationsBuilder<>();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(RequestMoney.class, pagedInfo);
        requestMoneyFilter.setUserId(requestMoneyFilter.getUserId());
        Specification<RequestMoney> requestMoneyListSpec = getRequestMoneyFilter(requestMoneyFilter);
        Page<RequestMoney> pageResult = requestMoneyRepository.findAll(requestMoneyListSpec, pageRequest);
        requestMoneyDTOS = requestMoneyResponseMapper.sourceToDestination(pageResult.getContent());

        System.out.println(">>>>>>    " + requestMoneyDTOS);
        for (RequestMoneyDTO dto : requestMoneyDTOS) {
            String userId = dto.getUserId();
            CommonUserDTO userDTO = paymentFeignHelper.getCommonUserByIdentity(userId);
            if(userDTO != null){
                dto.setUserName(PaymentCommon.setName(userDTO));
                dto.setUserEmail(userDTO.getEmail());
            }
            CommonUserDTO toUserDTO = paymentFeignHelper.getCommonUserByIdentity(dto.getToUserId());
            if(toUserDTO != null){
                dto.setToUserName(PaymentCommon.setName(toUserDTO));
                dto.setToUserEmail(toUserDTO.getEmail());
                dto.setExpiryDate(dto.getExpiryDate());
                if (dto.getExpiryDate() != null) {
                    if (CommonUtils.isExpired(dto.getExpiryDate())) {
                        dto.setExpired(Boolean.TRUE);
                    }
                }
                dto.setToUserEmail(toUserDTO.getEmail());
            }
        }
        return new PageItem<>(pageResult.getTotalPages(), pageResult.getTotalElements(), requestMoneyDTOS, pagedInfo.page,
                pagedInfo.items);
    }

    @Override
    public RequestMoneyResponse acceptOrDecline(RequestMoneyDTO requestMoneyDTO) throws Exception {
        RequestMoneyResponse requestMoneyResponse = new RequestMoneyResponse();
        RequestMoney requestMoney = requestMoneyRepository
                .findByIdAndRequestMoneyStatusAndToUserId(requestMoneyDTO.getRequestId(),
                        RequestMoneyStatus.MONEY_REQUESTED, requestMoneyDTO.getToUserId()).orElseThrow(() -> new
                        RuntimeException(String.format("Invalid Money Request %s ", requestMoneyDTO.getRequestId())));
        LocalDateTime expirationDate = requestMoney.getExpiryDate();

        if (CommonUtils.isExpired(expirationDate)) {
            throw new RuntimeException("Request Money is already expired ");
        }
        CommonUserDTO toUserDTO = paymentFeignHelper.getCommonUserByIdentity(requestMoney.getToUserId());
        if (requestMoneyDTO.isAccepted()) {
            WalletRequest walletRequest = new WalletRequest();
            // while placing money request, toUserId is to whom requesting money
            walletRequest.setCurrentUserId(requestMoney.getToUserId());
            // while placing money request userId is who  placing the request money
            walletRequest.setToUserId(requestMoney.getUserId());
            walletRequest.setAmount(requestMoney.getAmount());
            walletRequest.setUserIdentity(requestMoney.getUserId());
            walletRequest.setBeneficiaryId(requestMoney.getUserId());
            walletRequest.setUserType(requestMoney.getRequestUserTypeTo());
            walletRequest.setPurchaseType(PurchaseType.TRANSFER);
            walletRequest.setTransferMode(requestMoneyDTO.getTransferMode());
            walletRequest.setBeneficiaryUserType(requestMoney.getRequestUserTypeFrom());
            walletRequest.setTransactionReason(CommonConstant.MONEY_REQUEST_ACCEPTED);
            WalletTransactionResponse walletTransactionResponse = transferMoney(walletRequest);
            requestMoneyResponse.setWalletTransactionResponse(walletTransactionResponse);
            requestMoney.setRequestMoneyStatus(RequestMoneyStatus.REQUEST_ACCEPTED);
            requestMoney.setParentTransactionId(walletTransactionResponse.getParentTransactionId());
        } else {
            requestMoney.setRequestMoneyStatus(RequestMoneyStatus.REQUEST_DECLINED);
        }
        requestMoney.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        requestMoney = requestMoneyRepository.save(requestMoney);
        RequestMoneyDTO requestMoneyResponseDTO = requestMoneyResponseMapper.sourceToDestination(requestMoney);
        CommonUserDTO userDTO = paymentFeignHelper.getCommonUserByIdentity(requestMoney.getUserId());
        requestMoneyResponseDTO.setUserName(PaymentCommon.setName(userDTO));
        requestMoneyResponseDTO.setUserEmail(userDTO.getEmail());
        requestMoneyResponseDTO.setRequestMoneyStatus(requestMoney.getRequestMoneyStatus());
        requestMoneyResponseDTO.setToUserEmail(toUserDTO.getEmail());
        requestMoneyResponseDTO.setToUserName(PaymentCommon.setName(toUserDTO));
        requestMoneyResponse.setRequestMoneyDTO(requestMoneyResponseDTO);
        return requestMoneyResponse;
    }

    @Override
    public RequestMoneyDTO findRequestMoneyByUserIdAndToUserId(String userId, String toUserId) {
        RequestMoney requestMoney = requestMoneyRepository.findRequestMoneyByUserIdAndToUserId(userId, toUserId);
        RequestMoneyDTO requestMoneyDTO = requestMoneyResponseMapper.sourceToDestination(requestMoney);
        return requestMoneyDTO;
    }

    private void prepareWalletSearchQuery(WalletFilterRequest filterRequest,
                                          GenericSpecificationsBuilder<WalletTransaction> builder) {
        builder.with(specificationFactory.isEqual("deleted", false));

        if (StringUtils.isNotBlank(filterRequest.getUserId())) {
            builder.with(specificationFactory.isEqual("userId", filterRequest.getUserId()));
        }
        if (StringUtils.isNotBlank(filterRequest.getToUser())) {
            builder.with(specificationFactory.isEqual("toUser", filterRequest.getToUser()));
        }
        try {
            if (filterRequest.getTransactionTypes() != null) {
                builder.with(specificationFactory.isEqual("transactionTypes", filterRequest.getTransactionTypes()));
            } else {
            }
        } catch (Exception e) {
            String message = e.getMessage();
            System.out.println("Error message " + message);
        }
        try {
            if (filterRequest.getPurchaseType() != null) {
                builder.with(specificationFactory.isEqual("purchaseType", filterRequest.getPurchaseType()));
            } else {
            }

        } catch (Exception e) {
            String message = e.getMessage();
            System.out.println("Error message " + message);
        }

        if (StringUtils.isNotBlank(filterRequest.getTransactionRemark())) {
            builder.with(specificationFactory.like("transactionRemark", filterRequest.getTransactionRemark()));
        }
        if (filterRequest.getStartDate() != null) {
            builder.with(specificationFactory.isGreaterThan("createdAt", filterRequest.getStartDate().atStartOfDay()));
        }
        if (filterRequest.getEndDate() != null) {
            builder.with(specificationFactory.isLessThan("createdAt", filterRequest.getEndDate().plusDays(1).atStartOfDay()));
        }
        if (filterRequest.getAdminCommissionFrom() != null) {
            builder.with(specificationFactory
                    .isGreaterThanOrEquals("adminCommission", filterRequest.getAdminCommissionFrom()));
        }
        if (filterRequest.getAdminCommissionTo() != null) {
            builder.with(specificationFactory
                    .isLessThanOrEquals("adminCommission", filterRequest.getAdminCommissionTo()));
        }
        if (filterRequest.getTransactionAmountFrom() != null) {
            builder.with(specificationFactory
                    .isGreaterThanOrEquals("transactionAmount", filterRequest.getTransactionAmountFrom()));
        }
        if (filterRequest.getTransactionAmountTo() != null) {
            builder.with(specificationFactory
                    .isLessThanOrEquals("transactionAmount", filterRequest.getTransactionAmountTo()));
        }
        if (filterRequest.getTransferAmountFrom() != null) {
            builder.with(specificationFactory
                    .isGreaterThanOrEquals("transferAmount", filterRequest.getTransferAmountFrom()));
        }
        if (filterRequest.getTransferAmountTo() != null) {
            builder.with(specificationFactory
                    .isLessThanOrEquals("transferAmount", filterRequest.getTransferAmountTo()));
        }
        if (filterRequest.getPercentage() != null) {
            builder.with(specificationFactory
                    .isLessThanOrEquals("percentage", filterRequest.getPercentage()));
        }
        if (StringUtils.isNotBlank(filterRequest.getWalletTransactionId())) {
            builder.with(specificationFactory
                    .isEqual("walletTransactionId", filterRequest.getWalletTransactionId()));
        }
        if (StringUtils.isNotBlank(filterRequest.getParentTransaction())) {
            builder.with(specificationFactory
                    .isEqual("parentTransaction", filterRequest.getParentTransaction()));
        }
    }

    private Page<RequestMoney> requestMoneyCriteriaBuilder(RequestMoneyFilter
                                                                   filterRequest, PageRequest pageRequest) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<RequestMoney> criteriaQuery = criteriaBuilder.createQuery(RequestMoney.class);
        Root<RequestMoney> itemRoot = criteriaQuery.from(RequestMoney.class);
        if (StringUtils.isNotBlank(filterRequest.getUserId()) && StringUtils.isNotBlank(filterRequest.getToUserId())) {
            Predicate predicateUserId
                    = criteriaBuilder.equal(itemRoot.get("userId"), filterRequest.getUserId());
            Predicate predicateForToUserId
                    = criteriaBuilder.equal(itemRoot.get("toUserId"), filterRequest.getToUserId());
            Predicate predicate
                    = criteriaBuilder.or(predicateUserId, predicateForToUserId);
            criteriaQuery.where(predicate);
        }
        criteriaQuery.orderBy(criteriaBuilder.desc(itemRoot.get("createdAt")));
        List<RequestMoney> requestMoneyList =
                entityManager.createQuery(criteriaQuery).setFirstResult((int)
                        pageRequest.getOffset()).setMaxResults(pageRequest.getPageSize()).getResultList();
        int total = requestMoneyList.size();
        Page<RequestMoney> pageResult = new PageImpl<>(requestMoneyList, pageRequest, total);
        return pageResult;
    }

    private void prepareRequestMoneySearchQuery(RequestMoneyFilter
                                                        filterRequest, GenericSpecificationsBuilder<RequestMoney> builder) {

//        builder.with(requestMoneySpecificationFactory.isEqual("toUserId", filterRequest.getToUserId()));
//        builder.build().or(requestMoneySpecificationFactory.isEqual("userId",filterRequest.getUserId()));
        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getRequestId())) {
            builder.with(requestMoneySpecificationFactory.isEqual("id", filterRequest.getRequestId()));
        }

        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getUserId())) {
            builder.with(requestMoneySpecificationFactory.isEqual("userId", filterRequest.getUserId()));
        }

        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getToUserId())) {
            builder.with(requestMoneySpecificationFactory.isEqual("toUserId", filterRequest.getToUserId()));
        }

        if (filterRequest.getAmount() != null) {
            builder.with(requestMoneySpecificationFactory.isEqual("amount", filterRequest.getAmount()));
        }
        if (filterRequest.getRequestMoneyStatus() != null) {
            builder.with(requestMoneySpecificationFactory.isEqual("requestMoneyStatus", filterRequest.getRequestMoneyStatus()));
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getParentTransactionId())) {
            {
                builder.with(requestMoneySpecificationFactory.isEqual("parentTransactionId", filterRequest.getParentTransactionId()));
            }
        }
//        if (filterRequest.getStartDate() != null) {
//            builder.with(requestMoneySpecificationFactory.isGreaterThan("createdAt", filterRequest.getStartDate().atStartOfDay()));
//        }
//        if (filterRequest.getEndDate() != null) {
//            builder.with(requestMoneySpecificationFactory.isLessThan("createdAt", filterRequest.getEndDate().plusDays(1).atStartOfDay()));
//        }

        if (filterRequest.getFromAmount() != null) {
            builder.with(requestMoneySpecificationFactory
                    .isGreaterThanOrEquals("amount", filterRequest.getFromAmount()));
        }
        if (filterRequest.getToAmount() != null) {
            builder.with(requestMoneySpecificationFactory
                    .isLessThanOrEquals("amount", filterRequest.getToAmount()));
        }
    }

    @Override
    public CommonUserResponse findUserType(String userIdentity) {

        CommonUserDTO commonUserDTO = paymentFeignHelper.getCommonUserByIdentity(userIdentity);
        if (commonUserDTO == null) {
            throw new RuntimeException("Invalid user Identity");
        } else {
            CommonUserResponse commonUserResponse = new CommonUserResponse();
            if (commonUserDTO.getUserType().equalsIgnoreCase(CommonConstant.USER_TYPE_MERCHANT)) {
                commonUserResponse.setMerchantDetails(commonUserDTO);
            }
            if (commonUserDTO.getUserType().equalsIgnoreCase(CommonConstant.USER_TYPE_CUSTOMER)) {
                commonUserResponse.setCustomerDetails(commonUserDTO);
            }
            return commonUserResponse;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public WalletTransactionResponse addRewardToWallet(WalletRequest request) throws Exception {

        String currentUserId = request.getCurrentUserId();
        String userName = "";
        String userType = CommonConstant.USER_TYPE_CUSTOMER;
        CommonUserDTO commonUserDTO = paymentFeignHelper.getCommonUserById(currentUserId, userType);
        if (commonUserDTO == null) {
            throw new RuntimeException(String.format("Invalid User id for %s ", currentUserId));
        }

        Double commissionPercentage = request.getCommissionPercentage();
        List<WalletTransaction> walletTransactions = new ArrayList<>();
        String adminUserId = request.getAdminUserId();
        Wallet adminWallet = walletRepository.findWalletByUserId(adminUserId);
        if (adminWallet == null) {
            adminWallet = createAdminWallet();
        }

        Wallet walletObj = walletRepository.findWalletByUserId(request.getCurrentUserId());
        if (walletObj == null) {
            walletObj = new Wallet();
            walletObj.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            walletObj.setUserId(request.getCurrentUserId());
            walletObj.setBalanceAmount(0d);
            walletObj.setUserType(request.getUserType());
            walletObj.setActive(Boolean.TRUE);
            walletObj = createWallet(walletObj);
        }

        String parentTransaction = codeGenerator.getWalletParentTransactionCode();
        // Crediting Wallet amount to Customer Wallet
        request.setWalletTransactionId(codeGenerator.getWalletTransactionCode());
        request.setParentTransaction(parentTransaction);
        request.setTransactionTypes(TransactionTypes.CREDIT);
        request.setPurchaseType(PurchaseType.ADDED_MONEY_TO_WALLET);
        request.setUserId(request.getCurrentUserId());
        request.setToUserId(request.getAdminUserId());
        request.setTransactionRemark(messageHelper.getMessage(PaymentConstants.REWARD_ADDED_INTO_WALLET));
        WalletTransaction crCustomerWalletTransaction = walletCommon.setWalletTransaction(request, walletObj);
        crCustomerWalletTransaction.setUserType(userType);
        walletTransactions.add(crCustomerWalletTransaction);


        //Crediting Admin Wallet which Added by Customer
        request.setWalletTransactionId(codeGenerator.getWalletTransactionCode());
        request.setParentTransaction(parentTransaction);
        request.setUserType(CommonConstant.USER_TYPE_ADMIN);
        request.setUserId(adminUserId);
        request.setToUserId(currentUserId);
        request.setTransactionTypes(TransactionTypes.CREDIT);
        request.setPurchaseType(PurchaseType.ADDED_MONEY_TO_WALLET);
        request.setCommissionPercentage(commissionPercentage);
        userName = PaymentCommon.setName(commonUserDTO);
        request.setTransactionRemark(String.format(messageHelper
                .getMessage(PaymentConstants.WALLET_MONEY_RECEIVED_BY_CUSTOMER_TO_ADMIN_WALLET), userName));
        WalletTransaction crAdminWalletTransaction = walletCommon.setWalletTransaction(request, adminWallet);
        walletTransactions.add(crAdminWalletTransaction);
        crAdminWalletTransaction.setUserType(userType);
        List<WalletTransaction> newWalletTransactions = walletTransactionRepository.saveAll(walletTransactions);

        if (newWalletTransactions != null) {
            List<Wallet> walletList = new ArrayList<>();
            double currentWalletBalance = walletObj.getBalanceAmount();
            Double newAmount = crCustomerWalletTransaction.getTransferAmount();
            walletObj.setBalanceAmount(newAmount + currentWalletBalance);
            walletObj.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            walletList.add(walletObj);
            double currentAdminWalletBal = adminWallet.getBalanceAmount() != null ? adminWallet.getBalanceAmount() : 0d;
            Double totalAdminBalance = currentAdminWalletBal +
                    crAdminWalletTransaction.getAdminCommission() + crAdminWalletTransaction.getTransferAmount();
            adminWallet.setBalanceAmount(totalAdminBalance);
            walletList.add(adminWallet);
            walletRepository.saveAll(walletList);
        }

        WalletTransactionResponse walletTransactionResponse = new WalletTransactionResponse();
        walletTransactionResponse.setTransactionType(TransactionTypes.CREDIT.name());
        walletTransactionResponse.setWalletBalance(walletObj.getBalanceAmount());
        walletTransactionResponse.setTransferredAmount(crCustomerWalletTransaction.getTransactionAmount());
        walletTransactionResponse.setParentTransactionId(parentTransaction);
        walletTransactionResponse.setCustomerName(userName);
        return walletTransactionResponse;
    }

    @Override
    public PayrollWalletDTO readCSVFile(String userId, String userType, ByteArrayResource requestFile) {
        try {
            PayrollWalletDTO response = new PayrollWalletDTO();
            List<PayrollWalletDTO.PayrollDTO> payrollDTOS = new ArrayList<>();
            List<PayrollWalletDetails> payrollWalletDetails = new ArrayList<>();
            logger.info("-------CSV reading started--------");
            BufferedReader csvReaderFile = new BufferedReader(new InputStreamReader(requestFile.getInputStream()));
            CSVReader csvReader = new CSVReaderBuilder(csvReaderFile).withSkipLines(1).build();
            String[] csvRecord;

            while ((csvRecord = csvReader.readNext()) != null) {
                PayrollWalletDetails importData = new PayrollWalletDetails();
                if (!csvRecord[0].isEmpty() && !csvRecord[1].isEmpty()) {
                    importData.setReceiverEmail(csvRecord[0]);
                    importData.setAmount(csvRecord[1]);
                    importData.setCreatedAt(LocalDateTime.now());
                    importData.setUpdatedAt(LocalDateTime.now());
                    importData.setMerchantId(userId);
                    importData.setStatus("READY_FOR_RELEASE");
                    payrollWalletDetails.add(importData);
                }
            }
            payrollWalletDetails = payrollWalletDetailsRepository.saveAll(payrollWalletDetails);

            payrollWalletDetails.forEach(
                    v-> {
                        PayrollWalletDTO.PayrollDTO dto = new PayrollWalletDTO.PayrollDTO();
                        dto.setAmount(Double.parseDouble(v.getAmount()));
                        dto.setStatus(v.getStatus());
                        dto.setReceiverEmail(v.getReceiverEmail());
                        dto.setMerchantId(v.getMerchantId());
                        dto.setId(v.getId());
                        payrollDTOS.add(dto);
                    }
            );

            response.setMerchantBalance(getWalletBalanceByUserId(userId).getBalanceAmount());
            response.setPayrollWalletDetails(payrollDTOS);
            response.setTotalAmount(payrollDTOS.stream().mapToDouble(PayrollWalletDTO.PayrollDTO::getAmount).sum());
            response.setTotalUser(payrollDTOS.size());
            return response;
        } catch (Exception e) {
            logger.error("There is some issue in reading csv file");
            throw new RuntimeException("Issue in reading csv file");
        }
    }

    @Override
    public PayrollWalletDTO getReadyPayrolls(String userId) throws Exception {

            PayrollWalletDTO response = new PayrollWalletDTO();
            List<PayrollWalletDTO.PayrollDTO> payrollDTOS = new ArrayList<>();
            List<PayrollWalletDetails> payrollWalletDetails = payrollWalletDetailsRepository.findByStatusAndMerchantId("READY_FOR_RELEASE", userId);
            payrollWalletDetails.forEach(
                    v-> {
                        PayrollWalletDTO.PayrollDTO dto = new PayrollWalletDTO.PayrollDTO();
                        dto.setAmount(Double.parseDouble(v.getAmount()));
                        dto.setStatus(v.getStatus());
                        dto.setReceiverEmail(v.getReceiverEmail());
                        dto.setMerchantId(v.getMerchantId());
                        dto.setId(v.getId());
                        payrollDTOS.add(dto);
                    }
            );

            response.setMerchantBalance(getWalletBalanceByUserId(userId).getBalanceAmount());
            response.setPayrollWalletDetails(payrollDTOS);
            response.setTotalAmount(payrollDTOS.stream().mapToDouble(PayrollWalletDTO.PayrollDTO::getAmount).sum());
            response.setTotalUser(payrollDTOS.size());
            return response;
    }

    @Override
    public void processPayrolls(List<String> payrollIds) throws Exception {

        List<PayrollWalletDetails> payrollWalletDetailsList = payrollWalletDetailsRepository.findAllById(payrollIds);

        for (PayrollWalletDetails payrollWalletDetail : payrollWalletDetailsList) {
          try {
              WalletRequest request = new WalletRequest();
              List<WalletTransaction> walletTransactions = new ArrayList<>();
              List<Wallet> walletList = new ArrayList<>();
              CommonUserDTO currentUserDTO = paymentFeignHelper.getCommonUserById(payrollWalletDetail.getMerchantId(), CommonConstant.USER_TYPE_MERCHANT);

              if (currentUserDTO == null) {
                  throw new RuntimeException(String.format("Invalid User id for %s ", payrollWalletDetail.getMerchantId()));
              }

              Wallet currentUserWalletObj = walletRepository.findWalletByUserId(payrollWalletDetail.getMerchantId());
              if (currentUserWalletObj == null) {
                  currentUserWalletObj = createWalletUser(payrollWalletDetail.getMerchantId(), CommonConstant.USER_TYPE_MERCHANT);
                  throw new RuntimeException("Don't have sufficient Balance to Transfer");
              }
              Double currentBalance = currentUserWalletObj.getBalanceAmount();
              if (currentBalance == null || currentBalance == 0) {
                  throw new RuntimeException("Don't have sufficient Balance to Transfer");
              }
              if (currentBalance < Double.parseDouble(payrollWalletDetail.getAmount())) {
                  throw new RuntimeException("Don't have sufficient Balance to Transfer");
              }
              CommonUserDTO beneficiaryUserDTO = paymentFeignHelper.getCommonUserByIdentity(payrollWalletDetail.getReceiverEmail(), CommonConstant.USER_TYPE_MERCHANT);

              if (beneficiaryUserDTO == null) {
                  throw new RuntimeException(String.format("Invalid Beneficiary Identity %s ", payrollWalletDetail.getReceiverEmail()));
              }
              if (beneficiaryUserDTO.getUserId().equals(currentUserDTO.getUserId())) {
                  throw new RuntimeException(
                          String.format("The Same User can't act as Beneficiary %s ", beneficiaryUserDTO.getUserId()));
              }
              request.setBeneficiaryId(beneficiaryUserDTO.getUserId());
              Wallet beneficiaryWalletObj = walletRepository.findWalletByUserId(beneficiaryUserDTO.getUserId());
              if (beneficiaryWalletObj == null) {
                  beneficiaryWalletObj = createWalletUser(beneficiaryUserDTO.getUserId(), CommonConstant.USER_TYPE_CUSTOMER);
              }
              String parentTransaction = codeGenerator.getWalletParentTransactionCode();
              // Debit Process
              request.setTransactionTypes(TransactionTypes.DEBIT);
              request.setUserId(payrollWalletDetail.getMerchantId());
              request.setToUserId(beneficiaryUserDTO.getUserId());

              WalletTransaction debitTransaction = debit(request, currentUserWalletObj, currentUserDTO, beneficiaryUserDTO);
              debitTransaction.setTransactionRemark(messageHelper.getMessage(PaymentConstants.PAYROLL_ADDED_INTO_WALLET));
              debitTransaction.setWalletTransactionId(codeGenerator.getWalletTransactionCode());
              debitTransaction.setTransactionAmount(Double.parseDouble(payrollWalletDetail.getAmount()));
              debitTransaction.setTransactionReason("Payroll");
              debitTransaction.setTransferAmount(Double.parseDouble(payrollWalletDetail.getAmount()));
              debitTransaction.setUserType(CommonConstant.USER_TYPE_MERCHANT);
              Double currentWalletBalance = walletRepository.findWalletByUserId(payrollWalletDetail.getMerchantId()).getBalanceAmount();
              currentUserWalletObj.setBalanceAmount(currentWalletBalance - debitTransaction.getTransactionAmount());
              currentUserWalletObj.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));

              // Credit Process for Customer (Beneficiary Customer)
              request.setTransactionTypes(TransactionTypes.CREDIT);
              request.setUserId(beneficiaryUserDTO.getUserId());
              request.setToUserId(payrollWalletDetail.getMerchantId());
              WalletTransaction creditTransaction = credit(request, beneficiaryWalletObj, beneficiaryUserDTO, currentUserDTO);
              creditTransaction.setTransactionRemark(messageHelper.getMessage(PaymentConstants.PAYROLL_ADDED_INTO_WALLET));
              creditTransaction.setWalletTransactionId(codeGenerator.getWalletTransactionCode());
              creditTransaction.setParentTransaction(parentTransaction);
              creditTransaction.setTransactionAmount(Double.parseDouble(payrollWalletDetail.getAmount()));
              creditTransaction.setTransferAmount(Double.parseDouble(payrollWalletDetail.getAmount()));
              creditTransaction.setTransactionReason("Payroll");
              creditTransaction.setUserType(CommonConstant.USER_TYPE_MERCHANT);
              debitTransaction.setParentTransaction(parentTransaction);
              Double beneficiaryBalance = walletRepository.findWalletByUserId(beneficiaryUserDTO.getUserId()).getBalanceAmount();
              beneficiaryWalletObj.setBalanceAmount(creditTransaction.getTransactionAmount() + beneficiaryBalance);
              beneficiaryWalletObj.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
              //Credit/Debit Transaction for Transaction/Wallet Table.
              walletTransactions.add(debitTransaction);
              walletTransactions.add(creditTransaction);
              walletTransactionRepository.saveAll(walletTransactions);
              walletList.add(currentUserWalletObj);
              walletList.add(beneficiaryWalletObj);
              walletRepository.saveAll(walletList);

              payrollWalletDetail.setTransactionId(parentTransaction);
              payrollWalletDetail.setReceiverName(beneficiaryUserDTO.getBusinessName());
              payrollWalletDetail.setReceiverUserId(beneficiaryUserDTO.getUserId());
              payrollWalletDetail.setStatus("COMPLETED");
          } catch (Exception e) {
              logger.error(e.getLocalizedMessage());
              payrollWalletDetail.setStatus("NOT_PROCESSED");
          }
        }
        payrollWalletDetailsRepository.saveAll(payrollWalletDetailsList);
    }

    @Override
    public PageItem<PayrollWalletHistoryDTO> searchPayrollDetails(WalletFilterRequest filterRequest, PagedItemInfo pagedInfo) {


        GenericSpecificationsBuilder<PayrollWalletDetails> builder = new GenericSpecificationsBuilder<>();
        List<PayrollWalletHistoryDTO> payrollWalletHistoryDTOList = new ArrayList<>();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(PayrollWalletDetails.class, pagedInfo);
        preparePayrollSearchQuery(filterRequest, builder);
        Page<PayrollWalletDetails> pagedResult = payrollWalletDetailsRepository.findAll(builder.build(), pageRequest);
        List<PayrollWalletDetails> payrollTransactions = pagedResult.getContent();
        for (PayrollWalletDetails payrollWalletDetail : payrollTransactions) {
            PayrollWalletHistoryDTO payrollWalletHistoryDTO = new PayrollWalletHistoryDTO();
            payrollWalletHistoryDTO.setId(payrollWalletDetail.getId());
            payrollWalletHistoryDTO.setStatus(payrollWalletDetail.getStatus());
            payrollWalletHistoryDTO.setAmount(Double.parseDouble(payrollWalletDetail.getAmount()));
            payrollWalletHistoryDTO.setReceiverId(payrollWalletDetail.getReceiverUserId());
            payrollWalletHistoryDTO.setTransactionId(payrollWalletDetail.getTransactionId());
            payrollWalletHistoryDTO.setCreatedAt(payrollWalletDetail.getCreatedAt());
            payrollWalletHistoryDTO.setUpdatedAt(payrollWalletDetail.getUpdatedAt());
            payrollWalletHistoryDTO.setReceiverName(Objects.nonNull(payrollWalletDetail.getReceiverName()) ? payrollWalletDetail.getReceiverName() : "");
            payrollWalletHistoryDTO.setReceiverEmail(payrollWalletDetail.getReceiverEmail());
            payrollWalletHistoryDTOList.add(payrollWalletHistoryDTO);
        }
        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), payrollWalletHistoryDTOList, pagedInfo.page,
                pagedInfo.items);
    }

    private void preparePayrollSearchQuery(WalletFilterRequest filterRequest,
                                          GenericSpecificationsBuilder<PayrollWalletDetails> builder) {

        if (StringUtils.isNotBlank(filterRequest.getUserId())) {
            builder.with(payrollSpecificationFactory.isEqual("merchantId", filterRequest.getUserId()));
        }
        if (StringUtils.isNotBlank(filterRequest.getToUser())) {
            builder.with(payrollSpecificationFactory.isEqual("receiverUserId", filterRequest.getToUser()));
        }

        if (StringUtils.isNotBlank(filterRequest.getReceiverEmail())) {
            builder.with(payrollSpecificationFactory.isEqual("receiverEmail", filterRequest.getReceiverEmail()));
        }

        if (filterRequest.getStartDate() != null) {
            builder.with(payrollSpecificationFactory.isGreaterThan("createdAt", filterRequest.getStartDate().atStartOfDay()));
        }
        if (filterRequest.getEndDate() != null) {
            builder.with(payrollSpecificationFactory.isLessThan("createdAt", filterRequest.getEndDate().plusDays(1).atStartOfDay()));
        }

        if (filterRequest.getTransactionAmountFrom() != null) {
            builder.with(payrollSpecificationFactory
                    .isGreaterThanOrEquals("amount", filterRequest.getTransactionAmountFrom()));
        }
        if (filterRequest.getTransactionAmountTo() != null) {
            builder.with(payrollSpecificationFactory
                    .isLessThanOrEquals("amount", filterRequest.getTransactionAmountTo()));
        }
        if (filterRequest.getTransferAmountFrom() != null) {
            builder.with(payrollSpecificationFactory
                    .isGreaterThanOrEquals("amount", filterRequest.getTransferAmountFrom()));
        }
        if (filterRequest.getTransferAmountTo() != null) {
            builder.with(payrollSpecificationFactory
                    .isLessThanOrEquals("amount", filterRequest.getTransferAmountTo()));
        }

        if (StringUtils.isNotBlank(filterRequest.getWalletTransactionId())) {
            builder.with(payrollSpecificationFactory
                    .isEqual("transactionId", filterRequest.getWalletTransactionId()));
        }
    }

    @Override
    public List<WalletTransactionDTO> getAllWalletTransactionForReport(WalletFilterRequest filterRequest) throws Exception {

        GenericSpecificationsBuilder<WalletTransaction> builder = new GenericSpecificationsBuilder<>();
        List<WalletTransactionDTO> walletTransactionDTOList = new ArrayList<>();
        prepareWalletSearchQuery(filterRequest, builder);
        List<WalletTransaction> walletTransactions = walletTransactionRepository.findAll(builder.build());

        for (WalletTransaction walletTransaction : walletTransactions) {

            WalletTransactionDTO walletTransactionDTO = walletTxnResponseMapper.sourceToDestination(walletTransaction);
            walletTransactionDTO.setParentTransaction(walletTransaction.getParentTransaction());
            String userId = walletTransactionDTO.getUserId();

                CommonUserDTO commonUserDTO = paymentFeignHelper.getCommonUserByIdentity(walletTransactionDTO.getUserId());
                walletTransactionDTO.setUserName(PaymentCommon.setName(commonUserDTO));

                walletTransactionDTO.setEmail(commonUserDTO.getEmail());
                walletTransactionDTO.setMobileNo(commonUserDTO.getContactNumber());

            String toUserId = walletTransactionDTO.getToUser();
            if (StringUtils.isNotBlank(toUserId)) {

                CommonUserDTO commonUserToDTO = paymentFeignHelper.getCommonUserByIdentity(toUserId);
                walletTransactionDTO.setToUserName(PaymentCommon.setName(commonUserToDTO));
                walletTransactionDTO.setToEmail(commonUserToDTO.getEmail());
                walletTransactionDTO.setToMobileNo(commonUserToDTO.getContactNumber());

            }
            walletTransactionDTO.setWalletId(walletTransaction.getWallet().getId());
            walletTransactionDTOList.add(walletTransactionDTO);
        }
        return walletTransactionDTOList;
    }

}
