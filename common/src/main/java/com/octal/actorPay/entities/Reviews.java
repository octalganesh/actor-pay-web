package com.octal.actorPay.entities;

import javax.persistence.*;

//@Entity
//@Table(name = "reviews")
public class Reviews extends AbstractPersistable {

//    @Column(name = "rating")
    private Integer rating;

//    @Column(name = "reviewer_name")
    private String reviewerName;

//    @Column(name = "comment")
    private String comment;

//    @OneToOne
//    @JoinColumn(name = "product_id")
    private Product product;

//    @OneToOne
//    @JoinColumn(name = "user_id")
    private User user;

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
