package com.devpos.hotelapp.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.devpos.hotelapp.EditCateActivity;
import com.devpos.hotelapp.FindRoomActivity;
import com.devpos.hotelapp.FirebaseModels.CateRooms;
import com.devpos.hotelapp.FirebaseModels.RentsEnd;
import com.devpos.hotelapp.FirebaseModels.Rooms;
import com.devpos.hotelapp.MainActivity;
import com.devpos.hotelapp.MyApplication;
import com.devpos.hotelapp.R;
import com.devpos.hotelapp.RealmDB.PrintSetRm;
import com.devpos.hotelapp.SettingBillActivity;
import com.devpos.hotelapp.SettingRoomActivity;
import com.devpos.hotelapp.ViewRentActivity;
import com.devpos.hotelapp.ViewStatusRoomActivity;
import com.devpos.hotelapp.adaptors.CateRoomAdapter;
import com.devpos.hotelapp.adaptors.RoomsAdapter;
import com.devpos.hotelapp.addmenuroomActivity;
import com.devpos.hotelapp.addpageroomActivity;
import com.devpos.hotelapp.async.AsyncBluetoothEscPosPrint;
import com.devpos.hotelapp.async.AsyncEscPosPrint;
import com.devpos.hotelapp.async.AsyncEscPosPrinter;
import com.devpos.hotelapp.databinding.FragmentHomeBinding;
import com.devpos.hotelapp.dialog.DialogConfirm;
import com.devpos.hotelapp.dialog.DialogPrint;
import com.devpos.hotelapp.models.CateRoomModels;
import com.devpos.hotelapp.models.PayModel;
import com.devpos.hotelapp.models.RoomModel;
import com.devpos.hotelapp.models.ServiceModel;
import com.devpos.hotelapp.models.TextFinalBillModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ImageView menubar;
    private final static int LAUNCH_ADD_CATE = 1;
    private final static int LAUNCH_ADD_ROOM = 2;
    private final static int LAUNCH_RENT_NEW = 3;
    private final static int LAUNCH_SEARCH = 4;

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageReference = storage.getReference();

    private ImageView addmainmenu;
    private int CATE_CURRENT = 0;
    private String CATE_KEY_CURRENT = "";
    private boolean FIRST_OPEN = true;
    private RecyclerView viewCateRoom, viewRooms;
    private ArrayList<CateRoomModels> cateRoomModelsArrayList = new ArrayList<>();
    private ArrayList<RoomModel> roomsArrayList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static final int PERMISSION_BLUETOOTH = 101;
    public static final int PERMISSION_BLUETOOTH_ADMIN = 102;
    public static final int PERMISSION_BLUETOOTH_CONNECT = 103;
    public static final int PERMISSION_BLUETOOTH_SCAN = 104;

    private BluetoothConnection selectedDevice;
    Realm realm = Realm.getDefaultInstance();
    Bitmap bitmapSlip;
    String nameHotel = "";
    boolean statusLogo = false;
    Map<String, Object> curOrder;
    Map<String, Object> txtFinalBill;
    String rentIdCur = "";
    TextView search_view;
    Handler handler;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        menubar = root.findViewById(R.id.menubar);
        viewCateRoom = root.findViewById(R.id.viewCateRoom);
        viewRooms = root.findViewById(R.id.viewRooms);
        search_view = root.findViewById(R.id.search_view);
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
        getDataUser();
        getDataCateRoom();
        getDataPrint();

        setClickSearch();
        setReData();
        return root;
    }

    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            //Do Something
        }
    };

    public void setReData(){
        handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                Log.d("CHKINT","yes");
                getDataRooms(CATE_KEY_CURRENT);
                handler.postDelayed(this, 60000*20);
            }
        };
        handler.post(run);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(myRunnable);
        super.onDestroy();
    }

    private void setClickSearch() {
        search_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoaddmainmenu = new Intent(getContext(), FindRoomActivity.class);
                startActivityForResult(gotoaddmainmenu, LAUNCH_SEARCH);
            }
        });
    }

    private void getDataUser() {
        DocumentReference docRef = db.collection("users").document(MyApplication.getUser_id());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        nameHotel = document.getData().get("listhotel").toString();

                    }
                }
            }
        });
    }

    public void getDataRooms(String cateKey) {
        Query firstQuery = db.collection("rooms").whereEqualTo("userId", MyApplication.getUser_id());
        firstQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            roomsArrayList = new ArrayList<>();
                            if (task.getResult().size() > 0) {
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
                                            Log.d("CHKROOMS",""+value.get("roomName").toString()+" : "+value.get("statusRoom").toString());
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
                                                gotoaddroom.putExtra("statusSave", "add");
                                                gotoaddroom.putExtra("cateKey", CATE_KEY_CURRENT);
                                                startActivityForResult(gotoaddroom, LAUNCH_ADD_ROOM);
                                            } else if (statusClick.equals("rent")) {
                                                Intent gotosettingroom = new Intent(getContext(), SettingRoomActivity.class);
                                                gotosettingroom.putExtra("roomKey", roomModel.getRoomId());
                                                gotosettingroom.putExtra("statusSave", "new");
                                                startActivityForResult(gotosettingroom, LAUNCH_RENT_NEW);
                                            } else if (statusClick.equals("checkOut")) {
                                                goCheckOut(roomModel);
                                            } else if (statusClick.equals("viewRent")) {
                                                Intent goToRentView = new Intent(getContext(), ViewRentActivity.class);
                                                startActivityForResult(goToRentView, LAUNCH_ADD_ROOM);
                                            } else if (statusClick.equals("cancelRent")) {
                                                goCancel();
                                            } else if (statusClick.equals("editRoom")) {
                                                Intent goEditRoom = new Intent(getContext(), addmenuroomActivity.class);
                                                goEditRoom.putExtra("statusSave", "edit");
                                                goEditRoom.putExtra("cateKey", CATE_KEY_CURRENT);
                                                goEditRoom.putExtra("roomId", roomModel.getRoomId());
                                                startActivityForResult(goEditRoom, LAUNCH_ADD_ROOM);
                                            } else if (statusClick.equals("delRoom")) {
                                                goDelRoom(roomModel.getRoomId());
                                            } else if (statusClick.equals("viewStatusRoom")) {
                                                Intent goViewStatusRoom = new Intent(getContext(), ViewStatusRoomActivity.class);
                                                goViewStatusRoom.putExtra("roomId", roomModel.getRoomId());
                                                startActivityForResult(goViewStatusRoom, LAUNCH_ADD_ROOM);
                                            }
                                        }
                                    });
                                    viewRooms.setAdapter(roomsAdapter);
                                }
                            } else {
                                RoomModel roomModelAdd = new RoomModel();
                                roomModelAdd.setRoomId("");
                                roomModelAdd.setRoomName("");
                                roomModelAdd.setStatusRoom("");
                                roomModelAdd.setCateId(cateKey);
                                roomsArrayList.add(roomModelAdd);

                                viewRooms.setHasFixedSize(true);
                                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
                                viewRooms.setLayoutManager(mLayoutManager);

                                RoomsAdapter roomsAdapter = new RoomsAdapter(getContext(), roomsArrayList, new RoomsAdapter.OnClickChoose() {
                                    @Override
                                    public void OnClickChoose(int position, String statusClick, RoomModel roomModel) {
                                        if (statusClick.equals("add")) {
                                            Intent gotoaddroom = new Intent(getContext(), addmenuroomActivity.class);
                                            gotoaddroom.putExtra("statusSave", "add");
                                            gotoaddroom.putExtra("cateKey", CATE_KEY_CURRENT);
                                            startActivityForResult(gotoaddroom, LAUNCH_ADD_ROOM);
                                        } else if (statusClick.equals("rent")) {
                                            Intent gotosettingroom = new Intent(getContext(), SettingRoomActivity.class);
                                            gotosettingroom.putExtra("roomKey", roomModel.getRoomId());
                                            gotosettingroom.putExtra("statusSave", "new");
                                            startActivityForResult(gotosettingroom, LAUNCH_RENT_NEW);
                                        } else if (statusClick.equals("checkOut")) {
                                            goCheckOut(roomModel);
                                        } else if (statusClick.equals("viewRent")) {
                                            Intent goToRentView = new Intent(getContext(), ViewRentActivity.class);
                                            startActivityForResult(goToRentView, LAUNCH_ADD_ROOM);
                                        } else if (statusClick.equals("cancelRent")) {
                                            goCancel();
                                        } else if (statusClick.equals("editRoom")) {
                                            Intent goEditRoom = new Intent(getContext(), addmenuroomActivity.class);
                                            goEditRoom.putExtra("statusSave", "edit");
                                            goEditRoom.putExtra("cateKey", CATE_KEY_CURRENT);
                                            goEditRoom.putExtra("roomId", roomModel.getRoomId());
                                            startActivityForResult(goEditRoom, LAUNCH_ADD_ROOM);
                                        } else if (statusClick.equals("delRoom")) {
                                            goDelRoom(roomModel.getRoomId());
                                        } else if (statusClick.equals("viewStatusRoom")) {
                                            Intent goViewStatusRoom = new Intent(getContext(), ViewStatusRoomActivity.class);
                                            goViewStatusRoom.putExtra("roomId", roomModel.getRoomId());
                                            startActivityForResult(goViewStatusRoom, LAUNCH_ADD_ROOM);
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

    private void goDelRoom(String roomId) {
        DialogConfirm dialogConfirm = new DialogConfirm(getContext(), "delete", "ต้องการลบห้องนี้?", new DialogConfirm.OnClickDialog() {
            @Override
            public void OnConfirm() {
                db.collection("rooms")
                        .whereEqualTo("userId", MyApplication.getUser_id())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> friendsMap = (Map<String, Object>) document.getData().get("listRoom");
                                        if (friendsMap.size() > 0) {
                                            Map<String, Object> value = (Map<String, Object>) friendsMap.get(roomId);
                                            Map<String, Object> updateDEl = new HashMap<>();
                                            updateDEl.put("listRoom." + roomId, FieldValue.delete());
                                            db.collection("rooms").document(document.getId()).update(updateDEl);
                                            getDataRooms(CATE_KEY_CURRENT);
                                        }
                                    }
                                }
                            }
                        });
            }

            @Override
            public void OnCancel() {
                Log.d("CHKCONFIRM", "no");
            }
        }).openDialog();
    }

    private void goCancel() {
        DialogConfirm dialogConfirm = new DialogConfirm(getContext(), "delete", "ยกเลิกการจองนี้?", new DialogConfirm.OnClickDialog() {
            @Override
            public void OnConfirm() {
                db.collection("rents")
                        .whereEqualTo("userId", MyApplication.getUser_id())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> friendsMap = (Map<String, Object>) document.getData().get("listRents");
                                        String keyRent = MyApplication.getRentIdViewCur();
                                        if (friendsMap.size() > 0) {
                                            Map<String, Object> value = (Map<String, Object>) friendsMap.get(keyRent);
                                            Map<String, Object> updateDEl = new HashMap<>();
                                            updateDEl.put("listRents." + keyRent, FieldValue.delete());
                                            db.collection("rents").document(document.getId()).update(updateDEl);
                                            getDataRooms(CATE_KEY_CURRENT);
                                        }
                                    }
                                }
                            }
                        });
            }

            @Override
            public void OnCancel() {
                Log.d("CHKCONFIRM", "no");
            }
        }).openDialog();
    }

    private void goCheckOut(RoomModel roomModel) {

        DialogConfirm dialogConfirm = new DialogConfirm(getContext(), "delete", "ต้องการเช็คเอาท์ห้องนี้?", new DialogConfirm.OnClickDialog() {
            @Override
            public void OnConfirm() {
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
                                                                        if (task.getResult().size() > 0) {
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
                                                                                                    db.collection("rentsEnd").document(lastId).update("listRents." + rentNewId + ".listService", value.get("listService"));
                                                                                                    Map<String, Object> updateDEl = new HashMap<>();
                                                                                                    updateDEl.put("listRents." + key, FieldValue.delete());
                                                                                                    db.collection("rents").document(keyMain).update(updateDEl);
                                                                                                    getDataRooms(CATE_KEY_CURRENT);
                                                                                                }
                                                                                            } else {
                                                                                                Log.d("CHKDB", "Error getting documents: ", task.getException());
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        } else {
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
                                                                                            db.collection("rentsEnd").document(lastId).update("listRents." + rentNewId + ".listService", value.get("listService"));
                                                                                            Map<String, Object> updateDEl = new HashMap<>();
                                                                                            updateDEl.put("listRents." + key, FieldValue.delete());
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

            @Override
            public void OnCancel() {
                Log.d("CHKCONFIRM", "no");
            }
        }).openDialog();

    }

    public void getDataCateRoom() {
        if (FIRST_OPEN) {
            Log.d("CHKOPEM", "first open");
        } else {
            Log.d("CHKOPEM", "first not : key : " + CATE_KEY_CURRENT);
        }

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
                                    } else {
                                        Log.d("CHKBACK", "get bol " + MyApplication.isIsBackFromFragment());
                                        if (MyApplication.isIsBackFromFragment()) {
                                            getDataRooms(CATE_KEY_CURRENT);
                                            Log.d("CHKBACK", "is back fragment");
                                            MyApplication.setIsBackFromFragment(false);
                                        }
                                    }
                                }
                                viewCateRoom.setHasFixedSize(true);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                viewCateRoom.setLayoutManager(linearLayoutManager);
                                CateRoomAdapter cateRoomAdapter = new CateRoomAdapter(getContext(), cateRoomModelsArrayList, new CateRoomAdapter.OnClickChoose() {
                                    @Override
                                    public void OnClickChoose(int position, String keyCate, String status) {
                                        Log.d("CHKCICKLCATE", "choose : " + keyCate);
                                        if(status.equals("view")){
                                            CATE_CURRENT = position;
                                            CATE_KEY_CURRENT = keyCate;
                                            getDataRooms(CATE_KEY_CURRENT);
                                        }else{
                                            openSetCate(keyCate);
                                        }
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

    private void openSetCate(String keyCate) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_cate);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Display display = ((WindowManager) getContext().getSystemService(getContext().WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        Log.v("width", width + "");
        dialog.getWindow().setLayout((6 * width) / 9, (6 * height) / 9);
        TextView cancle = dialog.findViewById(R.id.cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        LinearLayout btnEdit = dialog.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent editPage = new Intent(getContext(), EditCateActivity.class);
                editPage.putExtra("keyCate",keyCate);
                startActivityForResult(editPage, LAUNCH_ADD_CATE);
            }
        });
        LinearLayout btnDel = dialog.findViewById(R.id.btnDel);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogConfirm dialogConfirm = new DialogConfirm(getContext(), "delete", "ลบหมวดหมู่นี้?", new DialogConfirm.OnClickDialog() {
                    @Override
                    public void OnConfirm() {
                        db.collection("cate_rooms")
                                .whereEqualTo("userId", MyApplication.getUser_id())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String getIdKey = document.getId();
                                                db.collection("cate_rooms").document(getIdKey)
                                                        .update("listCate." + keyCate, FieldValue.delete())
                                                        .addOnSuccessListener(aVoid -> {
                                                            dialog.dismiss();
                                                            FIRST_OPEN = true;
                                                            getDataCateRoom();
                                                        });
                                            }
                                        }
                                    }
                                });

                    }

                    @Override
                    public void OnCancel() {
                        Log.d("CHKCONFIRM", "no");
                    }
                }).openDialog();
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
        } else if (resultCode == Activity.RESULT_OK && requestCode == LAUNCH_SEARCH) {
            getDataRooms(CATE_KEY_CURRENT);
            String rentIdGet = data.getStringExtra("rentId");
            openDialogPrint(rentIdGet);
        } else {
            getDataRooms(CATE_KEY_CURRENT);
        }
    }

    private void openDialogPrint(String rentIdGet) {
        db.collection("rents")
                .whereEqualTo("userId", MyApplication.getUser_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> getList = (Map<String, Object>) document.getData().get("listRents");
                                Map<String, Object> value = (Map<String, Object>) getList.get(rentIdGet);
                                curOrder = value;
                                DialogPrint dialogPrint = new DialogPrint(getContext(), rentIdGet, new DialogPrint.OnClickDialog() {
                                    @Override
                                    public void OnConfirm() {
                                        setPrints();
                                    }
                                });
                                dialogPrint.openDialog();
                            }
                        }
                    }
                });

    }


    public void getDataPrint() {
        DocumentReference docRef = db.collection("print_data").document(MyApplication.getUser_id());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        txtFinalBill = (Map<String, Object>) document.getData().get("listBillTxt");

                        if (document.getData().get("picChooseStatus").toString().equals("yes")) {
                            storageReference.child("pic_slip/" + MyApplication.getUser_id()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    try {
                                        bitmapSlip = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                                        statusLogo = true;
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        } else {
                            statusLogo = false;
                        }

                    }
                }
            }
        });
    }


    private void setPrints() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
        } else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PERMISSION_BLUETOOTH_ADMIN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_BLUETOOTH_CONNECT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_BLUETOOTH_SCAN);
        } else {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(getContext(), "เครื่องนี้ไม่รองรับระบบ Bluetooth", Toast.LENGTH_SHORT).show();
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 11);
                } else {
                    Log.d("CHKBL", "on");
                    if (selectedDevice != null) {
                        browseBluetoothDevice();
                    } else {
                        setPrintCur();
                    }
                }
            }
        }
    }


    private void setPrintCur() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
        } else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PERMISSION_BLUETOOTH_ADMIN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_BLUETOOTH_CONNECT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_BLUETOOTH_SCAN);
        } else {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(getContext(), "เครื่องนี้ไม่รองรับระบบ Bluetooth", Toast.LENGTH_SHORT).show();
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 11);
                } else {
                    new AsyncBluetoothEscPosPrint(
                            getContext(),
                            new AsyncEscPosPrint.OnPrintFinished() {
                                @Override
                                public void onError(AsyncEscPosPrinter asyncEscPosPrinter, int codeException) {
                                    Log.e("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : An error occurred !");
                                }

                                @Override
                                public void onSuccess(AsyncEscPosPrinter asyncEscPosPrinter) {
                                    Log.i("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : Print is finished !");
                                }
                            }
                    ).execute(getAsyncEscPosPrinter(selectedDevice));
                }
            }
        }
    }


    /**
     * Asynchronous printing
     */
    @SuppressLint("SimpleDateFormat")
    public AsyncEscPosPrinter getAsyncEscPosPrinter(DeviceConnection printerConnection) {

        RealmResults<PrintSetRm> printSetRms = realm.where(PrintSetRm.class).findAll();
        SimpleDateFormat format = new SimpleDateFormat("'วันเวลา' dd/MM/yyyy HH:mm");
        DecimalFormat dfStr = new DecimalFormat("#,###.00");
        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, printSetRms.get(0).getDpiPrinter(), printSetRms.get(0).getSizePaper(), printSetRms.get(0).getPerLine());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);

        String txtPrint = "[L]\n";
        if (statusLogo) {
            txtPrint += "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, bitmapSlip) + "</img>\n";
        }

        txtPrint += "[C]<u><font size='big'>" + nameHotel + "</font></u>\n" +
                "[L]\n" +
                "[C]<u type='double'>" + format.format(new Date()) + "</u>\n" +
                "[C]\n" +
                "[C]================================\n" +
                "[L]\n";

        Timestamp timestamp = (Timestamp) curOrder.get("dateStart");
        String dateStartTxt = df.format(timestamp.toDate());

        Timestamp timestampEnd = (Timestamp) curOrder.get("dateEnd");
        String dateEndTxt = df.format(timestampEnd.toDate());

        txtPrint += "[L]<b>ชื่อ-สกุลผู้เข้าพัก : "+curOrder.get("rentName").toString()+"</b> \n";
        txtPrint += "[L]<b>จำนวนผู้เข้าพัก : "+curOrder.get("rentPerson").toString()+"</b> \n";
        txtPrint += "[L]<b>วันเวลาเช็คอิน : "+dateStartTxt+"</b> \n";
        txtPrint += "[L]<b>วันเวลาเช็คเอาท์ : "+dateEndTxt+"</b> \n";
        txtPrint += "[L]<b>จำนวนวันเข้าพัก : "+curOrder.get("rentDay").toString()+"</b> \n\n";
        txtPrint += "[C]\n" +
                    "[C]================================\n" +
                    "[L]\n\n";
        String prices = dfStr.format(curOrder.get("totalRentRoom").toString());
        txtPrint += "[L]<b>ค่าห้อง X "+curOrder.get("rentDay").toString()+" วัน</b>[R]฿"+prices+"\n"+
                    "[L]\n";
        Gson gson = new Gson();

        Type type = new TypeToken<ArrayList<ServiceModel>>() {}.getType();
        Type type2 = new TypeToken<ArrayList<PayModel>>() {}.getType();
        ArrayList<ServiceModel> serviceModelsGet = gson.fromJson(curOrder.get("listService").toString(), type);
        ArrayList<PayModel> payModelArrayListGet = gson.fromJson(curOrder.get("listPay").toString(), type2);
        float totalSv = 0;
        for(ServiceModel serviceModel : serviceModelsGet){
            totalSv += serviceModel.getPrice();
        }
        String pricesMore = dfStr.format(totalSv);
        txtPrint += "[L]<b>ค่าใช้จ่ายเพิ่มเติม</b>[R]฿"+pricesMore+"\n"+
                "[L]\n";
        float totalAll = Float.parseFloat(curOrder.get("totalRentRoom").toString())+totalSv;
        String totalStr = dfStr.format(totalAll);
        txtPrint += "[L]\n" +
                "[C]--------------------------------\n" +
                "[R]ยอดรวมทั้งหมด :[R]฿"+totalStr+"\n"+
                "[L]\n" +
                "[C]--------------------------------\n"+
                "[L]\n\n";
        txtPrint += "[R]<b>การชำระเงิน</b>\n";
        for (PayModel payData : payModelArrayListGet) {
            String payTt = dfStr.format(payData.getPricePay());
            txtPrint += "[R]"+payData.getTypePay()+" : "+payTt+"\n";
        }


        txtPrint += "[L] \n [C]================================ \n \n";
        for (Map.Entry<String, Object> entry : txtFinalBill.entrySet()) {
            Map<String, Object> value = (Map<String, Object>) entry.getValue();
            if(value.get("typeText").toString().equals("left")){
                txtPrint += "[L]"+value.get("txtShow").toString()+"\n";
            }else if(value.get("typeText").toString().equals("center")){
                txtPrint += "[C]"+value.get("txtShow").toString()+"\n";
            }else if(value.get("typeText").toString().equals("right")){
                txtPrint += "[R]"+value.get("txtShow").toString()+"\n";
            }
        }
        txtPrint += "[R]\n\n\n";
        return printer.addTextToPrint(txtPrint);
    }


    public void browseBluetoothDevice() {
        final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();
        if (bluetoothDevicesList != null) {
            final String[] items = new String[bluetoothDevicesList.length + 1];
//            items[0] = "Default printer";
            Log.d("CHKDV", "list : " + bluetoothDevicesList.length);
            int i = 0;
            for (BluetoothConnection device : bluetoothDevicesList) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                        return;
                    }
                }
                Log.d("CHKDV", "name : " + device.getDevice().getName());
                items[++i] = device.getDevice().getName();

            }

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle("เลือกเครื่องพิมพ์");
            if (bluetoothDevicesList.length == 0) {
                items[0] = "ไม่พบเครื่องพิมพ์";
                alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
            } else {
                alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int index = i - 1;
                        if (index == -1) {
                            selectedDevice = null;
                        } else {
                            selectedDevice = bluetoothDevicesList[index];
                            setPrintCur();
                        }
                    }
                });
            }

            AlertDialog alert = alertDialog.create();
            if (bluetoothDevicesList.length == 0) {
                alert.setCanceledOnTouchOutside(true);
            } else {
                alert.setCanceledOnTouchOutside(false);
            }
            alert.show();

        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}