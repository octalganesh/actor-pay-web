package com.octal.actorPay.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.octal.actorPay.constants.EkycStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "user_document")
public class UserDocument extends AbstractPersistable{

    @Column(name = "id_no",unique = true)
    private String idNo;

    @Column(name = "doc_type")
    private String docType;

    @Lob
    @Column(name = "document_data")
    private String documentData;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "verified_status")
    @Enumerated(EnumType.STRING)
    private EkycStatus ekycStatus;

    @Column(name = "reason_description")
    private String reasonDescription;

    @OneToMany(mappedBy = "userDocument", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<UserDocError> docErrorList;

}
