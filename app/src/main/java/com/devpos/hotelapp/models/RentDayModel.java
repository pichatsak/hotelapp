package com.devpos.hotelapp.models;

import java.util.Date;

public class RentDayModel {
    private Date date;
    private String nameRent;

    public RentDayModel(Date date, String nameRent) {
        this.date = date;
        this.nameRent = nameRent;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNameRent() {
        return nameRent;
    }

    public void setNameRent(String nameRent) {
        this.nameRent = nameRent;
    }
}
