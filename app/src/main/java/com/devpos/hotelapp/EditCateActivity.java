package com.devpos.hotelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devpos.hotelapp.FirebaseModels.CateRooms;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditCateActivity extends AppCompatActivity {
    private ImageView backhome;
    private TextInputEditText nameCate;
    private TextView addCate;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String keyCate = "";
    String keyId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cate);
        nameCate = findViewById(R.id.nameCate);
        backhome = findViewById(R.id.backhome);
        addCate = findViewById(R.id.addCate);
        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSaveCate();
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            keyCate = bundle.getString("keyCate");
        }
        getData();
    }

    private void getData() {
        db.collection("cate_rooms")
                .whereEqualTo("userId", MyApplication.getUser_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                keyId = document.getId();
                                Map<String, Object> friendsMap = (Map<String, Object>) document.getData().get("listCate");
                                Map<String, Object> value = (Map<String, Object>) friendsMap.get(keyCate);
                                nameCate.setText(value.get("cateName").toString());
                            }
                        }
                    }
                });
    }


    private void setSaveCate() {
        if (nameCate.getText().toString().isEmpty()) {
            Toast.makeText(this, "กรุณากรอกชื่อหมวดหมู่", Toast.LENGTH_SHORT).show();
        } else {
            db.collection("cate_rooms").document(keyId)
                    .update("listCate." + keyCate+".cateName", nameCate.getText().toString())
                    .addOnSuccessListener(aVoid -> {
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    });
        }
    }
}