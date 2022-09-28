package com.octal.actorPay.entities;

import javax.persistence.*;

@Entity
@Table(name = "dispute_message")
public class DisputeMessage extends AbstractPersistable{

    @Column(name = "message")
    private String message;

    @Column(name = "posted_by_id")
    private String postedById;

    @Column(name = "posted_by_name")
    private String postedByName;

    @Column(name = "user_type")
    private String userType;

    @OneToOne
    @JoinColumn(name = "disputeId")
    private DisputeItem disputeItem ;

    public DisputeItem getDisputeItem() {
        return disputeItem;
    }

    public void setDisputeItem(DisputeItem disputeItem) {
        this.disputeItem = disputeItem;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPostedById() {
        return postedById;
    }

    public void setPostedById(String postedById) {
        this.postedById = postedById;
    }

    public String getPostedByName() {
        return postedByName;
    }

    public void setPostedByName(String postedByName) {
        this.postedByName = postedByName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
