package com.devpos.hotelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity {

    private TextView registor;
    private TextView forgetpasswoord;
    private TextInputEditText mail_login;
    private TextInputEditText password_login;
    private TextView login;
    private FirebaseAuth mAuth;

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
                if (mail_login.getText().toString().isEmpty()){
                    Toast.makeText(loginActivity.this, "กรุณากรอกอีเมล", Toast.LENGTH_SHORT).show();
                }
                else if (password_login.getText().toString().isEmpty()){
                    Toast.makeText(loginActivity.this, "กรุณากรอกรหัสผ่าน", Toast.LENGTH_SHORT).show();
                }
                else {
                    Login();

                }
            }
        });



    }

    //เข้าสู่ระบบไฟล์เบส
    public void Login(){

        String getMail = mail_login.getText().toString();
        String getPassword = password_login.getText().toString();
        mAuth.signInWithEmailAndPassword(getMail,getPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("jui", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //      boolean getstatus = user.getProviderData().get(0).isEmailVerified();

//                            if(user.getProviderData().get(0).isEmailVerified()){
//                                Log.d("CHKEMAIL","yes");
//
//                                Intent jui3 = new Intent(MainActivity.this, momActivity.class);
//                                startActivity(jui3);
//                            }else{
//                                Log.d("CHKEMAIL","no");
//                                Toast.makeText(MainActivity.this, "กรุณายืนยันอีเมลก่อนเข้าสู่ระบบ", Toast.LENGTH_SHORT).show();
//                            }


                            Intent gologin = new Intent(loginActivity.this,MainActivity.class);
                            startActivity(gologin);


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("jui", "signInWithEmail:failure", task.getException());
                            Toast.makeText(loginActivity.this, "รหัสสมาชิกไม่ถูกต้อง", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }


}