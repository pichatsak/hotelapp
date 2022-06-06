package com.devpos.hotelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.devpos.hotelapp.FirebaseModels.CateRooms;
import com.devpos.hotelapp.FirebaseModels.Rooms;
import com.devpos.hotelapp.models.CateRoomModels;
import com.devpos.hotelapp.models.RoomModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class addmenuroomActivity extends AppCompatActivity {
    private ImageView backhome;
    private Spinner cateChoose;
    private TextView addRoom;
    private TextInputEditText nameRoom,priceRoom;
    private ArrayList<String> mCate = new ArrayList<String>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<CateRoomModels> cateRoomModelsArrayList = new ArrayList<>();
    private String keyCate="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmenuroom);
        cateChoose = findViewById(R.id.cateChoose);
        nameRoom = findViewById(R.id.nameRoom);
        backhome = findViewById(R.id.backhome);
        priceRoom = findViewById(R.id.priceRoom);
        addRoom = findViewById(R.id.addRoom);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            keyCate = bundle.getString("cateKey");
        }

        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRoom();
            }
        });

        getCateChoose();
    }

    private void saveRoom() {
        if(nameRoom.getText().toString().isEmpty()){
            Toast.makeText(this, "กรุณากรอกชื่อห้อง", Toast.LENGTH_SHORT).show();
        }else if(priceRoom.getText().toString().isEmpty()){
            Toast.makeText(this, "กรุณากรอกราคาห้อง", Toast.LENGTH_SHORT).show();
        }else if(cateChoose.getSelectedItemPosition()==0){
            Toast.makeText(this, "กรุณาเลือกหมวดหมู่ห้อง", Toast.LENGTH_SHORT).show();
        }else{
            db.collection("rooms")
                    .whereEqualTo("userId", MyApplication.getUser_id())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult().size()>0){
                                    db.collection("rooms")
                                            .whereEqualTo("userId", MyApplication.getUser_id())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            String lastId = document.getId();
                                                            String roomNewId = UUID.randomUUID().toString();
                                                            final HashMap<String, Object> newRoom = new HashMap<>();
                                                            newRoom.put("roomName", nameRoom.getText().toString());
                                                            newRoom.put("roomId", roomNewId);
                                                            newRoom.put("cateId", cateRoomModelsArrayList.get(cateChoose.getSelectedItemPosition()).getCateKey());
                                                            newRoom.put("statusRoom", "free");
                                                            newRoom.put("dateStart", 0);
                                                            newRoom.put("dateEnd", 0);
                                                            newRoom.put("roomPrice", Integer.valueOf(priceRoom.getText().toString()));
                                                            db.collection("rooms").document(lastId).update("listRoom."+roomNewId,newRoom);
                                                            Intent returnIntent = new Intent();
                                                            setResult(Activity.RESULT_OK,returnIntent);
                                                            finish();
                                                        }
                                                    } else {
                                                        Log.d("CHKDB", "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });
                                }else{
                                    Rooms rooms = new Rooms();
                                    rooms.setUserId(MyApplication.getUser_id());
                                    db.collection("rooms")
                                            .add(rooms)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    String lastId = documentReference.getId();
                                                    String roomNewId = UUID.randomUUID().toString();
                                                    final HashMap<String, Object> newRoom = new HashMap<>();
                                                    newRoom.put("roomName", nameRoom.getText().toString());
                                                    newRoom.put("roomId", roomNewId);
                                                    newRoom.put("cateId", cateRoomModelsArrayList.get(cateChoose.getSelectedItemPosition()).getCateKey());
                                                    newRoom.put("statusRoom", "free");
                                                    newRoom.put("dateStart", 0);
                                                    newRoom.put("dateEnd", 0);
                                                    newRoom.put("roomPrice", Integer.valueOf(priceRoom.getText().toString()));
                                                    db.collection("rooms").document(lastId).update("listRoom."+roomNewId,newRoom);
                                                    Intent returnIntent = new Intent();
                                                    setResult(Activity.RESULT_OK,returnIntent);
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
                            }
                        }
                    });
        }
    }

    private void getCateChoose() {
        db.collection("cate_rooms")
                .whereEqualTo("userId", MyApplication.getUser_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> friendsMap = (Map<String, Object>) document.getData().get("listCate");
                                CateRoomModels cateRoomModelsF = new CateRoomModels();
                                cateRoomModelsF.setCateName("");
                                cateRoomModelsF.setCateKey("");
                                cateRoomModelsArrayList.add(cateRoomModelsF);
                                mCate.add("เลือกหมวดหมู่ห้อง");
                                for (Map.Entry<String, Object> entry : friendsMap.entrySet()) {
                                    String key = entry.getKey();
                                    Map<String, Object> value = (Map<String, Object>) entry.getValue();
                                    CateRoomModels cateRoomModels = new CateRoomModels();
                                    cateRoomModels.setCateName(value.get("cateName").toString());
                                    cateRoomModels.setCateKey(key);
                                    cateRoomModelsArrayList.add(cateRoomModels);
                                    mCate.add(value.get("cateName").toString());
                                }
                                ArrayAdapter<String> adapterThai = new ArrayAdapter<String>(addmenuroomActivity.this,android.R.layout.simple_dropdown_item_1line, mCate);
                                cateChoose.setAdapter(adapterThai);
                                int iLoop=0;
                                for (CateRoomModels cateRoomModelGet : cateRoomModelsArrayList) {
                                    Log.d("CHKSET","list : "+cateRoomModelGet.getCateKey()+" => "+keyCate);
                                    if(cateRoomModelGet.getCateKey().equals(keyCate)){
                                        cateChoose.setSelection(iLoop);
                                    }
                                    iLoop++;
                                }
                            }
                        } else {
                            Log.d("CHKDB", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}