package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.Modules;
import com.octal.actorPay.entities.Screens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScreensRepository extends JpaRepository<Screens, String> {

}