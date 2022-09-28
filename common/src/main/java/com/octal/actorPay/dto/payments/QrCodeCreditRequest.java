package com.octal.actorPay.dto.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class QrCodeCreditRequest {
    String event;
    String accountId;
    String entity;
    List<String> contains;
    PayloadDTO payload;

    @Data
    public static class PayloadDTO {
        PaymentDTO payment;
        @JsonProperty("qr_code")
        QrCodeDTO qrCode;

        @Data
        public static class PaymentDTO {
            EntityDTO entity;

            @Data
            public static class EntityDTO {
                String id;
                String entity;
                Integer amount;
                String status;
                String method;
                @JsonProperty("amount_refunded")
                Long amountRefunded;
                @JsonProperty("captured")
                Boolean captured;
                String description;
                String vpa;
                String email;
                String contact;
                @JsonProperty("customer_id")
                String customerId;
                Long fee;
                Long tax;
            }
        }

        @Data
        public static class QrCodeDTO {

            EntityQRDTO entity;

            @Data
            public static class EntityQRDTO {
                String id;
                String entity;
                String name;
                String usage;
                String type;
                String imageURL;
                String description;
                @JsonProperty("customer_id")
                String customerId;
            }
        }
    }
}
