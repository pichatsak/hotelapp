package com.devpos.hotelapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devpos.hotelapp.data_registor.data_registor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

public class ChangPasswordActivity extends AppCompatActivity {

    private ImageView backsetting;
    private TextInputEditText newPassword;
    private TextInputEditText ConfirmNewPassword;
    private TextView ok;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        backsetting = findViewById(R.id.backsetting);
        newPassword = findViewById(R.id.newPassowrd);
        ConfirmNewPassword = findViewById(R.id.ConfirmNewPassword);
        ok = findViewById(R.id.ok);


        backsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newPassword.getText().toString().isEmpty()) {
                    Toast.makeText(ChangPasswordActivity.this, "กรุณากรอกรหัสผ่านใหม่", Toast.LENGTH_SHORT).show();
                } else if (ConfirmNewPassword.getText().toString().isEmpty()) {
                    Toast.makeText(ChangPasswordActivity.this, "กรุณากรอกยืนยันรหัสผ่านใหม่", Toast.LENGTH_SHORT).show();
                } else if (newPassword.getText().toString().equals(ConfirmNewPassword.getText().toString())) {

                    UpDatePassword();


                } else {
                    Toast.makeText(ChangPasswordActivity.this, "กรุณากรอกรหัสผ่านให้ตรงกัน", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private void UpDatePassword() {

        String getNewPassword = newPassword.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String newPassword = getNewPassword;

        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("CHKIDS", user.getUid());

                            db.collection("users")
                                    .whereEqualTo("id", user.getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    db.collection("users").document(document.getId()).update("password_regis",getNewPassword);
                                                    Toast.makeText(ChangPasswordActivity.this, "เปลี่ยนรหัสผ่านเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });

                        }
                    }
                });

    }


}