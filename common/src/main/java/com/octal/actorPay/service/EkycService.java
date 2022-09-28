package com.octal.actorPay.service;

import com.octal.actorPay.dto.EkycResponse;
import com.octal.actorPay.exceptions.ActorPayException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Service
public class EkycService extends HttpConnector {


    private String kycBaseEndpoint;
    private String verificationEndpoint;
    private String kycClientId;
    private String kycClientSecret;

    public EkycResponse verify(MultipartFile frontPart,
                               MultipartFile backPart) throws Exception {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("front_part", frontPart.getResource());
        if (backPart != null) {
            builder.part("back_part", backPart.getResource());
        }
        EkycResponse response = getWebClientObject(kycBaseEndpoint).post().uri(verificationEndpoint)
                .header("authorization", "Basic " + encode(kycClientId, kycClientSecret))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .onStatus(HttpStatus::isError, res -> {
                    return Mono.error(new ActorPayException("Invalid Request or Bad file uploaded"));
                })
                .bodyToMono(EkycResponse.class).block();
        validateResponse(response);
        return response;
    }

    private void validateResponse(EkycResponse response) {
        System.out.println("#### validateResponse  ####");
        if(response == null) {
            throw new ActorPayException("Invalid Request or Bad file uploaded");
        }
        if(StringUtils.isBlank(response.getEncodedImage())) {
            throw new ActorPayException("Invalid Request or Bad file uploaded");
        }
    }
    private String encode(String kycClientId, String kycClientSecret) {
        String clientCredential = new StringBuffer().append(kycClientId).append(":").append(kycClientSecret).toString();
        String encodedString = Base64.getEncoder().encodeToString(clientCredential.getBytes());
        return encodedString;
    }

    ;
    public void loadEkycClientCredentials(String kycBaseEndpoint, String
            verificationEndpoint,String kycClientId,String kycClientSecret) {
        this.kycClientId = kycClientId;
        this.kycClientSecret=kycClientSecret;
        this.kycBaseEndpoint=kycBaseEndpoint;
        this.verificationEndpoint=verificationEndpoint;
    }

}
