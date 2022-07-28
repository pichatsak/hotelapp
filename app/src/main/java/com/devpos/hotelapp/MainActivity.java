package com.devpos.hotelapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devpos.hotelapp.RealmDB.PrintSetRm;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.devpos.hotelapp.databinding.ActivityMainBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private LinearLayout room1;
    private TextView cancle;
    private LinearLayout pickRoom;
    private LinearLayout SeetingProfile;
    private TextView nameUser,nameHotel;
    Realm realm = Realm.getDefaultInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
//        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_salereport)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        getSupportActionBar().hide();


        View headerViews = navigationView.getHeaderView(0);
        SeetingProfile = headerViews.findViewById(R.id.SettingProfile);
        nameUser = headerViews.findViewById(R.id.nameUser);
        nameHotel = headerViews.findViewById(R.id.nameHotel);
        SeetingProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingprofile = new Intent(MainActivity.this, SeetingProfileActivity.class);
                startActivity(settingprofile);
                drawer.close();
            }
        });


        LinearLayout editProfiles = headerViews.findViewById(R.id.editProfiles);
        editProfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingprofile = new Intent(MainActivity.this, SeetingProfileActivity.class);
                startActivity(settingprofile);
                drawer.close();
            }
        });





        room1 = findViewById(R.id.room1);
        room1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_menu);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                Display display = ((WindowManager) getSystemService(MainActivity.this.WINDOW_SERVICE)).getDefaultDisplay();
                int width = display.getWidth();
                int height = display.getHeight();
                Log.v("width", width + "");
                dialog.getWindow().setLayout((6 * width) / 9, (4 * height) / 9);

                cancle = dialog.findViewById(R.id.cancle);
                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                pickRoom = dialog.findViewById(R.id.pickRoom);
                pickRoom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Intent gotosettingroom = new Intent(MainActivity.this, SettingRoomActivity.class);
                        startActivity(gotosettingroom);
                    }
                });
            }
        });
        getDataUser();
        checkPrintSet();
    }

    public void checkPrintSet(){
        RealmResults<PrintSetRm> printSetRms = realm.where(PrintSetRm.class).findAll();
        if(printSetRms.isEmpty()){
            realm.beginTransaction();
            PrintSetRm printSetRm = realm.createObject(PrintSetRm.class, UUID.randomUUID().toString());
            printSetRm.setSizePaper(58f);
            printSetRm.setPerLine(48);
            printSetRm.setDpiPrinter(203);
            realm.commitTransaction();
        }
    }

    public void getDataUser(){
        DocumentReference docRef = db.collection("users").document(MyApplication.getUser_id());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}