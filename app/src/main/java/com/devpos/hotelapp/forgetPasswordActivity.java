package com.devpos.hotelapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class forgetPasswordActivity extends AppCompatActivity {

    private TextView sendmail;
    private TextView backlogin;
    private TextInputEditText mail_reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        sendmail = findViewById(R.id.sendmail);
        mail_reset = findViewById(R.id.mail_reset);


        //ลืมรหัสผ่าน ส่งไปยังเมลล์
        sendmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mail_reset.getText().toString().isEmpty()){
                    Toast.makeText(forgetPasswordActivity.this, "กรุณากรอกอีเมล", Toast.LENGTH_SHORT).show();
                }
                else {

                    reset();

                    final Dialog dialog = new Dialog(forgetPasswordActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.dialog_resetpassword);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();
                    backlogin = dialog.findViewById(R.id.backlogin);
                    backlogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            finish();
                        }
                    });

                }

            }
        });
    }

    public void reset(){

        String getMail = mail_reset.getText().toString();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = getMail;

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("JuiKee", "Email sent.");

                        }
                    }
                });

    }
}