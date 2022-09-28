package com.octal.actorPay.service;

import com.octal.actorPay.dto.payments.ContactRequest;
import com.octal.actorPay.dto.payments.ContactResponse;
import com.octal.actorPay.dto.payments.DeactivatePgRequest;
import com.octal.actorPay.dto.payments.DeactivateResponse;
import com.octal.actorPay.dto.payments.PayoutRequest;
import com.octal.actorPay.dto.payments.PayoutResponse;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.utils.JsonConvertor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Component
public class PgConnectorService extends HttpConnector {


    private String baseEndpoint;
    private String clientId;
    private String clientSecret;
    private String apiUri;


    public ContactResponse createContact(ContactRequest contactRequest) {
        try {
            JsonConvertor<ContactRequest> jsonConvertor = new JsonConvertor<>();
            String json = jsonConvertor.convert(contactRequest);
            System.out.println("JSON  " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ContactResponse response = getWebClientObject(baseEndpoint).post().uri(apiUri)
                .header("authorization", "Basic " + encode(clientId, clientSecret))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(contactRequest))
                .retrieve()
                .onStatus(HttpStatus::isError, res -> {
                    return Mono.error(new ActorPayException("### ERROR " + res.toString()));
                })
                .bodyToMono(ContactResponse.class).block();
        return response;
    }

    public DeactivateResponse activeOrDeActiveAccount(DeactivatePgRequest deactivatePgRequest,String fundAccountId) {
        try {
            JsonConvertor<DeactivatePgRequest> jsonConvertor = new JsonConvertor<>();
            String json = jsonConvertor.convert(deactivatePgRequest);
            System.out.println("Json  " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DeactivateResponse deactivateResponse = getWebClientObject(baseEndpoint).patch()
                .uri(apiUri, uriBuilder -> uriBuilder.build(fundAccountId))
                .header("authorization", "Basic " + encode(clientId, clientSecret))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(deactivatePgRequest))
                .retrieve()
                .onStatus(HttpStatus::isError, res -> {
                    return Mono.error(new ActorPayException("### ERROR  ### #" + res.toString()));
                })
                .bodyToMono(DeactivateResponse.class).block();
        return deactivateResponse;
    }

    private String encode(String kycClientId, String kycClientSecret) {
        String clientCredential = new StringBuffer().append(kycClientId).append(":").append(kycClientSecret).toString();
        String encodedString = Base64.getEncoder().encodeToString(clientCredential.getBytes());
        return encodedString;
    }

    public void loadPGCredential(String baseEndpoint, String
            apiUri, String clientId, String clientSecret) {
        this.baseEndpoint = baseEndpoint;
        this.apiUri = apiUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public PayoutResponse createPayout(PayoutRequest payoutRequest) {
        try {
            JsonConvertor<PayoutRequest> jsonConvertor = new JsonConvertor<>();
            String json = jsonConvertor.convert(payoutRequest);
            System.out.println("JSON  " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PayoutResponse response = getWebClientObject(baseEndpoint).post().uri(apiUri)
                .header("authorization", "Basic " + encode(clientId, clientSecret))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(payoutRequest))
                .retrieve()
                .onStatus(HttpStatus::isError, res -> {
                    return Mono.error(new ActorPayException("### ERROR " + res.toString()));
                })
                .bodyToMono(PayoutResponse.class).block();
        return response;
    }
}
