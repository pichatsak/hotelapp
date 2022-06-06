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
import java.util.UUID;

public class addpageroomActivity extends AppCompatActivity {

    private ImageView backhome;
    private TextInputEditText nameCate;
    private TextView addCate;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpageroom);
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
    }

    private void setSaveCate() {
        if (nameCate.getText().toString().isEmpty()) {
            Toast.makeText(this, "กรุณากรอกชื่อหมวดหมู่", Toast.LENGTH_SHORT).show();
        } else {
            db.collection("cate_rooms")
                    .whereEqualTo("userId", MyApplication.getUser_id())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() > 0) {
                                    db.collection("cate_rooms")
                                            .whereEqualTo("userId", MyApplication.getUser_id())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            String getId = document.getId();
                                                            String cateNewId = UUID.randomUUID().toString();
                                                            final HashMap<String, Object> newCate = new HashMap<>();
                                                            newCate.put("cateName", nameCate.getText().toString());
                                                            newCate.put("cateId", cateNewId);
                                                            db.collection("cate_rooms").document(getId).update("listCate."+cateNewId,newCate);
                                                            Intent returnIntent = new Intent();
                                                            setResult(Activity.RESULT_OK,returnIntent);
                                                            finish();
                                                        }
                                                    } else {
                                                        Log.d("CHKDB", "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });
                                } else {
                                    CateRooms cateRooms = new CateRooms();
                                    cateRooms.setUserId(MyApplication.getUser_id());
                                    db.collection("cate_rooms")
                                            .add(cateRooms)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    String lastId = documentReference.getId();
                                                    String cateNewId = UUID.randomUUID().toString();
                                                    final HashMap<String, Object> newCate = new HashMap<>();
                                                    newCate.put("cateName", nameCate.getText().toString());
                                                    newCate.put("cateId", cateNewId);
                                                    db.collection("cate_rooms").document(lastId).update("listCate."+cateNewId,newCate);
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
}