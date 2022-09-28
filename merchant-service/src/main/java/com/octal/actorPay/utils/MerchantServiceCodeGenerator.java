package com.octal.actorPay.utils;

import com.octal.actorPay.config.MerchantSettingsConfig;
import com.octal.actorPay.entities.MerchantQR;
import com.octal.actorPay.repositories.MerchantQrRepository;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class MerchantServiceCodeGenerator {

    private MerchantQrRepository merchantQrRepository;
    private MerchantSettingsConfig merchantSettingsConfig;

    public MerchantServiceCodeGenerator(MerchantQrRepository merchantQrRepository,MerchantSettingsConfig merchantSettingsConfig) {
        this.merchantQrRepository = merchantQrRepository;
        this.merchantSettingsConfig=merchantSettingsConfig;
    }

    public String getUPICode() {
        String code = "";
        MerchantQR merchantQR = null;
        do {
            String newCode = getCode(merchantSettingsConfig.getUpiCode());
            merchantQR = merchantQrRepository.findByUpiQrCode(newCode);
            if (merchantQR == null) {
                code = newCode;
            }
        } while (merchantQR != null);
        return code;
    }


    private String getCode(String prefix) {
        Random r = new Random(System.currentTimeMillis());
        int id = ((1 + r.nextInt(2)) * 10000000 + r.nextInt(10000000));
        String codePrefix = prefix;
        return codePrefix + id;
    }

}
