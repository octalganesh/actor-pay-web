package com.octal.actorPay.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.octal.actorPay.constants.EkycStatus;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user_doc_error")
public class UserDocError extends AbstractPersistable{


    @Column(name = "error_message")
    private String errorMessage;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "document_id")
    private UserDocument userDocument;

}
