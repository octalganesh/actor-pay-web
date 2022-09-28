package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.ContactUs;
import com.octal.actorPay.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactUsRepository extends JpaRepository<ContactUs, String> {

    Optional<ContactUs> findByName(String name);
}
