package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.GlobalSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalSettingsRepositories extends JpaRepository<GlobalSettings, String> {


}