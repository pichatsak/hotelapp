package com.devpos.hotelapp;

import static com.applikeysolutions.cosmocalendar.utils.SelectionType.NONE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.selection.MultipleSelectionManager;
import com.applikeysolutions.cosmocalendar.selection.OnDaySelectedListener;
import com.applikeysolutions.cosmocalendar.selection.RangeSelectionManager;
import com.applikeysolutions.cosmocalendar.selection.SingleSelectionManager;
import com.applikeysolutions.cosmocalendar.selection.criteria.BaseCriteria;
import com.applikeysolutions.cosmocalendar.selection.criteria.WeekDayCriteria;
import com.applikeysolutions.cosmocalendar.selection.criteria.month.BaseMonthCriteria;
import com.applikeysolutions.cosmocalendar.selection.criteria.month.CurrentMonthCriteria;
import com.applikeysolutions.cosmocalendar.selection.criteria.month.NextMonthCriteria;
import com.applikeysolutions.cosmocalendar.selection.criteria.month.PreviousMonthCriteria;
import com.applikeysolutions.cosmocalendar.settings.lists.DisabledDaysCriteria;
import com.applikeysolutions.cosmocalendar.settings.lists.DisabledDaysCriteriaType;
import com.applikeysolutions.cosmocalendar.settings.lists.connected_days.ConnectedDays;
import com.applikeysolutions.cosmocalendar.utils.DateUtils;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.devpos.hotelapp.models.RentDayModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ViewStatusRoomActivity extends AppCompatActivity {
    private ImageView backhome;
    private String roomId = "";
    private CalendarView calendarView;
    private WeekDayCriteria fridayCriteria;

    private List<BaseCriteria> threeMonthsCriteriaList;
    private boolean fridayCriteriaEnabled;
    private boolean threeMonthsCriteriaEnabled;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView titleTv;
    private ArrayList<RentDayModel> rentDayModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_status_room);
        backhome = findViewById(R.id.backhome);
        titleTv = findViewById(R.id.titleTv);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            roomId = bundle.getString("roomId");
        }
        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        calendarView = findViewById(R.id.calendar_view);
        createCriterias();
        getDataRoom();

    }

    private void getDataRoom() {
        db.collection("rooms")
                .whereEqualTo("userId", MyApplication.getUser_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> friendsMap = (Map<String, Object>) document.getData().get("listRoom");
                                Map<String, Object> value = (Map<String, Object>) friendsMap.get(roomId);
                                titleTv.setText("สถานะห้อง " + value.get("roomName").toString());
                                getAllRentRoom();
                            }
                        }
                    }
                });
    }

    private void createCriterias() {

        calendarView.setSelectedDayBackgroundColor(R.color.main);

        Set<Long> weeks = new HashSet<>();
        Calendar calendarWeek = Calendar.getInstance();
        calendarWeek.add(Calendar.YEAR, 1990);
        weeks.add(calendarWeek.getTimeInMillis());
        calendarView.setWeekendDays(weeks);
        MultipleSelectionManager multipleSelectionManager = new MultipleSelectionManager(new OnDaySelectedListener() {
            @Override
            public void onDaySelected() {

                Log.e("CALENDAR", "Selected Days : " + calendarView.getSelectedDays());

            }
        });
        calendarView.setSelectionManager(multipleSelectionManager);
    }

    private void getAllRentRoom() {
        db.collection("rents")
                .whereEqualTo("userId", MyApplication.getUser_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> friendsMap = (Map<String, Object>) document.getData().get("listRents");
                                for (Map.Entry<String, Object> entry : friendsMap.entrySet()) {
                                    Map<String, Object> value = (Map<String, Object>) entry.getValue();
                                    if (value.get("roomId").equals(roomId)) {
                                        Timestamp timeStart = (Timestamp) value.get("dateStart");
                                        Timestamp timeEnd = (Timestamp) value.get("dateEnd");
                                        Log.d("CHK_ROOM_RENT", "dateStart : " + timeStart.toDate().toString());
                                        Log.d("CHK_ROOM_RENT", "dateEnd : " + timeEnd.toDate().toString());
                                        List<Date> dateList = getDatesAll(timeStart.toDate(), timeEnd.toDate());

                                        if (calendarView.getSelectionManager() instanceof MultipleSelectionManager) {
                                            for (Date dateGet : dateList) {
                                                Calendar calendarIn = Calendar.getInstance();
                                                calendarIn.setTime(dateGet);
                                                rentDayModelArrayList.add(new RentDayModel(dateGet,value.get("rentName").toString()));
                                                ((MultipleSelectionManager) calendarView.getSelectionManager()).addCriteria(new DayCriteria(calendarIn.get(Calendar.DAY_OF_MONTH), calendarIn.get(Calendar.MONTH), calendarIn.get(Calendar.YEAR)));
                                                calendarView.update();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private static List<Date> getDatesAll(Date dateString1, Date dateString2) {
        ArrayList<Date> dates = new ArrayList<Date>();

        Date date1 = dateString1;
        Date date2 = dateString2;


        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while (!cal1.after(cal2)) {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }


}


class DayCriteria extends BaseCriteria {

    private int dateDay = -1;
    private int dateMonth = -1;
    private int dateYear = -1;

    public DayCriteria(int dateDay, int dateMonth, int dateYear) {
        this.dateDay = dateDay;
        this.dateMonth = dateMonth;
        this.dateYear = dateYear;
    }


    @Override
    public boolean isCriteriaPassed(Day day) {
        return day.getCalendar().get(Calendar.DAY_OF_MONTH) == dateDay
                && day.getCalendar().get(Calendar.YEAR) == dateYear
                && day.getCalendar().get(Calendar.MONTH) == dateMonth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DayCriteria that = (DayCriteria) o;
        return dateDay == that.dateDay;
    }

    @Override
    public int hashCode() {
        return dateDay;
    }
}


