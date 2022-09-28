package com.octal.actorPay.service.impl;

import com.octal.actorPay.constants.PGConstant;
import com.octal.actorPay.dto.payments.PayoutRequest;
import com.octal.actorPay.dto.payments.PayoutResponse;
import com.octal.actorPay.dto.payments.QrCodeCreditRequest;
import com.octal.actorPay.dto.payments.RefundRequest;
import com.octal.actorPay.dto.payments.RefundResponse;
import com.octal.actorPay.model.entities.QrCodeDetails;
import com.octal.actorPay.model.entities.QrCodeTransactions;
import com.octal.actorPay.repositories.QrCodeDetailsRepository;
import com.octal.actorPay.repositories.QrCodeTransactionsRepository;
import com.octal.actorPay.service.PGService;
import com.octal.actorPay.service.WebHookService;
import com.razorpay.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.Utilities;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class WebHookServiceImpl implements WebHookService {

    @Autowired
    QrCodeDetailsRepository qrCodeDetailsRepository;

    @Autowired
    PGService pgService;

    @Autowired
    QrCodeTransactionsRepository qrCodeTransactionsRepository;

    private static final Logger log = LoggerFactory.getLogger(WebHookServiceImpl.class);

    @Override
    public void qrCodePaymentCredit(QrCodeCreditRequest request) throws Exception {
        if (request.getEvent().equals("qr_code.credited")) {
           // boolean isValidate = Utils.verifyWebhookSignature(request.toString(), "DMZc5o4ilpifFnMOok0HYsut");
            if (request.getPayload().getPayment().getEntity().getCaptured()) {
                QrCodeDetails qrCodeDetails = qrCodeDetailsRepository.findByQrCodeId(request.getPayload().getQrCode()
                        .getEntity().getId());

                if (qrCodeDetails != null) {
                    QrCodeTransactions qrCodeTransactions = new QrCodeTransactions();
                    qrCodeTransactions.setIfsc(qrCodeDetails.getIfsc());
                    qrCodeTransactions.setAccountNumber(qrCodeDetails.getAccountNumber());
                    qrCodeTransactions.setAccountHolderName(qrCodeDetails.getAccountHolderName());
                    qrCodeTransactions.setReason(request.getPayload().getPayment().getEntity().getDescription());
                    qrCodeTransactions.setQrTransactionId(getQRTransactionCode());
                    qrCodeTransactions.setTransactionType("QR_CODE_TRANSACTION");
                    qrCodeTransactions.setAmount((double) request.getPayload().getPayment().getEntity().getAmount() / 100);

                    PayoutRequest payoutRequest = new PayoutRequest();
                    payoutRequest.setAmount(request.getPayload().getPayment().getEntity().getAmount());
                    payoutRequest.setCurrency(PGConstant.IND_CURRENCY);
                    payoutRequest.setMode("IMPS");
                    payoutRequest.setPurpose("payout");
                    payoutRequest.setNarration("UPI Payment");
                    payoutRequest.setReferenceId(qrCodeTransactions.getQrTransactionId());
                    payoutRequest.setFundAccountId(qrCodeDetails.getFundAccountId());

                    try {
                        PayoutResponse payoutResponse = pgService.createPayout(payoutRequest);
                        qrCodeTransactions.setStatus("PAYOUT_SUCCEED");
                        qrCodeTransactions.setPayoutId(payoutResponse.getPayoutId());
                        qrCodeTransactions.setPayoutResponse(payoutResponse);
                        qrCodeTransactions.setUpdatedAt(LocalDateTime.now());
                        qrCodeTransactionsRepository.save(qrCodeTransactions);
                        log.info("Payout succeed for qr code id : " + qrCodeDetails.getQrCodeId());

                    } catch (Exception e) {

                        RefundResponse refundResponse = processRefund(qrCodeTransactions.getAmount(),
                                request.getPayload().getPayment().getEntity().getId());
                        qrCodeTransactions.setStatus("PAYOUT_REFUNDED");
                        qrCodeTransactions.setRefundResponse(refundResponse);
                        qrCodeTransactions.setUpdatedAt(LocalDateTime.now());
                        qrCodeTransactionsRepository.save(qrCodeTransactions);
                        throw new RuntimeException("There is some issue in payout, your refund is initiated ");
                    }

                } else {
                    log.warn("No qr code details find with this qr code id : " + request.getPayload().getQrCode()
                            .getEntity().getId());
                }
            }
        }
    }

    public String getQRTransactionCode() {
        String code = "";
        QrCodeTransactions bankTransaction = null;
        do {
            String newCode = getCode("QRTXN");
            bankTransaction = qrCodeTransactionsRepository.findByQrTransactionId(newCode);
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
        RefundResponse refundResponse = pgService.doRefund(refundRequest);
        log.info(String.format("Refund Response Amount : %s and Response Status : %s", refundResponse.getAmount(), refundResponse.getStatus()));
        return refundResponse;
    }
}
