package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.MerchantQR;
import com.octal.actorPay.entities.MerchantSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantQrRepository extends JpaRepository<MerchantQR, String> {

   MerchantQR findByUpiQrCode(String upiQrCode);
}
