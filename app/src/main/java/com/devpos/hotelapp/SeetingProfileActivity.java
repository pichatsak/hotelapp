package com.devpos.hotelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class SeetingProfileActivity extends AppCompatActivity {

    private ImageView backhome;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextInputEditText name,listhotel,phone;
    TextView nameUser,nameHotel;
    TextView setSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeting_profile);
        name = findViewById(R.id.name);
        listhotel = findViewById(R.id.listhotel);
        phone = findViewById(R.id.phone);
        backhome = findViewById(R.id.backhome);
        nameUser = findViewById(R.id.nameUser);
        setSave = findViewById(R.id.setSave);
        nameHotel = findViewById(R.id.nameHotel);
        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        getData();
        setSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSaveData();
            }
        });
    }

    private void setSaveData() {
        if(name.getText().toString().equals("")||phone.getText().toString().isEmpty()||listhotel.getText().toString().isEmpty()){
            Toast.makeText(this, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
        }else{
            db.collection("users").document(MyApplication.getUser_id())
                    .update(
                            "name", name.getText().toString(),
                            "listhotel", listhotel.getText().toString(),
                            "phone", phone.getText().toString())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "บันทึกข้อมูลเรียบร้อย", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void getData() {
        DocumentReference docRef = db.collection("users").document(MyApplication.getUser_id());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        name.setText(document.getData().get("name").toString());
                        listhotel.setText(document.getData().get("listhotel").toString());
                        phone.setText(document.getData().get("phone").toString());
                        nameUser.setText(document.getData().get("name").toString());
                        nameHotel.setText(document.getData().get("listhotel").toString());
                    } else {
                        Log.d("CHKDB", "No such document");
                    }
                } else {
                    Log.d("CHKDB", "get failed with ", task.getException());
                }
            }
        });
    }
}