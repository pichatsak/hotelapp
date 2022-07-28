package com.devpos.hotelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.devpos.hotelapp.FirebaseModels.Rents;
import com.devpos.hotelapp.FirebaseModels.Rooms;
import com.devpos.hotelapp.adaptors.PaysAdapter;
import com.devpos.hotelapp.adaptors.RoomSearchAdapter;
import com.devpos.hotelapp.adaptors.ServiceAdapter;
import com.devpos.hotelapp.models.DataCheck;
import com.devpos.hotelapp.models.PayModel;
import com.devpos.hotelapp.models.RoomModel;
import com.devpos.hotelapp.models.RoomModelSearch;
import com.devpos.hotelapp.models.ServiceModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SettingRoomActivity extends AppCompatActivity {

    private ImageView addOrther;
    private ImageView closedialog;
    private ImageView addBin;
    private ImageView backhome;
    private TextView showNameRoom;
    private String KEY_ROOM = "";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextInputEditText totalRentRoom, roomPriceDay;
    private EditText edNumDay;
    private int numDayRent = 1;
    private int priceRoom = 0;
    private int TotalRoom = 0;
    private int totalRentFinal = 0;
    private int totalPay = 0;

    private LinearLayout btnDel, btnPlus;
    private String edOld = "";

    private TextView chooseDateStart, dateEndChoose;
    private Calendar date = null;
    private Calendar dateEnd = null;

    private ArrayList<ServiceModel> serviceModelArrayList = new ArrayList<>();
    private LinearLayout contSeviceEmpty;
    private RecyclerView viewService;

    private TextView showTtDay, ttTotalDayRent, ttTotalService, ttTotalFinal;

    private LinearLayout showPayEmpty;
    private RecyclerView viewPays;
    private ArrayList<PayModel> payModelArrayList = new ArrayList<>();

    private TextView saveRent;
    private TextInputEditText rentName, rentPerson;
    private String statusSave = "";
    private String rentId = "";

    Map<String, Object> cateMap = new HashMap<>();
    Map<String, Object> rentMap = new HashMap<>();
    Map<String, Object> roomMap = new HashMap<>();

    Map<String, Object> valueRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_room);

        rentName = findViewById(R.id.rentName);
        rentPerson = findViewById(R.id.rentPerson);

        addOrther = findViewById(R.id.addOrther);
        addBin = findViewById(R.id.addBin);
        backhome = findViewById(R.id.backhome);
        showNameRoom = findViewById(R.id.showNameRoom);

        totalRentRoom = findViewById(R.id.totalRentRoom);
        roomPriceDay = findViewById(R.id.roomPriceDay);
        edNumDay = findViewById(R.id.edNumDay);

        btnDel = findViewById(R.id.btnDel);
        btnPlus = findViewById(R.id.btnPlus);

        chooseDateStart = findViewById(R.id.chooseDateStart);
        dateEndChoose = findViewById(R.id.dateEndChoose);

        contSeviceEmpty = findViewById(R.id.contSeviceEmpty);
        viewService = findViewById(R.id.viewService);

        showTtDay = findViewById(R.id.showTtDay);
        ttTotalDayRent = findViewById(R.id.ttTotalDayRent);
        ttTotalService = findViewById(R.id.ttTotalService);
        ttTotalFinal = findViewById(R.id.ttTotalFinal);

        showPayEmpty = findViewById(R.id.showPayEmpty);
        viewPays = findViewById(R.id.viewPays);

        saveRent = findViewById(R.id.saveRent);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            KEY_ROOM = bundle.getString("roomKey");
            statusSave = bundle.getString("statusSave");
            if (statusSave.equals("new")) {
                saveRent.setText("ยืนยันการจอง");
            } else if (statusSave.equals("edit")) {
                rentId = bundle.getString("rentId");
                saveRent.setText("บันทึกข้อมูล");
            } else {
                saveRent.setText("ยืนยันการจอง");
            }
        }
        setClickBtnAll();
        getDataRoom();
        setBtnIncrease();
        setChooseDate();
        getListMorePrice();
        getListPay();
        setClickSave();

        if (statusSave.equals("edit")) {
            getDataEdit();
        } else if (statusSave.equals("search")) {

            date = MyApplication.getDateStartChoose();
            dateEnd = MyApplication.getDateEndChoose();
            setDataShow();
        } else {
            getCateData();
        }
    }

    private void setDataShow() {
        chooseDateStart.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date.getTime()));
        dateEndChoose.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dateEnd.getTime()));

        long millionSeconds = dateEnd.getTimeInMillis() - date.getTimeInMillis();
        long days = TimeUnit.MILLISECONDS.toDays(millionSeconds);
        int daysRounded = Math.round(days);
        edNumDay.setText("" + daysRounded);
        numDayRent = daysRounded;
    }

    private void getDataEdit() {
        db.collection("rents")
                .whereEqualTo("userId", MyApplication.getUser_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> friendsMap = (Map<String, Object>) document.getData().get("listRents");
                                if (friendsMap.size() > 0) {
                                    Map<String, Object> value = (Map<String, Object>) friendsMap.get(rentId);
                                    rentName.setText(value.get("rentName").toString());
                                    rentPerson.setText(value.get("rentPerson").toString());
                                    KEY_ROOM = value.get("roomId").toString();
                                    Timestamp timestamp = (Timestamp) value.get("dateStart");
                                    Date dateGetStart = timestamp.toDate();
                                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy HH:mm", Locale.US);
                                    String dateStartTxt = df.format(dateGetStart);
                                    chooseDateStart.setText(dateStartTxt);
                                    date = Calendar.getInstance();
                                    date.setTime(dateGetStart);
                                    Log.d("CHKDATERENT", "get date start : " + date.getTime());

                                    Timestamp timestampEnd = (Timestamp) value.get("dateEnd");
                                    priceRoom = Integer.parseInt(value.get("roomPriceDay").toString());
                                    roomPriceDay.setText(String.valueOf(priceRoom));

                                    numDayRent = Integer.parseInt(value.get("rentDay").toString());
                                    edNumDay.setText(String.valueOf(numDayRent));

                                    Gson gson = new Gson();
                                    Type type = new TypeToken<ArrayList<ServiceModel>>() {
                                    }.getType();
                                    serviceModelArrayList = gson.fromJson(value.get("listService").toString(), type);

                                    Type type2 = new TypeToken<ArrayList<PayModel>>() {
                                    }.getType();
                                    payModelArrayList = gson.fromJson(value.get("listPay").toString(), type2);

                                    setNewTotalAll();
                                    getListMorePrice();
                                    getListPay();
                                }
                            }
                        }
                    }
                });
    }

    private void setClickSave() {
        saveRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CHK_DATA_SAVE", totalPay + ".-");
                if (rentName.getText().toString().isEmpty()) {
                    rentName.setError("กรุณากรอกชื่อผู้เข้าพัก");
                    rentName.requestFocus();
                } else if (rentPerson.getText().toString().isEmpty()) {
                    rentPerson.setError("กรุณากรอกจำนวนผู้เข้าพัก");
                    rentPerson.requestFocus();
                } else if (date == null) {
                    Toast.makeText(SettingRoomActivity.this, "กรุณาเลือกวันเวลาเข้าพัก", Toast.LENGTH_SHORT).show();
                } else if (roomPriceDay.getText().toString().isEmpty()) {
                    roomPriceDay.setError("กรุณากรอกราคาห้อง");
                    roomPriceDay.requestFocus();
                } else if (totalRentRoom.getText().toString().isEmpty()) {
                    totalRentRoom.setError("กรุณากรอกยอดรวมค่าห้อง");
                    totalRentRoom.requestFocus();
                } else if (payModelArrayList.size() == 0) {
                    Toast.makeText(SettingRoomActivity.this, "กรุณาบันทึกการชำระเงิน", Toast.LENGTH_SHORT).show();
                } else if (totalPay < totalRentFinal) {
                    Toast.makeText(SettingRoomActivity.this, "ยอดรวมการชำระเงินไม่เพียงพอ", Toast.LENGTH_SHORT).show();
                } else {
                    if (statusSave.equals("new") || statusSave.equals("search")) {
                        if (isChkDate()) {
                            setSaveRent();
                        } else {
                            Toast.makeText(SettingRoomActivity.this, "ห้องนี้ไม่ว่างในช่วงเวลานี้", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (isChkDate()) {
                            setEditRent();
                        } else {
                            Toast.makeText(SettingRoomActivity.this, "ห้องนี้ไม่ว่างในช่วงเวลานี้", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
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

    }

    public boolean isChkDate() {
        boolean resData = false;
        int numRoomFree = 0;

        int pos = 1;
        int max = rentMap.size();
        int numIn = 0;
        int chkRe = 0;
        int numBusy = 0;


        for (Map.Entry<String, Object> entryRent : rentMap.entrySet()) {
            Map<String, Object> valueRent = (Map<String, Object>) entryRent.getValue();

            if (valueRent.get("roomId").toString().equals(valueRoom.get("roomId").toString())) {
                chkRe++;
                Timestamp timeStampGet = (Timestamp) valueRent.get("dateStart");
                Date dateStartRentGet = timeStampGet.toDate();

                Timestamp timeStampEnd = (Timestamp) valueRent.get("dateEnd");
                Date dateEndRentGet = timeStampEnd.toDate();

                if (date.getTimeInMillis() >= dateStartRentGet.getTime() && dateEnd.getTimeInMillis() <= dateEndRentGet.getTime()) {
                    Log.d("CHKBUSY", "in bs 1 " + valueRoom.get("roomName").toString());
                    numBusy++;
                } else if ((date.getTimeInMillis() >= dateStartRentGet.getTime() && date.getTimeInMillis() <= dateEndRentGet.getTime()) && dateEnd.getTimeInMillis() >= dateEndRentGet.getTime()) {
                    Log.d("CHKBUSY", "in bs 2 " + valueRoom.get("roomName").toString());
                    numBusy++;
                } else if (date.getTimeInMillis() < dateStartRentGet.getTime() && (dateEnd.getTimeInMillis() >= dateStartRentGet.getTime() && dateEnd.getTimeInMillis() <= dateEndRentGet.getTime())) {
                    Log.d("CHKBUSY", "in bs 3 " + valueRoom.get("roomName").toString());
                    numBusy++;
                } else {
                    Log.d("CHKBUSY", "in free " + valueRoom.get("roomName").toString());
                }

            }
            pos++;
        }
        Log.d("CHKBUSY", valueRoom.get("roomName").toString() + " numIn:" + numIn + " , re:" + chkRe);
        if (chkRe == 0) {
            numRoomFree++;
        } else if (numBusy == 0) {
            numRoomFree++;
        }


        if (numRoomFree > 0) {
            resData = true;

            Log.d("CHKBUSY", "true");
        } else {
            resData = false;
            Log.d("CHKBUSY", "false");
        }
        return resData;
    }

    private void setEditRent() {
        db.collection("rents")
                .whereEqualTo("userId", MyApplication.getUser_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String docId = document.getId();
                                db.collection("rents").document(docId).update(
                                        "listRents." + rentId + ".rentName", rentName.getText().toString(),
                                        "listRents." + rentId + ".rentPerson", rentPerson.getText().toString(),
                                        "listRents." + rentId + ".dateStart", date.getTime(),
                                        "listRents." + rentId + ".dateEnd", dateEnd.getTime(),
                                        "listRents." + rentId + ".rentDay", numDayRent,
                                        "listRents." + rentId + ".roomPriceDay", Integer.valueOf(roomPriceDay.getText().toString()),
                                        "listRents." + rentId + ".totalRentRoom", Integer.valueOf(totalRentRoom.getText().toString()),
                                        "listRents." + rentId + ".listPay", payModelArrayList,
                                        "listRents." + rentId + ".listService", serviceModelArrayList
                                );
                                Intent returnIntent = new Intent();
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            }
                        } else {
                            Log.d("CHKDB", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void setSaveRent() {
        db.collection("rents")
                .whereEqualTo("userId", MyApplication.getUser_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                db.collection("rents")
                                        .whereEqualTo("userId", MyApplication.getUser_id())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        String lastId = document.getId();
                                                        String rentNewId = UUID.randomUUID().toString();
                                                        final HashMap<String, Object> newRent = new HashMap<>();
                                                        newRent.put("rentName", rentName.getText().toString());
                                                        newRent.put("rentPerson", rentPerson.getText().toString());
                                                        newRent.put("dateStart", date.getTime());
                                                        newRent.put("dateEnd", dateEnd.getTime());
                                                        newRent.put("rentDay", numDayRent);
                                                        newRent.put("roomId", KEY_ROOM);
                                                        newRent.put("dateCreate", FieldValue.serverTimestamp());
                                                        newRent.put("status", "renting");
                                                        newRent.put("roomPriceDay", Integer.valueOf(roomPriceDay.getText().toString()));
                                                        newRent.put("totalRentRoom", Integer.valueOf(totalRentRoom.getText().toString()));
                                                        db.collection("rents").document(lastId).update("listRents." + rentNewId, newRent);
                                                        db.collection("rents").document(lastId).update("listRents." + rentNewId + ".listPay", payModelArrayList);
                                                        db.collection("rents").document(lastId).update("listRents." + rentNewId + ".listService", serviceModelArrayList);
                                                        Intent returnIntent = new Intent();
                                                        returnIntent.putExtra("rentId", rentNewId);
                                                        setResult(Activity.RESULT_OK, returnIntent);
                                                        finish();
                                                    }
                                                } else {
                                                    Log.d("CHKDB", "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });
                            } else {
                                Rents rents = new Rents();
                                rents.setUserId(MyApplication.getUser_id());
                                db.collection("rents")
                                        .add(rents)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                String lastId = documentReference.getId();
                                                String rentNewId = UUID.randomUUID().toString();
                                                final HashMap<String, Object> newRent = new HashMap<>();
                                                newRent.put("rentName", rentName.getText().toString());
                                                newRent.put("rentPerson", rentPerson.getText().toString());
                                                newRent.put("dateStart", date.getTime());
                                                newRent.put("dateEnd", dateEnd.getTime());
                                                newRent.put("rentDay", numDayRent);
                                                newRent.put("roomId", KEY_ROOM);
                                                newRent.put("dateCreate", FieldValue.serverTimestamp());
                                                newRent.put("status", "renting");
                                                newRent.put("roomPriceDay", Integer.valueOf(roomPriceDay.getText().toString()));
                                                newRent.put("totalRentRoom", Integer.valueOf(totalRentRoom.getText().toString()));
                                                db.collection("rents").document(lastId).update("listRents." + rentNewId, newRent);
                                                db.collection("rents").document(lastId).update("listRents." + rentNewId + ".listPay", payModelArrayList);
                                                db.collection("rents").document(lastId).update("listRents." + rentNewId + ".listService", serviceModelArrayList);
                                                Intent returnIntent = new Intent();
                                                returnIntent.putExtra("rentId", rentNewId);
                                                setResult(Activity.RESULT_OK, returnIntent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("CHKDB", "Error adding document", e);
                                            }
                                        });
                            }
                        } else {
                            Log.d("CHKDB", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getListPay() {
        if (payModelArrayList.size() == 0) {
            showPayEmpty.setVisibility(View.VISIBLE);
        } else {
            showPayEmpty.setVisibility(View.GONE);
        }
        totalPay = 0;
        for (PayModel objects : payModelArrayList) {
            totalPay += objects.getPricePay();
        }
        viewPays.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewPays.setLayoutManager(linearLayoutManager);
        PaysAdapter paysAdapter = new PaysAdapter(this, payModelArrayList, new PaysAdapter.OnClickChoose() {
            @Override
            public void OnClickChoose(int position, String keyCate) {
                payModelArrayList.remove(position);
                getListPay();
            }
        });
        viewPays.setAdapter(paysAdapter);
        setNewTotalAll();
    }

    private void getListMorePrice() {
        if (serviceModelArrayList.size() == 0) {
            contSeviceEmpty.setVisibility(View.VISIBLE);
        } else {
            contSeviceEmpty.setVisibility(View.GONE);
        }
        viewService.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewService.setLayoutManager(linearLayoutManager);
        ServiceAdapter serviceAdapter = new ServiceAdapter(this, serviceModelArrayList, new ServiceAdapter.OnClickChoose() {
            @Override
            public void OnClickChoose(int position, String keyCate) {
                serviceModelArrayList.remove(position);
                getListMorePrice();
            }
        });
        viewService.setAdapter(serviceAdapter);
        setNewTotalAll();
    }

    private void setChooseDate() {
        chooseDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar currentDate = Calendar.getInstance();
                date = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(SettingRoomActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                        date.set(year, monthOfYear, dayOfMonth);
                        new TimePickerDialog(SettingRoomActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                date.set(Calendar.MINUTE, minute);
                                // Log.v(TAG, "The choosen one " + date.getTime());
                                // Toast.makeText(getContext(),"The choosen one " + date.getTime(),Toast.LENGTH_SHORT).show();
                                chooseDateStart.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date.getTime()));
                                setNewDateEnd();
                            }
                        }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();

                    }
                }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
                datePickerDialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());
                datePickerDialog.show();
            }
        });
    }

    private void setNewDateEnd() {
        Date dtStartDate = date.getTime();
        dateEnd = Calendar.getInstance();
        dateEnd.setTime(dtStartDate);
        dateEnd.add(Calendar.DATE, numDayRent);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dt = sdf.format(dateEnd.getTime());
        dateEndChoose.setText(dt);
    }

    private void setBtnIncrease() {
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numDayRent = numDayRent + 1;
                edNumDay.setText(String.valueOf(numDayRent));
                setNewTotalAll();
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numDayRent - 1 != 0) {
                    numDayRent = numDayRent - 1;
                    edNumDay.setText(String.valueOf(numDayRent));
                    setNewTotalAll();
                } else {
                    Toast.makeText(SettingRoomActivity.this, "ไม่สามารถลดได้มากกว่านี้", Toast.LENGTH_SHORT).show();
                }
            }
        });
        edNumDay.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Log.d("CHKTXT", "after : " + s.toString());
                numDayRent = Integer.valueOf(s.toString());
                if (date != null) {
                    setNewDateEnd();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("CHKTXT", "before : " + s.toString());
                edOld = s.toString();
                edNumDay.setSelection(edNumDay.getText().length());
                numDayRent = Integer.valueOf(edOld);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("CHKTXT", "changed : " + s.toString());
                if (s.toString().isEmpty()) {
                    edNumDay.setText(edOld);
                    edNumDay.setSelection(edNumDay.getText().length());
                    numDayRent = Integer.valueOf(edOld);
                } else if (s.toString().equals("0")) {
                    edNumDay.setText(edOld);
                    edNumDay.setSelection(edNumDay.getText().length());
                    numDayRent = Integer.valueOf(edOld);
                }

            }
        });
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
                                Map<String, Object> value = (Map<String, Object>) friendsMap.get(KEY_ROOM);
                                valueRoom = value;
                                roomPriceDay.setText(value.get("roomPrice").toString());
                                priceRoom = Integer.valueOf(value.get("roomPrice").toString());
                                if (statusSave.equals("edit")) {
                                    showNameRoom.setText("แก้ไขข้อมูลการจองห้อง " + value.get("roomName").toString());
                                } else {
                                    showNameRoom.setText("จองห้อง " + value.get("roomName").toString());
                                }
                                setNewTotalAll();
                            }
                        } else {
                            Log.d("CHKDB", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void setNewTotalAll() {

        showTtDay.setText("ค่าห้อง X " + numDayRent + " วัน");
        int newTotalRoom = priceRoom * numDayRent;
        TotalRoom = newTotalRoom;
        ttTotalDayRent.setText(TotalRoom + ".-");
        if (serviceModelArrayList.size() == 0) {
            totalRentFinal = newTotalRoom;
            totalRentRoom.setText(String.valueOf(TotalRoom));
            ttTotalFinal.setText(totalRentFinal + ".-");
            ttTotalService.setText("0.-");
        } else {
            int getSvTotal = 0;
            for (ServiceModel objects : serviceModelArrayList) {
                getSvTotal += objects.getPrice();
            }
            totalRentFinal = newTotalRoom + getSvTotal;
            totalRentRoom.setText(String.valueOf(TotalRoom));
            ttTotalService.setText(getSvTotal + ".-");
            ttTotalFinal.setText(totalRentFinal + ".-");
        }

    }

    public void setClickBtnAll() {
        addOrther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(SettingRoomActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_orther);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                Display display = ((WindowManager) getSystemService(SettingRoomActivity.this.WINDOW_SERVICE)).getDefaultDisplay();
                int width = display.getWidth();
                int height = display.getHeight();
                Log.v("width", width + "");
                dialog.getWindow().setLayout((6 * width) / 7, (6 * height) / 9);
                TextInputEditText nameFillSevice = dialog.findViewById(R.id.nameFillSevice);
                TextInputEditText priceFillSevice = dialog.findViewById(R.id.priceFillSevice);
                TextView saveService = dialog.findViewById(R.id.saveService);
                saveService.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (nameFillSevice.getText().toString().isEmpty() || priceFillSevice.getText().toString().isEmpty()) {
                            Toast.makeText(SettingRoomActivity.this, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
                        } else {
                            ServiceModel serviceModel = new ServiceModel();
                            serviceModel.setNameService(nameFillSevice.getText().toString());
                            serviceModel.setPrice(Integer.valueOf(priceFillSevice.getText().toString()));
                            serviceModelArrayList.add(serviceModel);
                            getListMorePrice();
                            dialog.dismiss();
                        }
                    }
                });

                closedialog = dialog.findViewById(R.id.closedialog);
                closedialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });


        addBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(SettingRoomActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_check_bin);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                Display display = ((WindowManager) getSystemService(SettingRoomActivity.this.WINDOW_SERVICE)).getDefaultDisplay();
                int width = display.getWidth();
                int height = display.getHeight();
                Log.v("width", width + "");
                dialog.getWindow().setLayout((6 * width) / 7, (6 * height) / 9);

                Spinner spinPayType = dialog.findViewById(R.id.spinPayType);
                TextInputEditText pricePayFill = dialog.findViewById(R.id.pricePayFill);
                TextView savePay = dialog.findViewById(R.id.savePay);

                ArrayList<String> mPayType = new ArrayList<String>();
                mPayType.add("เงินสด");
                mPayType.add("เงินโอน");
                mPayType.add("เครดิต");
                ArrayAdapter<String> adapterThai = new ArrayAdapter<String>(dialog.getContext(), android.R.layout.simple_dropdown_item_1line, mPayType);
                spinPayType.setAdapter(adapterThai);

                savePay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (pricePayFill.getText().toString().isEmpty()) {
                            Toast.makeText(SettingRoomActivity.this, "กรุณากรอกยอดเงิน", Toast.LENGTH_SHORT).show();
                        } else {
                            PayModel payModel = new PayModel();
                            payModel.setPricePay(Integer.valueOf(pricePayFill.getText().toString()));
                            payModel.setTypePay(mPayType.get(spinPayType.getSelectedItemPosition()));
                            payModelArrayList.add(payModel);
                            dialog.dismiss();
                            getListPay();
                        }
                    }
                });

                closedialog = dialog.findViewById(R.id.closedialog);
                closedialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });


        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}