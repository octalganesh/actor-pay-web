package com.octal.actorPay.service.impl;

import com.octal.actorPay.dto.GlobalSettingsDTO;
import com.octal.actorPay.entities.GlobalSettings;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.repositories.GlobalSettingsRepositories;
import com.octal.actorPay.service.GlobalSettingsService;
import com.octal.actorPay.transformer.GlobalDataTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Component
public class GlobalSettingsServiceImpl implements GlobalSettingsService {

    @Autowired
    private GlobalSettingsRepositories globalSettingsRepo;

   @Autowired
   private PasswordEncoder passwordEncoder;

    @Override
    public GlobalSettingsDTO getGlobalSettings() {
        Optional<GlobalSettings> gs = globalSettingsRepo.findAll().stream().findFirst();
//        if (gs.isPresent()) {
            return gs.map(gsObj -> GlobalDataTransformer.GLOBAL_SETTINGS_TO_DTO.apply(gsObj)).orElse(null);
//        } else {
//            throw new ObjectNotFoundException("Global settings data not found");
//        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createOrUpdateGlobalSettings(GlobalSettingsDTO gsDTO) {
        // TODO add admin validation

        List<GlobalSettings> gsList = globalSettingsRepo.findAll();
        if(gsList.isEmpty()){
            GlobalSettings gs = new GlobalSettings();
            gs.setId(gsDTO.getId());
            gs.setNumOfRecPerPage(gsDTO.getNumOfRecPerPage());
            gs.setMinAmtTransToUser(gsDTO.getMinAmtTransToUser());
            gs.setMaxAmtTransToUser(gsDTO.getMaxAmtTransToUser());
            gs.setMinAmtTransToBank(gsDTO.getMinAmtTransToBank());
            gs.setMaxAmtTransToBank(gsDTO.getMaxAmtTransToBank());
            gs.setECommerce(gsDTO.getECommerce());
            gs.setRemittance(gsDTO.getRemittance());
            gs.setNFC(gsDTO.getNFC());
            gs.setRechargeMobile(gsDTO.getRechargeMobile());
            gs.setUtilityBillPay(gsDTO.getUtilityBillPay());
            gs.setAdminCommissionAllTrans(gsDTO.getAdminCommissionAllTrans());
            gs.setAdminCommissionOnShopping(gsDTO.getAdminCommissionOnShopping());
            gs.setMailFrom(gsDTO.getMailFrom());
            gs.setMailTo(gsDTO.getMailTo());
            gs.setServerName(gsDTO.getServerName());
            gs.setUserName(gsDTO.getUserName());
            gs.setPassword(passwordEncoder.encode(gsDTO.getPassword()));
            gs.setPort(gsDTO.getPort());

            gs.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            globalSettingsRepo.save(gs);
        } else {
            Optional<GlobalSettings> gsObj = globalSettingsRepo.findById(gsDTO.getId());
            if (gsObj.isPresent()) {

                gsObj.get().setNumOfRecPerPage(gsDTO.getNumOfRecPerPage());
                gsObj.get().setMinAmtTransToUser(gsDTO.getMinAmtTransToUser());
                gsObj.get().setMaxAmtTransToUser(gsDTO.getMaxAmtTransToUser());
                gsObj.get().setMinAmtTransToBank(gsDTO.getMinAmtTransToBank());
                gsObj.get().setMaxAmtTransToBank(gsDTO.getMaxAmtTransToBank());
                gsObj.get().setECommerce(gsDTO.getECommerce());
                gsObj.get().setRemittance(gsDTO.getRemittance());
                gsObj.get().setNFC(gsDTO.getNFC());
                gsObj.get().setRechargeMobile(gsDTO.getRechargeMobile());
                gsObj.get().setUtilityBillPay(gsDTO.getUtilityBillPay());
                gsObj.get().setAdminCommissionAllTrans(gsDTO.getAdminCommissionAllTrans());
                gsObj.get().setAdminCommissionOnShopping(gsDTO.getAdminCommissionOnShopping());
                gsObj.get().setMailFrom(gsDTO.getMailFrom());
                gsObj.get().setMailTo(gsDTO.getMailTo());
                gsObj.get().setServerName(gsDTO.getServerName());
                gsObj.get().setUserName(gsDTO.getUserName());
                gsObj.get().setPassword(passwordEncoder.encode(gsDTO.getPassword()));
                gsObj.get().setPort(gsDTO.getPort());
                gsObj.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

                globalSettingsRepo.save(gsObj.get());
            } else {
                throw new ObjectNotFoundException("Global Settings not found for the given id: "+ gsDTO.getId());
            }
        }
    }

}