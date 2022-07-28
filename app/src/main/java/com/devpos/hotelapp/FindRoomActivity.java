package com.devpos.hotelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devpos.hotelapp.adaptors.RoomSearchAdapter;
import com.devpos.hotelapp.adaptors.RoomsAdapter;
import com.devpos.hotelapp.models.CateRoomModels;
import com.devpos.hotelapp.models.DataCheck;
import com.devpos.hotelapp.models.RoomModel;
import com.devpos.hotelapp.models.RoomModelSearch;
import com.devpos.hotelapp.models.RoomModelView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FindRoomActivity extends AppCompatActivity {
    TextView dateStart, dateEnd, chkRoom;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Calendar dateStartChoose = null;
    private Calendar dateEndChoose = null;
    ImageView backhome;
    RecyclerView view_room_all;
    Map<String, Object> cateMap = new HashMap<>();
    Map<String, Object> rentMap = new HashMap<>();
    Map<String, Object> roomMap = new HashMap<>();
    ArrayList<RoomModelSearch> roomModelViewArrayList = new ArrayList<>();
    RoomSearchAdapter roomsAdapter;
    LinearLayout contEmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_room);

        dateStart = findViewById(R.id.dateStart);
        dateEnd = findViewById(R.id.dateEnd);
        chkRoom = findViewById(R.id.chkRoom);
        backhome = findViewById(R.id.backhome);
        view_room_all = findViewById(R.id.view_room_all);
        contEmp = findViewById(R.id.contEmp);

        backhome.setOnClickListener(view -> finish());

        getCateData();

        chkRoom.setOnClickListener(view -> {
            if (dateStart.getText().toString().equals("") || dateEnd.getText().toString().equals("")) {
                Toast.makeText(FindRoomActivity.this, "กรุณาเลือกวันที่ให้ถูกต้อง", Toast.LENGTH_SHORT).show();
            } else {
                getDataShow();
            }
        });

        dateStart.setOnClickListener(view -> {
            final Calendar currentDate = Calendar.getInstance();
            dateStartChoose = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(FindRoomActivity.this, (datePicker, year, monthOfYear, dayOfMonth) -> {
                dateStartChoose.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(FindRoomActivity.this, (view1, hourOfDay, minute) -> {
                    dateStartChoose.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    dateStartChoose.set(Calendar.MINUTE, minute);
                    dateStart.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dateStartChoose.getTime()));

                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();

            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
            datePickerDialog.show();
        });
        dateEnd.setOnClickListener(view -> {
            final Calendar currentDate = Calendar.getInstance();
            dateEndChoose = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(FindRoomActivity.this, new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                    dateEndChoose.set(year, monthOfYear, dayOfMonth);
                    new TimePickerDialog(FindRoomActivity.this, (view12, hourOfDay, minute) -> {
                        dateEndChoose.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateEndChoose.set(Calendar.MINUTE, minute);
                        dateEnd.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dateEndChoose.getTime()));

                    }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();

                }
            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
            datePickerDialog.getDatePicker().setMinDate(dateStartChoose.getTimeInMillis());
            datePickerDialog.show();
        });
    }

    private void getCateData() {

        db.collection("cate_rooms")
                .whereEqualTo("userId", MyApplication.getUser_id())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            cateMap = (Map<String, Object>) document.getData().get("listCate");
                        }
                    }
                });

        db.collection("rents")
                .whereEqualTo("userId", MyApplication.getUser_id())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            rentMap = (Map<String, Object>) document.getData().get("listRents");
                        }
                    }
                });
        db.collection("rooms")
                .whereEqualTo("userId", MyApplication.getUser_id())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            roomMap = (Map<String, Object>) document.getData().get("listRoom");
                        }
                    }
                });
    }

    private void getDataShow() {
        roomModelViewArrayList = new ArrayList<>();
        view_room_all.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        view_room_all.setLayoutManager(mLayoutManager);
        int numRoomFree=0;
        for (Map.Entry<String, Object> entryRoom : roomMap.entrySet()) {
            Map<String, Object> valueRoom = (Map<String, Object>) entryRoom.getValue();

            Log.d("CHKBUSY", valueRoom.get("roomName").toString());
            if(cateMap.containsKey(valueRoom.get("cateId").toString())){
                Map<String, Object> valueCate = (Map<String, Object>) cateMap.get((valueRoom.get("cateId").toString()));
                RoomModelSearch roomModelView = new RoomModelSearch();
                roomModelView.setKey(entryRoom.getKey());
                roomModelView.setRoomName(valueRoom.get("roomName").toString());
                roomModelView.setCateName(valueCate.get("cateName").toString());
                roomModelView.setRoomId(valueRoom.get("roomId").toString());
                int pos = 1;
                int max = rentMap.size();
                int numIn = 0;
                int chkRe = 0;
                int numBusy = 0;
                ArrayList<DataCheck> dataCheckArrayList = new ArrayList<>();
                for (Map.Entry<String, Object> entryRent : rentMap.entrySet()) {
                    Map<String, Object> valueRent = (Map<String, Object>) entryRent.getValue();

                    if (valueRent.get("roomId").toString().equals(valueRoom.get("roomId").toString())) {
                        chkRe++;
                        Timestamp timeStampGet = (Timestamp) valueRent.get("dateStart");
                        Date dateStartRent = timeStampGet.toDate();

                        Timestamp timeStampEnd = (Timestamp) valueRent.get("dateEnd");
                        Date dateEndRent = timeStampEnd.toDate();
                        if (dateStartChoose.getTimeInMillis() >= dateStartRent.getTime()&&dateEndChoose.getTimeInMillis()<=dateEndRent.getTime()) {
                            Log.d("CHKBUSY", "in bs 1 " + valueRoom.get("roomName").toString());
                            numBusy++;
                        }else if((dateStartChoose.getTimeInMillis() >= dateStartRent.getTime()&&dateStartChoose.getTimeInMillis()<=dateEndRent.getTime())&&dateEndChoose.getTimeInMillis()>=dateEndRent.getTime()){
                            Log.d("CHKBUSY", "in bs 2 " + valueRoom.get("roomName").toString());
                            numBusy++;
                        }else if(dateStartChoose.getTimeInMillis()<dateStartRent.getTime()&&(dateEndChoose.getTimeInMillis()>=dateStartRent.getTime()&&dateEndChoose.getTimeInMillis()<=dateEndRent.getTime())){
                            Log.d("CHKBUSY", "in bs 3 " + valueRoom.get("roomName").toString());
                            numBusy++;
                        }else{
                            Log.d("CHKBUSY", "in free " + valueRoom.get("roomName").toString());
                        }

                    }
                    pos++;
                }
                Log.d("CHKBUSY",valueRoom.get("roomName").toString()+" numIn:"+numIn+" , re:"+chkRe );
                if(chkRe==0){
                    roomModelViewArrayList.add(roomModelView);
                    numRoomFree++;
                }else if(numBusy==0){
                    roomModelViewArrayList.add(roomModelView);
                    numRoomFree++;
                }
            }


        }
        if(numRoomFree>0){
            roomsAdapter = new RoomSearchAdapter(this, roomModelViewArrayList, new RoomSearchAdapter.OnClickChoose() {
                @Override
                public void OnClickChoose(int position, String statusClick, RoomModelSearch roomModel) {
                    MyApplication.setDateStartChoose(dateStartChoose);
                    MyApplication.setDateEndChoose(dateEndChoose);
                    Intent gotosettingroom = new Intent(FindRoomActivity.this, SettingRoomActivity.class);
                    gotosettingroom.putExtra("roomKey", roomModel.getRoomId());
                    gotosettingroom.putExtra("statusSave", "search");
                    startActivityForResult(gotosettingroom, 1);
                }
            });
            view_room_all.setAdapter(roomsAdapter);
            view_room_all.setVisibility(View.VISIBLE);
            contEmp.setVisibility(View.GONE);
        }else{
            contEmp.setVisibility(View.VISIBLE);
            view_room_all.setVisibility(View.GONE);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("CHKRES", "requestCode : " + requestCode + " resultCode: " + resultCode);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {

            String rentIdGet = data.getStringExtra("rentId");
            Intent returnIntent = new Intent();
            returnIntent.putExtra("rentId", rentIdGet);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();

        }
    }
}