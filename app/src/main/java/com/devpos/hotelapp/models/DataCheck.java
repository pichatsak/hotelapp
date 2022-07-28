package com.devpos.hotelapp.models;

import java.util.Date;

public class DataCheck {
    private Date RentDateStart;
    private Date RentDateEnd;

    public DataCheck(Date rentDateStart, Date rentDateEnd) {
        RentDateStart = rentDateStart;
        RentDateEnd = rentDateEnd;
    }

    public DataCheck() {

    }

    public Date getRentDateStart() {
        return RentDateStart;
    }

    public void setRentDateStart(Date rentDateStart) {
        RentDateStart = rentDateStart;
    }

    public Date getRentDateEnd() {
        return RentDateEnd;
    }

    public void setRentDateEnd(Date rentDateEnd) {
        RentDateEnd = rentDateEnd;
    }
}
