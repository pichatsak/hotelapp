package com.devpos.hotelapp.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devpos.hotelapp.FirebaseModels.CateRooms;
import com.devpos.hotelapp.FirebaseModels.RentsEnd;
import com.devpos.hotelapp.FirebaseModels.Rooms;
import com.devpos.hotelapp.MainActivity;
import com.devpos.hotelapp.MyApplication;
import com.devpos.hotelapp.R;
import com.devpos.hotelapp.SettingRoomActivity;
import com.devpos.hotelapp.adaptors.CateRoomAdapter;
import com.devpos.hotelapp.adaptors.RoomsAdapter;
import com.devpos.hotelapp.addmenuroomActivity;
import com.devpos.hotelapp.addpageroomActivity;
import com.devpos.hotelapp.databinding.FragmentHomeBinding;
import com.devpos.hotelapp.models.CateRoomModels;
import com.devpos.hotelapp.models.RoomModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ImageView menubar;
    private final static int LAUNCH_ADD_CATE = 1;
    private final static int LAUNCH_ADD_ROOM = 2;
    private final static int LAUNCH_RENT_NEW = 3;

    private ImageView addmainmenu;
    private int CATE_CURRENT = 0;
    private String CATE_KEY_CURRENT = "";
    private boolean FIRST_OPEN = true;
    private RecyclerView viewCateRoom, viewRooms;
    private ArrayList<CateRoomModels> cateRoomModelsArrayList = new ArrayList<>();
    private ArrayList<RoomModel> roomsArrayList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        menubar = root.findViewById(R.id.menubar);
        viewCateRoom = root.findViewById(R.id.viewCateRoom);
        viewRooms = root.findViewById(R.id.viewRooms);
        menubar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawer.open();
            }
        });
        addmainmenu = root.findViewById(R.id.addmainmenu);
        addmainmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoaddmainmenu = new Intent(getContext(), addpageroomActivity.class);
                startActivityForResult(gotoaddmainmenu, LAUNCH_ADD_CATE);
            }
        });

        getDataCateRoom();
        return root;
    }

    public void getDataRooms(String cateKey) {
        Query firstQuery = db.collection("rooms").whereEqualTo("userId", MyApplication.getUser_id());
        firstQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            roomsArrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                RoomModel roomModelAdd = new RoomModel();
                                roomModelAdd.setRoomId("");
                                roomModelAdd.setRoomName("");
                                roomModelAdd.setStatusRoom("");
                                roomModelAdd.setCateId(cateKey);
                                roomsArrayList.add(roomModelAdd);

                                Map<String, Object> friendsMap = (Map<String, Object>) document.getData().get("listRoom");
                                for (Map.Entry<String, Object> entry : friendsMap.entrySet()) {
                                    String key = entry.getKey();
                                    Map<String, Object> value = (Map<String, Object>) entry.getValue();
                                    if (value.get("cateId").toString().equals(cateKey)) {
                                        RoomModel roomModel = new RoomModel();
                                        roomModel.setRoomId(value.get("roomId").toString());
                                        roomModel.setRoomName(value.get("roomName").toString());
                                        roomModel.setStatusRoom(value.get("statusRoom").toString());
                                        roomModel.setCateId(value.get("cateId").toString());
                                        roomsArrayList.add(roomModel);
                                    }
                                }

                                viewRooms.setHasFixedSize(true);
                                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
                                viewRooms.setLayoutManager(mLayoutManager);

                                RoomsAdapter roomsAdapter = new RoomsAdapter(getContext(), roomsArrayList, new RoomsAdapter.OnClickChoose() {
                                    @Override
                                    public void OnClickChoose(int position, String statusClick, RoomModel roomModel) {
                                        if (statusClick.equals("add")) {
                                            Intent gotoaddroom = new Intent(getContext(), addmenuroomActivity.class);
                                            gotoaddroom.putExtra("cateKey", CATE_KEY_CURRENT);
                                            startActivityForResult(gotoaddroom, LAUNCH_ADD_ROOM);
                                        } else if (statusClick.equals("rent")) {
                                            Intent gotosettingroom = new Intent(getContext(), SettingRoomActivity.class);
                                            gotosettingroom.putExtra("roomKey", roomModel.getRoomId());
                                            startActivityForResult(gotosettingroom, LAUNCH_RENT_NEW);
                                        } else if (statusClick.equals("checkOut")) {
                                            goCheckOut(roomModel);
                                        }
                                    }
                                });
                                viewRooms.setAdapter(roomsAdapter);
                            }
                        } else {
                            Log.d("CHKDB", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void goCheckOut(RoomModel roomModel) {
        db.collection("rents")
                .whereEqualTo("userId", MyApplication.getUser_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String keyMain = document.getId();
                                Map<String, Object> friendsMap = (Map<String, Object>) document.getData().get("listRents");
                                for (Map.Entry<String, Object> entry : friendsMap.entrySet()) {
                                    String key = entry.getKey();
                                    Map<String, Object> value = (Map<String, Object>) entry.getValue();
                                    if (roomModel.getRoomId().equals(value.get("roomId").toString())) {
                                        Timestamp timeStart = (Timestamp) value.get("dateStart");
                                        Timestamp timeEnd = (Timestamp) value.get("dateEnd");
                                        Calendar curDate = Calendar.getInstance();
                                        Date dateCur = curDate.getTime();
                                        Date dateStart = timeStart.toDate();
                                        Date dateEnd = timeEnd.toDate();
                                        if (dateCur.getTime() >= dateStart.getTime() && dateCur.getTime() <= dateEnd.getTime()) {
                                            db.collection("rentsEnd")
                                                    .whereEqualTo("userId", MyApplication.getUser_id())
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                if(task.getResult().size()>0){
                                                                    db.collection("rentsEnd")
                                                                            .whereEqualTo("userId", MyApplication.getUser_id())
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                            String lastId = document.getId();
                                                                                            String rentNewId = UUID.randomUUID().toString();
                                                                                            final HashMap<String, Object> newRent = new HashMap<>();
                                                                                            newRent.put("rentName", value.get("rentName").toString());
                                                                                            newRent.put("rentPerson", Integer.valueOf(value.get("rentPerson").toString()));
                                                                                            newRent.put("dateStart", value.get("dateStart"));
                                                                                            newRent.put("dateEnd", value.get("dateEnd"));
                                                                                            newRent.put("rentDay", value.get("rentDay"));
                                                                                            newRent.put("roomId", value.get("roomId").toString());
                                                                                            newRent.put("status", value.get("status").toString());
                                                                                            newRent.put("dateCreate", value.get("dateCreate"));
                                                                                            newRent.put("dateCheckOut", FieldValue.serverTimestamp());
                                                                                            newRent.put("roomPriceDay", Integer.valueOf(value.get("roomPriceDay").toString()));
                                                                                            newRent.put("totalRentRoom", Integer.valueOf(value.get("totalRentRoom").toString()));
                                                                                            db.collection("rentsEnd").document(lastId).update("listRents." + rentNewId, newRent);
                                                                                            db.collection("rentsEnd").document(lastId).update("listRents." + rentNewId + ".listPay", value.get("listPay"));
                                                                                            Map<String,Object> updateDEl = new HashMap<>();
                                                                                            updateDEl.put("listRents."+key, FieldValue.delete());
                                                                                            db.collection("rents").document(keyMain).update(updateDEl);
                                                                                            getDataRooms(CATE_KEY_CURRENT);
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("CHKDB", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                }else{
                                                                    RentsEnd rentsEnd = new RentsEnd();
                                                                    rentsEnd.setUserId(MyApplication.getUser_id());
                                                                    db.collection("rentsEnd")
                                                                            .add(rentsEnd)
                                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onSuccess(DocumentReference documentReference) {
                                                                                    String lastId = documentReference.getId();
                                                                                    String rentNewId = UUID.randomUUID().toString();
                                                                                    final HashMap<String, Object> newRent = new HashMap<>();
                                                                                    newRent.put("rentName", value.get("rentName").toString());
                                                                                    newRent.put("rentPerson", Integer.valueOf(value.get("rentPerson").toString()));
                                                                                    newRent.put("dateStart", value.get("dateStart"));
                                                                                    newRent.put("dateEnd", value.get("dateEnd"));
                                                                                    newRent.put("rentDay", value.get("rentDay"));
                                                                                    newRent.put("roomId", value.get("roomId").toString());
                                                                                    newRent.put("status", value.get("status").toString());
                                                                                    newRent.put("dateCreate", value.get("dateCreate"));
                                                                                    newRent.put("dateCheckOut", FieldValue.serverTimestamp());
                                                                                    newRent.put("roomPriceDay", Integer.valueOf(value.get("roomPriceDay").toString()));
                                                                                    newRent.put("totalRentRoom", Integer.valueOf(value.get("totalRentRoom").toString()));
                                                                                    db.collection("rentsEnd").document(lastId).update("listRents." + rentNewId, newRent);
                                                                                    db.collection("rentsEnd").document(lastId).update("listRents." + rentNewId + ".listPay", value.get("listPay"));
                                                                                    Map<String,Object> updateDEl = new HashMap<>();
                                                                                    updateDEl.put("listRents."+key, FieldValue.delete());
                                                                                    db.collection("rents").document(keyMain).update(updateDEl);
                                                                                    getDataRooms(CATE_KEY_CURRENT);
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
                            }
                        }
                    }
                });
    }

    public void getDataCateRoom() {
        db.collection("cate_rooms")
                .whereEqualTo("userId", MyApplication.getUser_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            cateRoomModelsArrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> friendsMap = (Map<String, Object>) document.getData().get("listCate");
                                for (Map.Entry<String, Object> entry : friendsMap.entrySet()) {
                                    String key = entry.getKey();
                                    Map<String, Object> value = (Map<String, Object>) entry.getValue();
                                    Log.d("CHKGETMAP", value.get("cateName").toString());
                                    CateRoomModels cateRoomModels = new CateRoomModels();
                                    cateRoomModels.setCateName(value.get("cateName").toString());
                                    cateRoomModels.setCateKey(key);
                                    cateRoomModelsArrayList.add(cateRoomModels);
                                    if (FIRST_OPEN) {
                                        FIRST_OPEN = false;
                                        CATE_KEY_CURRENT = key;
                                        getDataRooms(key);
                                    }
                                }
                                viewCateRoom.setHasFixedSize(true);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                viewCateRoom.setLayoutManager(linearLayoutManager);
                                CateRoomAdapter cateRoomAdapter = new CateRoomAdapter(getContext(), cateRoomModelsArrayList, new CateRoomAdapter.OnClickChoose() {
                                    @Override
                                    public void OnClickChoose(int position, String keyCate) {
                                        Log.d("CHKCICKLCATE", "choose : " + keyCate);
                                        CATE_CURRENT = position;
                                        CATE_KEY_CURRENT = keyCate;
                                        getDataRooms(CATE_KEY_CURRENT);
                                    }
                                });
                                viewCateRoom.setAdapter(cateRoomAdapter);
                            }
                        } else {
                            Log.d("CHKDB", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("CHKRES", "requestCode : " + requestCode + " resultCode: " + resultCode);
        if (resultCode == Activity.RESULT_OK && requestCode == LAUNCH_ADD_CATE) {
            Log.d("CHKRES", "success");
            getDataCateRoom();
        } else if (resultCode == Activity.RESULT_OK && requestCode == LAUNCH_ADD_ROOM) {
            getDataRooms(CATE_KEY_CURRENT);
        } else if (resultCode == Activity.RESULT_OK && requestCode == LAUNCH_RENT_NEW) {
            getDataRooms(CATE_KEY_CURRENT);
            String rentIdGet = data.getStringExtra("rentId");
            openDialogPrint(rentIdGet);
        }
    }

    private void openDialogPrint(String rentIdGet) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}