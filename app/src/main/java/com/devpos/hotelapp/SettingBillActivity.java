package com.devpos.hotelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.devpos.hotelapp.adaptors.ServiceViewAdapter;
import com.devpos.hotelapp.adaptors.TxtBillAdapter;
import com.devpos.hotelapp.models.TextFinalBillModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SettingBillActivity extends AppCompatActivity {
    TextView chooseImg;
    private Uri UrlImg;
    private int StatusChooseImg = 0;
    private ImageView imgShow;
    private RecyclerView view_bill;
    TextView addBt;
    private ArrayList<String> mSpin = new ArrayList<String>();
    private ArrayList<TextFinalBillModel> textFinalBillModels = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView savePrint;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageReference = storage.getReference();
    ImageView backsetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_bill);
        chooseImg = findViewById(R.id.chooseImg);
        imgShow = findViewById(R.id.imgShow);
        addBt = findViewById(R.id.addBt);
        savePrint = findViewById(R.id.savePrint);
        backsetting = findViewById(R.id.backsetting);
        view_bill = findViewById(R.id.view_bill);
        mSpin.add("ซ้าย");
        mSpin.add("กลาง");
        mSpin.add("ขวา");
        backsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImgUp();
            }
        });

        addBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAdd();
            }
        });

        getData();
        savePrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
    }

    private void saveData() {
        if (StatusChooseImg == 0) {
            Map<String, Object> newList = new HashMap<>();
            db.collection("print_data").document(MyApplication.getUser_id())
                    .update("picChooseStatus", "no", "listBillTxt", newList)
                    .addOnSuccessListener(aVoid -> {
                        Map<String, Object> newBillList = new HashMap<>();
                        for(TextFinalBillModel textFinalBillModel : textFinalBillModels){
                            newBillList.put(UUID.randomUUID().toString(),textFinalBillModel);
                        }
                        db.collection("print_data").document(MyApplication.getUser_id())
                                .update( "listBillTxt", newBillList)
                                .addOnSuccessListener(aVoid2 -> {
                                    Toast.makeText(SettingBillActivity.this, "บันทึกข้อมูลเรียบร้อย", Toast.LENGTH_SHORT).show();
                                    finish();
                                });

                    });
        } else {
            StorageReference ref = storageReference.child("pic_slip/" + MyApplication.getUser_id());
            ref.putFile(UrlImg)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    Map<String, Object> newList = new HashMap<>();
                                    db.collection("print_data").document(MyApplication.getUser_id())
                                            .update("picChooseStatus", "yes", "listBillTxt", newList)
                                            .addOnSuccessListener(aVoid -> {
                                                Map<String, Object> newBillList = new HashMap<>();
                                                for(TextFinalBillModel textFinalBillModel : textFinalBillModels){
                                                    newBillList.put(UUID.randomUUID().toString(),textFinalBillModel);
                                                }
                                                db.collection("print_data").document(MyApplication.getUser_id())
                                                        .update( "listBillTxt", newBillList)
                                                        .addOnSuccessListener(aVoid2 -> {
                                                            Toast.makeText(SettingBillActivity.this, "บันทึกข้อมูลเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        });

                                            });

                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }

    private void getData() {
        DocumentReference docRef = db.collection("print_data").document(MyApplication.getUser_id());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("CHKDB", "DocumentSnapshot data: " + document.getData());
                        if(document.getData().get("picChooseStatus").toString().equals("yes")){
                            storageReference.child("pic_slip/"+MyApplication.getUser_id()).getDownloadUrl().addOnSuccessListener(uri -> {
                                Glide.with(SettingBillActivity.this)
                                        .load(uri)
                                        .into(imgShow);
                            });
                        }
                        Map<String, Object> listMap = (Map<String, Object>) document.getData().get("listBillTxt");
                        ArrayList<TextFinalBillModel> textFinalBillModelsGet = new ArrayList<>();
                        for (Map.Entry<String, Object> entry : listMap.entrySet()) {
                            Map<String, Object> value = (Map<String, Object>) entry.getValue();
                            String getTxtType = value.get("typeText").toString();
                            TextFinalBillModel textFinalBillModel = new TextFinalBillModel();
                            textFinalBillModel.setTypeText(getTxtType);
                            textFinalBillModel.setTxtShow(value.get("txtShow").toString());
                            textFinalBillModel.setPos(Integer.valueOf(value.get("pos").toString()));
                            textFinalBillModelsGet.add(textFinalBillModel);
                        }

                        Collections.sort(textFinalBillModelsGet, (TextFinalBillModel m1, TextFinalBillModel m2) -> m1.getPos() - m2.getPos());
                        textFinalBillModels = textFinalBillModelsGet;
                        getShowList();
                    } else {
                        Log.d("CHKDB", "No such document");
                        Map<String, Object> newList = new HashMap<>();
                        Map<String, Object> newListBill = new HashMap<>();
                        newList.put("picChooseStatus", "no");
                        newList.put("listBillTxt", newListBill);
                        db.collection("print_data").document(MyApplication.getUser_id())
                                .set(newList)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                }
            }
        });

    }

    private void getShowList() {
        view_bill.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        view_bill.setLayoutManager(linearLayoutManager);
        TxtBillAdapter txtBillAdapter = new TxtBillAdapter(this, textFinalBillModels, new TxtBillAdapter.OnClickChoose() {
            @Override
            public void OnClickChoose(int position, String status) {
                if (status.equals("del")) {
                    textFinalBillModels.remove(position);
                    getShowList();
                } else if (status.equals("edit")) {
                    openEdit(position);
                }
            }
        });
        view_bill.setAdapter(txtBillAdapter);
    }

    private void openEdit(int pos) {
        TextFinalBillModel modeGet = textFinalBillModels.get(pos);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_add_txt_bill);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        AutoCompleteTextView spinType = dialog.findViewById(R.id.spinType);
        ArrayAdapter<String> adapterThai = new ArrayAdapter<String>(this,
                R.layout.dropdown_item, mSpin);
        spinType.setAdapter(adapterThai);
        final String[] itemChoose = {""};
        spinType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                itemChoose[0] = editable.toString();
            }
        });

        TextView cancle = dialog.findViewById(R.id.cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        TextInputEditText tv_txt = dialog.findViewById(R.id.tv_txt);
        tv_txt.setText(modeGet.getTxtShow());
        TextView confirm = dialog.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_txt.getText().toString().isEmpty() || itemChoose[0].equals("")) {
                    Toast.makeText(SettingBillActivity.this, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
                } else {

                    String typeFinal = "";
                    if (itemChoose[0].equals("ซ้าย")) {
                        typeFinal = "left";
                    } else if (itemChoose[0].equals("กลาง")) {
                        typeFinal = "center";
                    } else if (itemChoose[0].equals("ขวา")) {
                        typeFinal = "right";
                    }

                    TextFinalBillModel textFinalBillModel = new TextFinalBillModel();
                    textFinalBillModel.setTypeText(typeFinal);
                    textFinalBillModel.setTxtShow(tv_txt.getText().toString());
                    textFinalBillModel.setPos(textFinalBillModels.size() + 1);
                    textFinalBillModels.set(pos, textFinalBillModel);
                    getShowList();
                    dialog.dismiss();
                }
            }
        });
    }

    private void openAdd() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_add_txt_bill);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        AutoCompleteTextView spinType = dialog.findViewById(R.id.spinType);
        ArrayAdapter<String> adapterThai = new ArrayAdapter<String>(this,
                R.layout.dropdown_item, mSpin);
        spinType.setAdapter(adapterThai);
        final String[] itemChoose = {""};
        spinType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                itemChoose[0] = editable.toString();
            }
        });
        TextView cancle = dialog.findViewById(R.id.cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        TextInputEditText tv_txt = dialog.findViewById(R.id.tv_txt);
        TextView confirm = dialog.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_txt.getText().toString().isEmpty() || itemChoose[0].equals("")) {
                    Toast.makeText(SettingBillActivity.this, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
                } else {

                    String typeFinal = "";
                    if (itemChoose[0].equals("ซ้าย")) {
                        typeFinal = "left";
                    } else if (itemChoose[0].equals("กลาง")) {
                        typeFinal = "center";
                    } else if (itemChoose[0].equals("ขวา")) {
                        typeFinal = "right";
                    }

                    TextFinalBillModel textFinalBillModel = new TextFinalBillModel();
                    textFinalBillModel.setTypeText(typeFinal);
                    textFinalBillModel.setTxtShow(tv_txt.getText().toString());
                    textFinalBillModel.setPos(textFinalBillModels.size() + 1);
                    textFinalBillModels.add(textFinalBillModel);
                    getShowList();
                    dialog.dismiss();
                }
            }
        });
    }


    public void chooseImgUp() {
        // สร้าง Intent เพื่อไปหน้าเลือกรูปจากตัวเครื่อง
        Intent intent = new Intent();
        // เซ็ตค่าให้หน้าเลือกรูปภาพจะเลือกได้แค่ไฟล์ที่เป็น รูปภาพเท่านั้น
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // ไปหน้าเลือกรูปภาพ พร้อมกับรอผลตอบกลับ
        startActivityForResult(Intent.createChooser(intent, "เลือกรูปภาพ"), 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case 1:
                    if (resultCode == Activity.RESULT_OK) {
                        // เข้าเงื่อนไขนี้ก็ต่อเมื่อผู้ใช้เลือกรูปภาพสำเร็จ
                        // รับค่ารูปภาพที่เลือกแปลงเป็นตัวแปร URI
                        Uri selectedImageUri = data.getData();
                        UrlImg = selectedImageUri;
                        // เปลี่ยนค่าตัวแปร StatusChooseImg ให้เป็น 1 เพื่อเอาไว้เช็คว่าผู้ใช้ได้ทำการอัพโหลดรูปแล้วหรือยัง
                        StatusChooseImg = 1;
                        // ซ่อน ImageView Icon กล้อง
                        imgShow.setImageURI(selectedImageUri);
                        Log.d("CHKIMG", "Selecting img success");
                        break;
                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        // เข้าเงื่อนไขนี้ก็ต่อเมื่อผู้ใช้ไม่ได้เลือกรูปภาพ
                        StatusChooseImg = 0;
                        Log.e("CHKIMG", "Selecting picture cancelled");
                    }
                    break;
            }
        } catch (Exception e) {
            StatusChooseImg = 0;
            Log.e("CHKIMG", "Exception in onActivityResult : " + e.getMessage());
        }
    }
}