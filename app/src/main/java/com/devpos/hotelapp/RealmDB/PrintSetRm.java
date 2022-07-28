// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.devpos.hotelapp.RealmDB;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class PrintSetRm extends RealmObject {
    @PrimaryKey
    @Required
    private String id;
    private float sizePaper;
    private int dpiPrinter;
    private int perLine;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getSizePaper() {
        return sizePaper;
    }

    public void setSizePaper(float sizePaper) {
        this.sizePaper = sizePaper;
    }

    public int getDpiPrinter() {
        return dpiPrinter;
    }

    public void setDpiPrinter(int dpiPrinter) {
        this.dpiPrinter = dpiPrinter;
    }

    public int getPerLine() {
        return perLine;
    }

    public void setPerLine(int perLine) {
        this.perLine = perLine;
    }
}
