package com.devpos.hotelapp.data_registor;

public class data_registor {
    private String name;
    private String listhotel;
    private String mail;
    private String phone;
    private String password_regis;
    private String id;
    private String status;

    public data_registor() {
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
