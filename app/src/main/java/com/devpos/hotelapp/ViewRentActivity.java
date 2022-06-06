package com.devpos.hotelapp;

import static com.google.firebase.firestore.DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devpos.hotelapp.adaptors.PaysViewAdapter;
import com.devpos.hotelapp.adaptors.ServiceAdapter;
import com.devpos.hotelapp.adaptors.ServiceViewAdapter;
import com.devpos.hotelapp.models.PayModel;
import com.devpos.hotelapp.models.ServiceModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ViewRentActivity extends AppCompatActivity {
    private String keyRent = "";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final static String KEY_LOG = "ViewRent";
    private final static int LAUNCH_EDIT = 1;
    private ImageView backhome;
    private TextView nameRent, personRent, dateStart, dateEnd, numDay;
    private ArrayList<ServiceModel> serviceModelList = new ArrayList<>();
    private ArrayList<PayModel> payModelArrayList = new ArrayList<>();
    private RecyclerView viewSevice,viewPay;
    private LinearLayout contEmptyService;
    private TextView totalFinal,tvRoom,tvPriceRoom,tvServicePrice;
    private int totalRent =0;
    private int totalService =0;
    private int totalRoom =0;
    private LinearLayout goEdit;
    private String roomKey = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rent);
        tvRoom = findViewById(R.id.tvRoom);
        tvServicePrice = findViewById(R.id.tvServicePrice);
        tvPriceRoom = findViewById(R.id.tvPriceRoom);
        backhome = findViewById(R.id.backhome);
        nameRent = findViewById(R.id.nameRent);
        personRent = findViewById(R.id.personRent);
        dateStart = findViewById(R.id.dateStart);
        dateEnd = findViewById(R.id.dateEnd);
        viewSevice = findViewById(R.id.viewSevice);
        totalFinal = findViewById(R.id.totalFinal);
        goEdit = findViewById(R.id.goEdit);
        viewPay = findViewById(R.id.viewPay);
        contEmptyService = findViewById(R.id.contEmptyService);
        numDay = findViewById(R.id.numDay);
        Log.d(KEY_LOG, "keyRent : " + MyApplication.getRentIdViewCur());
        keyRent = MyApplication.getRentIdViewCur();
        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getDataShow();
        goEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotosettingroom = new Intent(ViewRentActivity.this, SettingRoomActivity.class);
                gotosettingroom.putExtra("roomKey", roomKey);
                gotosettingroom.putExtra("statusSave", "edit");
                gotosettingroom.putExtra("rentId", keyRent);
                startActivityForResult(gotosettingroom, LAUNCH_EDIT);
            }
        });
    }

    private void getDataShow() {
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
                                    Map<String, Object> value = (Map<String, Object>) friendsMap.get(keyRent);
                                    nameRent.setText(value.get("rentName").toString());
                                    personRent.setText(value.get("rentPerson").toString() + " คน");
                                    roomKey = value.get("roomId").toString();
                                    Timestamp timestamp = (Timestamp) value.get("dateStart");
                                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy HH:mm", Locale.US);
                                    String dateStartTxt = df.format(timestamp.toDate());

                                    Timestamp timestampEnd = (Timestamp) value.get("dateEnd");
                                    String dateEndTxt = df.format(timestampEnd.toDate());

                                    dateStart.setText(dateStartTxt);
                                    dateEnd.setText(dateEndTxt);
                                    numDay.setText(value.get("rentDay").toString()+" วัน");

                                    Gson gson = new Gson();
                                    Type type = new TypeToken<ArrayList<ServiceModel>>() {}.getType();
                                    serviceModelList = gson.fromJson(value.get("listService").toString(), type);
                                    if(serviceModelList.size()==0){
                                        contEmptyService.setVisibility(View.VISIBLE);
                                    }else{
                                        contEmptyService.setVisibility(View.GONE);
                                    }
                                    setShowService();
                                    totalService =0;
                                    for (ServiceModel serviceModel : serviceModelList){
                                        totalService += serviceModel.getPrice();
                                    }
                                    totalRoom = Integer.valueOf(value.get("totalRentRoom").toString());
                                    int totalAll = totalService+totalRoom;
                                    tvRoom.setText("ค่าห้อง X "+value.get("rentDay").toString()+" วัน");
                                    tvPriceRoom.setText(totalRoom+".-");
                                    totalFinal.setText(totalAll+".-");
                                    tvServicePrice.setText(totalService+".-");

                                    Type type2 = new TypeToken<ArrayList<PayModel>>() {}.getType();
                                    payModelArrayList = gson.fromJson(value.get("listPay").toString(), type2);
                                    setShowPay();

                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("CHKRES", "requestCode : " + requestCode + " resultCode: " + resultCode);
        if (resultCode == Activity.RESULT_OK && requestCode == LAUNCH_EDIT) {
            Log.d("CHKRES", "success");
            getDataShow();
        }
    }

    private void setShowPay() {
        viewPay.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewPay.setLayoutManager(linearLayoutManager);
        PaysViewAdapter paysViewAdapter = new PaysViewAdapter(this, payModelArrayList, new PaysViewAdapter.OnClickChoose() {
            @Override
            public void OnClickChoose(int position, String keyCate) {

            }
        });
        viewPay.setAdapter(paysViewAdapter);
    }

    private void setShowService() {
        viewSevice.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewSevice.setLayoutManager(linearLayoutManager);
        ServiceViewAdapter serviceAdapter = new ServiceViewAdapter(this, serviceModelList, new ServiceViewAdapter.OnClickChoose() {
            @Override
            public void OnClickChoose(int position, String keyCate) {

            }
        });
        viewSevice.setAdapter(serviceAdapter);
    }
}