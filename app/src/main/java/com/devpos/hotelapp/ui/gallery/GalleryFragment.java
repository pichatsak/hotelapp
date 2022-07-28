package com.devpos.hotelapp.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.devpos.hotelapp.MyApplication;
import com.devpos.hotelapp.R;
import com.devpos.hotelapp.databinding.FragmentGalleryBinding;
import com.devpos.hotelapp.models.ContactModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    ImageView backhome;
    TextView sendMsg,tv_phone,tv_line;
    EditText ed_msg;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        backhome = root.findViewById(R.id.backhome);
        sendMsg = root.findViewById(R.id.sendMsg);
        ed_msg = root.findViewById(R.id.ed_msg);
        tv_phone = root.findViewById(R.id.tv_phone);
        tv_line = root.findViewById(R.id.tv_line);

        getDataWeb();

        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.setIsBackFromFragment(true);
                Log.d("CHKBACK","set bak : "+MyApplication.isIsBackFromFragment());
                getActivity().onBackPressed();
            }
        });

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSave();
            }
        });
        return root;
    }

    private void getDataWeb() {
        DocumentReference docRef = db.collection("settings").document("CZ6XBeJrwLfGlfbXMFc3");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        tv_line.setText(document.getData().get("idLine").toString());
                        tv_phone.setText(document.getData().get("phone").toString());
                    }
                }
            }
        });

    }

    private void setSave() {
        if(ed_msg.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "กรุณากรอกข้อความ", Toast.LENGTH_SHORT).show();
        }else{
            DocumentReference docRef = db.collection("users").document(MyApplication.getUser_id());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String nameGet = document.getData().get("name").toString();
                            String phoneGet = document.getData().get("phone").toString();
                            String mailGet = document.getData().get("mail").toString();
                            String uid = UUID.randomUUID().toString();
                            Date curDate = Calendar.getInstance().getTime();
                            ContactModel contactModel = new ContactModel();
                            contactModel.setUserId(MyApplication.getUser_id());
                            contactModel.setEmail(mailGet);
                            contactModel.setPhone(phoneGet);
                            contactModel.setName(nameGet);
                            contactModel.setDate(curDate);
                            contactModel.setMsg(ed_msg.getText().toString());
                            db.collection("contacts").document("all")
                                    .update("list_contact." + uid, contactModel)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "ส่งข้อความเรียบร้อย", Toast.LENGTH_SHORT).show();
                                        ed_msg.setText("");
                                    });
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}