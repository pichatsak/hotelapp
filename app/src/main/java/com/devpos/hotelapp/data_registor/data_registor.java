package com.devpos.hotelapp.data_registor;

import com.google.firebase.firestore.FieldValue;

import java.util.Date;

public class data_registor {
    private String name;
    private String listhotel;
    private String mail;
    private String phone;
    private String password_regis;
    private String id;
    private String status;
    private Date dateCreate;
    private Date dateStartUse;
    private Date dateEndUse;

    public data_registor() {
    }

    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    public Date getDateStartUse() {
        return dateStartUse;
    }

    public void setDateStartUse(Date dateStartUse) {
        this.dateStartUse = dateStartUse;
    }

    public Date getDateEndUse() {
        return dateEndUse;
    }

    public void setDateEndUse(Date dateEndUse) {
        this.dateEndUse = dateEndUse;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getListhotel() {
        return listhotel;
    }

    public void setListhotel(String listhotel) {
        this.listhotel = listhotel;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword_regis() {
        return password_regis;
    }

    public void setPassword_regis(String password_regis) {
        this.password_regis = password_regis;
    }
}
