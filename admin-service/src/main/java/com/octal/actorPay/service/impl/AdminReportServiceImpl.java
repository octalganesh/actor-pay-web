package com.octal.actorPay.service.impl;

import com.amazonaws.AmazonServiceException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.client.AdminOrderClient;
import com.octal.actorPay.client.AdminPaymentClient;
import com.octal.actorPay.client.AdminUserService;
import com.octal.actorPay.dto.AdminReportHistoryDTO;
import com.octal.actorPay.dto.AdminReportURLResponse;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.BankTransactionDTO;
import com.octal.actorPay.dto.OrderReportResponse;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.ReportFilter;
import com.octal.actorPay.dto.payments.WalletTransactionDTO;
import com.octal.actorPay.dto.request.BankTransactionFilterRequest;
import com.octal.actorPay.dto.request.OrderFilterRequest;
import com.octal.actorPay.dto.request.WalletFilterRequest;
import com.octal.actorPay.entities.AdminReportHistory;
import com.octal.actorPay.repositories.AdminReportHistoryRepository;
import com.octal.actorPay.service.AdminReportService;
import com.octal.actorPay.service.UploadService;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AdminReportServiceImpl implements AdminReportService {

    @Autowired
    private AdminOrderClient adminOrderClient;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private AdminUserService userServiceClient;

    @Autowired
    private AdminPaymentClient paymentWalletClient;

    @Autowired
    private AdminReportHistoryRepository adminReportHistoryRepository;

    @Autowired
    private SpecificationFactory<AdminReportHistory> adminSpecificationFactory;

    @Override
    public AdminReportURLResponse createReport(ReportFilter reportFilter) {
        AdminReportURLResponse response = new AdminReportURLResponse();
        String path = null;
        String fileName = UUID.randomUUID() + ".csv";
        if (reportFilter.getType().equals("ORDER")) {
            OrderFilterRequest orderFilterRequest = new OrderFilterRequest();
            orderFilterRequest.setMerchantId(reportFilter.getMerchantId());
            orderFilterRequest.setUserId(reportFilter.getUserId());
            orderFilterRequest.setStartDate(reportFilter.getStartDate());
            orderFilterRequest.setEndDate(reportFilter.getEndDate());

            ResponseEntity<ApiResponse> orderResponse =
                    adminOrderClient.getAllOrdersForReport(orderFilterRequest);

            List<OrderReportResponse> orderDetails = new ObjectMapper().convertValue(orderResponse.getBody().getData(), new TypeReference<>() {});
            path = uploadOrderReport(orderDetails, fileName);
        } else if (reportFilter.getType().equals("BANK_TRANSACTION")){
            BankTransactionFilterRequest bankTransactionFilterRequest = new BankTransactionFilterRequest();
            bankTransactionFilterRequest.setUserId(reportFilter.getUserId());
            bankTransactionFilterRequest.setEmail(reportFilter.getEmail());
            bankTransactionFilterRequest.setContact(reportFilter.getContact());
            bankTransactionFilterRequest.setName(reportFilter.getName());
            bankTransactionFilterRequest.setStartDate(reportFilter.getStartDate());
            bankTransactionFilterRequest.setEndDate(reportFilter.getEndDate());

            ResponseEntity<ApiResponse> bankTransactionResponse =
                    userServiceClient.getAllTransactionForReport(bankTransactionFilterRequest);

            List<BankTransactionDTO>  transactionDetails =  new ObjectMapper().convertValue(bankTransactionResponse.getBody().getData(), new TypeReference<>() {});

            path = uploadTransactionReport(transactionDetails, fileName);
        } else {
            WalletFilterRequest walletFilterRequest = new WalletFilterRequest();
            walletFilterRequest.setUserId(reportFilter.getUserId());
            walletFilterRequest.setStartDate(reportFilter.getStartDate());
            walletFilterRequest.setEndDate(reportFilter.getEndDate());

            ResponseEntity<ApiResponse> walletTransactionResponse =
                    paymentWalletClient.getAllWalletTransactionForReport(walletFilterRequest);

            List<WalletTransactionDTO> walletTransactionDetails = new ObjectMapper().convertValue(walletTransactionResponse.getBody().getData(), new TypeReference<>() {});

            path = uploadWalletTransactionReport(walletTransactionDetails, fileName);

        }

        AdminReportHistory history = new AdminReportHistory();
        history.setReportName(fileName);
        history.setReportType(reportFilter.getType());
        history.setReportURL(path);
        history.setUserId(reportFilter.getUserId());
        history.setUpdatedAt(LocalDateTime.now());
        history.setCreatedAt(LocalDateTime.now());
        response.setReportURL(path);
        adminReportHistoryRepository.save(history);
        return response;
    }


    public String uploadOrderReport(List<OrderReportResponse> orderDetailsList, String fileName) {

        if (orderDetailsList.isEmpty()) {
            return null;
        }
        String path = "";
        StringBuilder orderBuilder = new StringBuilder();
        orderBuilder.append("orderId").append(",").append("orderNo").append(",").append("totalQuantity").append(",").append("totalPrice").append(",").append("totalCgst").append(",").append("totalTaxableValue")
                .append(",").append("customerId").append(",").append("customerName").append(",").append("merchantId").append(",").append("orderStatus").append(",").append("paymentMethod")
                .append(",").append("createdAt");

        for (OrderReportResponse orderDetails : orderDetailsList) {
            orderBuilder.append("\n");
            orderBuilder.append(orderDetails.getOrderId());
            orderBuilder.append(",").append(orderDetails.getOrderNo());
            orderBuilder.append(",").append(orderDetails.getTotalQuantity());
            orderBuilder.append(",").append(orderDetails.getTotalPrice());
            orderBuilder.append(",").append(orderDetails.getTotalCgst());
            orderBuilder.append(",").append(orderDetails.getTotalTaxableValue());
            orderBuilder.append(",").append(orderDetails.getCustomer().getId());
            orderBuilder.append(",").append(orderDetails.getCustomer().getFirstName() + " " + orderDetails.getCustomer().getLastName());
            orderBuilder.append(",").append(orderDetails.getMerchantId());
            orderBuilder.append(",").append(orderDetails.getOrderStatus());
            orderBuilder.append(",").append(orderDetails.getPaymentMethod());
            orderBuilder.append(",").append(orderDetails.getCreatedAt());
        }

        File file = new File(fileName);
        try {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(orderBuilder.toString().getBytes());

                DiskFileItem fileItem = new DiskFileItem("file", "text/plain", false, file.getName(), (int) file.length(), file.getParentFile());
                new FileInputStream(file).transferTo(fileItem.getOutputStream());
                MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
                path = uploadService.uploadSaasFileToS3(multipartFile, "Category/");
                file.delete();

            }
        } catch (IOException | AmazonServiceException ex) {
            file.delete();
        }
        return path;
    }

    public String uploadTransactionReport(List<BankTransactionDTO> bankTransactionList, String fileName) {

        if (bankTransactionList.isEmpty()) {
            return null;
        }
        String path = "";
        StringBuilder transactionBuilder = new StringBuilder();
        transactionBuilder.append("userId").append(",").append("userName").append(",").append("email").append(",").append("mobile").append(",").append("bankTransactionId").append(",").append("amount")
                .append(",").append("IFSC").append(",").append("accountHolderName").append(",").append("accountNumber").append(",").append("transactionType").append(",").append("walletTransactionId")
                .append(",").append("createdAt");

        for (BankTransactionDTO transactionDetails : bankTransactionList) {
            transactionBuilder.append("\n");
            transactionBuilder.append(transactionDetails.getUserId());
            transactionBuilder.append(",").append(transactionDetails.getUserName());
            transactionBuilder.append(",").append(transactionDetails.getEmail());
            transactionBuilder.append(",").append(transactionDetails.getMobileNo());
            transactionBuilder.append(",").append(transactionDetails.getBankTransactionId());
            transactionBuilder.append(",").append(transactionDetails.getAmount());
            transactionBuilder.append(",").append(transactionDetails.getIfsc());
            transactionBuilder.append(",").append(transactionDetails.getAccountHolderName());
            transactionBuilder.append(",").append(transactionDetails.getAccountNumber());
            transactionBuilder.append(",").append(transactionDetails.getTransactionType());
            transactionBuilder.append(",").append(transactionDetails.getWalletTransactionId());
            transactionBuilder.append(",").append(transactionDetails.getCreatedAt());
        }
        File file = new File(fileName);
        try {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(transactionBuilder.toString().getBytes());

                DiskFileItem fileItem = new DiskFileItem("file", "text/plain", false, file.getName(), (int) file.length(), file.getParentFile());
                new FileInputStream(file).transferTo(fileItem.getOutputStream());
                MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
                path = uploadService.uploadSaasFileToS3(multipartFile, "Category/");
                file.delete();

            }
        } catch (IOException | AmazonServiceException ex) {
            file.delete();
        }
        return path;
    }

    public String uploadWalletTransactionReport(List<WalletTransactionDTO> walletTransactionDTOList, String fileName) {

        if (walletTransactionDTOList.isEmpty()) {
            return null;
        }
        String path = "";
        StringBuilder transactionBuilder = new StringBuilder();
        transactionBuilder.append("userId").append(",").append("senderUserName").append(",").append("senderEmail").append(",").append("senderMobile").append(",").append("senderWalletId").append(",").append("receiverUserId")
                .append(",").append("receiverUserName").append(",").append("receiverEmail").append(",").append("receiverMobile").append(",").append("transferAmount").append(",")
                .append("adminCommission").append(",").append("walletTransactionId")
                .append(",").append("createdAt");

        for (WalletTransactionDTO transactionDetails : walletTransactionDTOList) {
            transactionBuilder.append("\n");
            transactionBuilder.append(transactionDetails.getUserId());
            transactionBuilder.append(",").append(transactionDetails.getUserName());
            transactionBuilder.append(",").append(transactionDetails.getEmail());
            transactionBuilder.append(",").append(transactionDetails.getMobileNo());
            transactionBuilder.append(",").append(transactionDetails.getWalletId());
            transactionBuilder.append(",").append(transactionDetails.getToUser());
            transactionBuilder.append(",").append(transactionDetails.getToUserName());
            transactionBuilder.append(",").append(transactionDetails.getToEmail());
            transactionBuilder.append(",").append(transactionDetails.getToMobileNo());
            transactionBuilder.append(",").append(transactionDetails.getTransferAmount());
            transactionBuilder.append(",").append(transactionDetails.getAdminCommission());
            transactionBuilder.append(",").append(transactionDetails.getWalletTransactionId());
            transactionBuilder.append(",").append(transactionDetails.getCreatedAt());
        }
        File file = new File(fileName);
        try {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(transactionBuilder.toString().getBytes());

                DiskFileItem fileItem = new DiskFileItem("file", "text/plain", false, file.getName(), (int) file.length(), file.getParentFile());
                new FileInputStream(file).transferTo(fileItem.getOutputStream());
                MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
                path = uploadService.uploadSaasFileToS3(multipartFile, "Category/");
                file.delete();

            }
        } catch (IOException | AmazonServiceException ex) {
            file.delete();
        }
        return path;
    }

    @Override
    public PageItem<AdminReportHistoryDTO> searchAdminReport(PagedItemInfo pagedInfo, ReportFilter reportFilter) {

        GenericSpecificationsBuilder<AdminReportHistory> builder = new GenericSpecificationsBuilder<>();
        List<AdminReportHistoryDTO> adminReportHistoryDTOList = new ArrayList<>();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(AdminReportHistory.class, pagedInfo);
        prepareHistorySearchQuery(reportFilter, builder);
        Page<AdminReportHistory> pagedResult = adminReportHistoryRepository.findAll(builder.build(), pageRequest);
        List<AdminReportHistory> merchantReportHistories = pagedResult.getContent();
        for (AdminReportHistory adminReport : merchantReportHistories) {
            AdminReportHistoryDTO adminReportHistory = new AdminReportHistoryDTO();
            adminReportHistory.setId(adminReport.getId());
            adminReportHistory.setReportName(adminReport.getReportName());
            adminReportHistory.setReportType(adminReport.getReportType());
            adminReportHistory.setReportURL(adminReport.getReportURL());
            adminReportHistory.setCreatedAt(adminReport.getCreatedAt());
            adminReportHistory.setUpdatedAt(adminReport.getUpdatedAt());
            adminReportHistoryDTOList.add(adminReportHistory);
        }
        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), adminReportHistoryDTOList, pagedInfo.page,
                pagedInfo.items);
    }

    private void prepareHistorySearchQuery(ReportFilter reportFilter, GenericSpecificationsBuilder<AdminReportHistory> builder) {

        if (StringUtils.isNotBlank(reportFilter.getUserId())) {
            builder.with(adminSpecificationFactory.isEqual("userId", reportFilter.getUserId()));
        }

        if (reportFilter.getStartDate() != null) {
            builder.with(adminSpecificationFactory.isGreaterThan("createdAt", reportFilter.getStartDate().atStartOfDay()));
        }
        if (reportFilter.getEndDate() != null) {
            builder.with(adminSpecificationFactory.isLessThan("createdAt", reportFilter.getEndDate().plusDays(1).atStartOfDay()));
        }

        if (reportFilter.getType() != null && !reportFilter.getType().isEmpty()) {
            builder.with(adminSpecificationFactory
                    .isGreaterThanOrEquals("reportType", reportFilter.getType()));
        }
        if (reportFilter.getReportName() != null && !reportFilter.getReportName().isEmpty()) {
            builder.with(adminSpecificationFactory
                    .like("reportName", reportFilter.getReportName()));
        }
    }
}
