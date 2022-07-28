package com.devpos.hotelapp.tools;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateTool {

    public DateTool() {

    }

    public String getMonthYearCur(){
        DateFormat dateFormat = new SimpleDateFormat("MMMM YYYY");
        TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
        dateFormat.setTimeZone(thais);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public int getMonthCurInt(){
        DateFormat dateFormat = new SimpleDateFormat("M");
        TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
        dateFormat.setTimeZone(thais);
        Date date = new Date();
        return Integer.valueOf(dateFormat.format(date));
    }
    public int getYearCurInt(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
        dateFormat.setTimeZone(thais);
        Date date = new Date();
        return Integer.valueOf(dateFormat.format(date));
    }

    public int getDayCurInt(){
        DateFormat dateFormat = new SimpleDateFormat("d");
        TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
        dateFormat.setTimeZone(thais);
        Date date = new Date();
        return Integer.valueOf(dateFormat.format(date));
    }

    public String getDateFromTm(String getd){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dateString = formatter.format(new Date(Long.parseLong(getd)));
        return dateString;
    }

    public String getDayFromTm(String getd){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(Long.parseLong(getd)));
        return dateString;
    }

    public String getTimeFromTm(String getd){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String dateString = formatter.format(new Date(Long.parseLong(getd)));
        return dateString;
    }

    public String getDateFromTm2(String getd){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(Long.parseLong(getd)));
        return dateString;
    }

    public String getDateFromTm3(String getd){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateString = formatter.format(new Date(Long.parseLong(getd)));
        return dateString;
    }


    public String getDateOnlyFromTm(String getd){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(Long.parseLong(getd)));
        return dateString;
    }

    public static long getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try {
            return TimeUnit.DAYS.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getAfterBirth(String getd,String date2){
        String od = getDateFromTm2(getd);
        int dateDifference = (int) getDateDiff(new SimpleDateFormat("dd/MM/yyyy"), od, date2);

//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//        String dateString = formatter.format(new Date(Long.parseLong(getd)));
        return String.valueOf(dateDifference);
    }

    public long getDateCur(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
        TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
        dateFormat.setTimeZone(thais);
        Date date = new Date();
        return date.getTime();
    }


    public String getDateCur2(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
        dateFormat.setTimeZone(thais);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String getMYCur(){
        DateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
        TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
        dateFormat.setTimeZone(thais);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String getDateCur3(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
        dateFormat.setTimeZone(thais);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String getTimeCur2(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
        dateFormat.setTimeZone(thais);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public long getConvDateToTsmp2(String get){
        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        long res = 0;
        Date date = null;
        try {
            date = (Date)formatter.parse(get);
            res = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    public long getConvDateToTsmp(String get){
        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        long res = 0;
        Date date = null;
        try {
            date = (Date)formatter.parse(get);
            res = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String getDateCurrent(int status){
        if(status==0){
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
            dateFormat.setTimeZone(thais);
            Date date = new Date();
            String dateNew = dateFormat.format(date);
            Date date2 = new Date(dateNew);
            return String.valueOf(date2.getTime());
        }else{
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
            dateFormat.setTimeZone(thais);
            Date date = new Date();
            return dateFormat.format(date);
        }

    }
    public String getDatePlus1Day(String dateGet){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();

        try {
            cal.setTime(dateFormat.parse(dateGet));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.DATE, +1);
        return dateFormat.format(cal.getTime());
    }

    public String getDateCurrent2(int status){
        if(status==0){
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
            dateFormat.setTimeZone(thais);
            Date date = new Date();
            String dateNew = dateFormat.format(date);
            Date date2 = new Date(dateNew);
            return String.valueOf(date2.getTime());
        }else{
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
            dateFormat.setTimeZone(thais);
            Date date = new Date();
            return dateFormat.format(date);
        }

    }

    public ArrayList<String> getAllDateBetween(String str_date, String end_date){
        List<Date> dates = new ArrayList<Date>();

        DateFormat formatter;
        ArrayList<String> datelist = new ArrayList<>();
        formatter = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date startDate = (Date) formatter.parse(str_date);
            Date endDate = (Date) formatter.parse(end_date);

            long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
            long endTime = endDate.getTime(); // create your endtime here, possibly using Calendar or Date
            long curTime = startDate.getTime();
            while (curTime <= endTime) {
                dates.add(new Date(curTime));
                curTime += interval;
            }
//            dates.add(new Date(endTime));
//            curTime += interval;
            for (int i = 0; i < dates.size(); i++) {
                Date lDate = (Date) dates.get(i);
                String ds = formatter.format(lDate);
                datelist.add(ds);
                Log.d("DATEGET"," Date is ..." + ds);
            }
        } catch (Exception e) {

        }
        return datelist;
    }


    public String getDateNextDay(int status){
        if(status==0){
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");// HH:mm:ss");
            String reg_date = df.format(c.getTime());
            c.add(Calendar.DATE, 1);
            String end_date = df.format(c.getTime());
//            Log.d("TESTNOW",end_date);
//            Date date = new Date();
//            String dateNew = dateFormat.format(date);
            Date date2 = new Date(String.valueOf(c.getTime()));
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
            dateFormat.setTimeZone(thais);
            Log.d("TESTNOW",String.valueOf(date2.getTime()));
            return String.valueOf(date2.getTime());
        }else{
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
            dateFormat.setTimeZone(thais);
            Date date = new Date();
            return dateFormat.format(date);
        }

    }

}
