package com.devpos.hotelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.devpos.hotelapp.dialog.DialogAlert;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;

public class loginActivity extends AppCompatActivity {

    private TextView registor;
    private TextView forgetpasswoord;
    private TextInputEditText mail_login;
    private TextInputEditText password_login;
    private TextView login;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mail_login = findViewById(R.id.mail_login);
        password_login = findViewById(R.id.password_login);
        login = findViewById(R.id.login);


        mAuth = FirebaseAuth.getInstance();


        //สมัครสมาชิก
        registor = findViewById(R.id.registor);
        registor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent juikee = new Intent(loginActivity.this, registorActivity.class);
                startActivity(juikee);
            }
        });


        //ลืมรหัสผ่าน
        forgetpasswoord = findViewById(R.id.forgetpassword);
        forgetpasswoord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent juikee1 = new Intent(loginActivity.this, forgetPasswordActivity.class);
                startActivity(juikee1);
            }
        });

        //เข้าสู่ระบบ
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mail_login.getText().toString().isEmpty()) {
                    Toast.makeText(loginActivity.this, "กรุณากรอกอีเมล", Toast.LENGTH_SHORT).show();
                } else if (password_login.getText().toString().isEmpty()) {
                    Toast.makeText(loginActivity.this, "กรุณากรอกรหัสผ่าน", Toast.LENGTH_SHORT).show();
                } else {
                    Login();

                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            db.collection("users")
                    .whereEqualTo("id", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    MyApplication.setUser_id(document.getId());
                                    Intent intent = new Intent(loginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    });
        }
    }

    //เข้าสู่ระบบไฟล์เบส
    public void Login() {

        String getMail = mail_login.getText().toString();
        String getPassword = password_login.getText().toString();
        db.collection("users")
                .whereEqualTo("mail", getMail)
                .whereEqualTo("password_regis", getPassword)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("CHKDB", document.getId() + " => " + document.getData());
                                    String getStatus = document.getData().get("status").toString();
                                    if (getStatus.equals("yes")) {


                                        Timestamp timeStart = (Timestamp) document.getData().get("dateStartUse");
                                        Timestamp timeEnd = (Timestamp) document.getData().get("dateEndUse");
                                        Calendar curDate = Calendar.getInstance();
                                        Date dateStart = timeStart.toDate();
                                        Date dateEnd = timeEnd.toDate();
                                        Date dateCur = curDate.getTime();

                                        if(dateCur.getTime()>=dateStart.getTime()&&dateCur.getTime()<=dateEnd.getTime()){
                                            MyApplication.setUser_id(document.getId());
                                            mAuth.signInWithEmailAndPassword(getMail, getPassword)
                                                    .addOnCompleteListener(loginActivity.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                // Sign in success, update UI with the signed-in user's information
                                                                Log.d("jui", "signInWithEmail:success");
                                                                FirebaseUser user = mAuth.getCurrentUser();

                                                                Intent intent = new Intent(loginActivity.this, MainActivity.class);
                                                                startActivity(intent);
                                                                finish();

                                                            } else {
                                                                // If sign in fails, display a message to the user.
                                                                Log.w("jui", "signInWithEmail:failure", task.getException());
                                                                Toast.makeText(loginActivity.this, "รหัสสมาชิกไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }else if(dateCur.getTime()>dateEnd.getTime()){
                                            String showTxt = "ผู้ใช้นี้หมดอายุการใช้งานแล้ว \n กรุณาติดต่อเจ้าหน้าที่ \n Line : @hotel \n เบอร์โทร : 085-457-5811";
                                            new DialogAlert(loginActivity.this,showTxt,"warning").openDialog();
                                        }

                                    } else {
                                        String showTxt = "บัญชีนี้ยังไม่ได้รับการอนุมัติ \n กรุณาติดต่อเจ้าหน้าที่ \n Line : @hotel \n เบอร์โทร : 085-457-5811";
                                        new DialogAlert(loginActivity.this,showTxt,"error").openDialog();
                                    }
                                }
                            } else {
                                new DialogAlert(loginActivity.this,"อีเมลหรือรหัสผ่านไม่ถูกต้อง","error").openDialog();
                            }
                        } else {
                            Log.d("CHKDB", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }


}