package com.octal.actorPay.dto;

/**
 * Author: Nancy
 * PermissionsDTO mapped with permission entity
 */
public class PermissionDTO {

    private String id;
    private Boolean read;
    private Boolean write;

    public PermissionDTO() {
    }

    public PermissionDTO(String id, Boolean read, Boolean write) {
        this.id = id;
        this.read = read;
        this.write = write;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getWrite() {
        return write;
    }

    public void setWrite(Boolean write) {
        this.write = write;
    }
}