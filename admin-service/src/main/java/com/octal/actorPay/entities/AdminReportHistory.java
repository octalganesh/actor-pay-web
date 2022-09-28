package com.octal.actorPay.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "admin_report_history")
public class AdminReportHistory extends AbstractPersistable{
    @Column(name = "report_name")
    private String reportName;

    @Column(name = "report_url")
    private String reportURL;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "report_type")
    private String reportType;
}
