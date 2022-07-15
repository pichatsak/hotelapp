package com.devpos.hotelapp;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.devpos.hotelapp.data_registor.data_registor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

public class registorActivity extends AppCompatActivity {

    private TextView login;
    private TextInputEditText name;
    private TextInputEditText listhotel;
    private TextInputEditText mail;
    private TextInputEditText phone;
    private TextInputEditText password_regis;
    private TextInputEditText password_regis_confirm;
    private TextView registor;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView gologin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registor);


        mAuth = FirebaseAuth.getInstance();
        login = findViewById(R.id.login);
        name = findViewById(R.id.name);
        listhotel = findViewById(R.id.listhotel);
        mail = findViewById(R.id.mail);
        phone = findViewById(R.id.phone);
        password_regis = findViewById(R.id.password_regis);
        password_regis_confirm = findViewById(R.id.password_regis_confirm);
        registor = findViewById(R.id.registor);


        //กลับสู่หน้า login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //เงื่อนไขกรอกข้อมูลให้ครบ

        registor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().isEmpty()) {
                    Toast.makeText(registorActivity.this, "กรุณากรอกชื่อผู้ใช้", Toast.LENGTH_SHORT).show();
                } else if (listhotel.getText().toString().isEmpty()) {
                    Toast.makeText(registorActivity.this, "กรุณากรอกรายชื่อโรงแรม", Toast.LENGTH_SHORT).show();
                } else if (mail.getText().toString().isEmpty()) {
                    Toast.makeText(registorActivity.this, "กรุณากรอกอีเมล", Toast.LENGTH_SHORT).show();
                } else if (phone.getText().toString().isEmpty()) {
                    Toast.makeText(registorActivity.this, "กรุณากรอกเบอร์โทรศัพท์", Toast.LENGTH_SHORT).show();
                } else if (password_regis.getText().toString().isEmpty()) {
                    Toast.makeText(registorActivity.this, "กรุณากรอกรหัสผ่าน", Toast.LENGTH_SHORT).show();
                } else if (password_regis_confirm.getText().toString().isEmpty()) {
                    Toast.makeText(registorActivity.this, "กรุณากรอกยืนยันรหัสผ่าน", Toast.LENGTH_SHORT).show();
                } else if (!password_regis_confirm.getText().toString().equals(password_regis.getText().toString())) {
                    Toast.makeText(registorActivity.this, "กรุณากรอกรหัสผ่านให้ตรงกัน", Toast.LENGTH_SHORT).show();
                } else {
                    registor();
                    //ไดอาร็อกเด้งสมัครเสร็จสิ้น
                }
            }
        });

    }


    //สมัครสมาชิกเก็บข้อมูล ทั้ง2 แบบ ในไฟล์เบส
    public void registor() {

        String getName = name.getText().toString();
        String getListhotel = listhotel.getText().toString();
        String getMail = mail.getText().toString();
        String getPhone = phone.getText().toString();
        String getPassword_regis = password_regis.getText().toString();

        db.collection("users")
                .whereEqualTo("mail", getMail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().size()>0){
                                Toast.makeText(registorActivity.this, "อีเมลนี้เคยลงทะเบียนไว้แล้ว", Toast.LENGTH_SHORT).show();
                            }else{
                                mAuth.createUserWithEmailAndPassword(getMail, getPassword_regis).addOnCompleteListener(registorActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d("JuiKee", "createUserWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Calendar calendar = Calendar.getInstance();
                                            data_registor data = new data_registor();
                                            data.setName(getName);
                                            data.setListhotel(getListhotel);
                                            data.setMail(getMail);
                                            data.setPhone(getPhone);
                                            data.setPassword_regis(getPassword_regis);
                                            data.setId(user.getUid());
                                            data.setStatus("wait");
                                            data.setDateCreate(calendar.getTime());
                                            data.setDateStartUse(calendar.getTime());
                                            data.setDateEndUse(calendar.getTime());
                                            db.collection("users")
                                                    .add(data)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Log.d("JuiKee", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                            final Dialog dialog = new Dialog(registorActivity.this);
                                                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                            dialog.setCancelable(false);
                                                            dialog.setContentView(R.layout.dialog_registor_success);
                                                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                                            gologin = dialog.findViewById(R.id.gologin);
                                                            mAuth.signOut();
                                                            dialog.show();
                                                            gologin.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    dialog.dismiss();
                                                                    finish();
                                                                }
                                                            });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("JuiKee", "Error adding document", e);
                                                            Toast.makeText(registorActivity.this, "เกิดข้อผิดพลาดลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w("JuiKee", "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(registorActivity.this, "เกิดข้อผิดพลาดลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });




    }


}