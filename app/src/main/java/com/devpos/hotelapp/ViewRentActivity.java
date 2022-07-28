package com.devpos.hotelapp;

import static com.google.firebase.firestore.DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.devpos.hotelapp.RealmDB.PrintSetRm;
import com.devpos.hotelapp.adaptors.PaysViewAdapter;
import com.devpos.hotelapp.adaptors.ServiceAdapter;
import com.devpos.hotelapp.adaptors.ServiceViewAdapter;
import com.devpos.hotelapp.async.AsyncBluetoothEscPosPrint;
import com.devpos.hotelapp.async.AsyncEscPosPrint;
import com.devpos.hotelapp.async.AsyncEscPosPrinter;
import com.devpos.hotelapp.models.PayModel;
import com.devpos.hotelapp.models.ServiceModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class ViewRentActivity extends AppCompatActivity {
    private String keyRent = "";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final static String KEY_LOG = "ViewRent";
    private final static int LAUNCH_EDIT = 1;
    private ImageView backhome;
    private TextView nameRent, personRent, dateStart, dateEnd, numDay;
    private ArrayList<ServiceModel> serviceModelList = new ArrayList<>();
    private ArrayList<PayModel> payModelArrayList = new ArrayList<>();
    private RecyclerView viewSevice,viewPay;
    private LinearLayout contEmptyService;
    private TextView totalFinal,tvRoom,tvPriceRoom,tvServicePrice;
    private int totalRent =0;
    private int totalService =0;
    private int totalRoom =0;
    private LinearLayout goEdit;
    private String roomKey = "";
    LinearLayout printBill;
    private BluetoothConnection selectedDevice;
    Realm realm = Realm.getDefaultInstance();
    Bitmap bitmapSlip;
    String nameHotel = "";
    boolean statusLogo = false;
    Map<String, Object> curOrder;
    Map<String, Object> txtFinalBill;



    public static final int PERMISSION_BLUETOOTH = 101;
    public static final int PERMISSION_BLUETOOTH_ADMIN = 102;
    public static final int PERMISSION_BLUETOOTH_CONNECT = 103;
    public static final int PERMISSION_BLUETOOTH_SCAN = 104;


    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageReference = storage.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rent);
        tvRoom = findViewById(R.id.tvRoom);
        tvServicePrice = findViewById(R.id.tvServicePrice);
        tvPriceRoom = findViewById(R.id.tvPriceRoom);
        backhome = findViewById(R.id.backhome);
        nameRent = findViewById(R.id.nameRent);
        personRent = findViewById(R.id.personRent);
        dateStart = findViewById(R.id.dateStart);
        dateEnd = findViewById(R.id.dateEnd);
        viewSevice = findViewById(R.id.viewSevice);
        totalFinal = findViewById(R.id.totalFinal);
        goEdit = findViewById(R.id.goEdit);
        viewPay = findViewById(R.id.viewPay);
        contEmptyService = findViewById(R.id.contEmptyService);
        printBill = findViewById(R.id.printBill);
        numDay = findViewById(R.id.numDay);
        Log.d(KEY_LOG, "keyRent : " + MyApplication.getRentIdViewCur());
        keyRent = MyApplication.getRentIdViewCur();
        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getDataShow();
        goEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotosettingroom = new Intent(ViewRentActivity.this, SettingRoomActivity.class);
                gotosettingroom.putExtra("roomKey", roomKey);
                gotosettingroom.putExtra("statusSave", "edit");
                gotosettingroom.putExtra("rentId", keyRent);
                startActivityForResult(gotosettingroom, LAUNCH_EDIT);
            }
        });
        getDataPrint();
        printBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPrints();
            }
        });
    }

    private void setPrints() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PERMISSION_BLUETOOTH_ADMIN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_BLUETOOTH_CONNECT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_BLUETOOTH_SCAN);
        } else {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(this, "เครื่องนี้ไม่รองรับระบบ Bluetooth", Toast.LENGTH_SHORT).show();
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PERMISSION_BLUETOOTH_ADMIN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_BLUETOOTH_CONNECT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_BLUETOOTH_SCAN);
        } else {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(this, "เครื่องนี้ไม่รองรับระบบ Bluetooth", Toast.LENGTH_SHORT).show();
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 11);
                } else {
                    new AsyncBluetoothEscPosPrint(
                            this,
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
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                        return;
                    }
                }
                Log.d("CHKDV", "name : " + device.getDevice().getName());
                items[++i] = device.getDevice().getName();

            }

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
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
                                        bitmapSlip = MediaStore.Images.Media.getBitmap(ViewRentActivity.this.getContentResolver(), uri);
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

    private void getDataShow() {
        db.collection("rents")
                .whereEqualTo("userId", MyApplication.getUser_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> getList = (Map<String, Object>) document.getData().get("listRents");
                                Map<String, Object> valueGet = (Map<String, Object>) getList.get(keyRent);
                                curOrder = valueGet;

                                Map<String, Object> friendsMap = (Map<String, Object>) document.getData().get("listRents");
                                if (friendsMap.size() > 0) {
                                    Map<String, Object> value = (Map<String, Object>) friendsMap.get(keyRent);
                                    nameRent.setText(value.get("rentName").toString());
                                    personRent.setText(value.get("rentPerson").toString() + " คน");
                                    roomKey = value.get("roomId").toString();
                                    Timestamp timestamp = (Timestamp) value.get("dateStart");
                                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy HH:mm", Locale.US);
                                    String dateStartTxt = df.format(timestamp.toDate());

                                    Timestamp timestampEnd = (Timestamp) value.get("dateEnd");
                                    String dateEndTxt = df.format(timestampEnd.toDate());

                                    dateStart.setText(dateStartTxt);
                                    dateEnd.setText(dateEndTxt);
                                    numDay.setText(value.get("rentDay").toString()+" วัน");

                                    Gson gson = new Gson();
                                    Type type = new TypeToken<ArrayList<ServiceModel>>() {}.getType();
                                    serviceModelList = gson.fromJson(value.get("listService").toString(), type);
                                    if(serviceModelList.size()==0){
                                        contEmptyService.setVisibility(View.VISIBLE);
                                    }else{
                                        contEmptyService.setVisibility(View.GONE);
                                    }
                                    setShowService();
                                    totalService =0;
                                    for (ServiceModel serviceModel : serviceModelList){
                                        totalService += serviceModel.getPrice();
                                    }
                                    totalRoom = Integer.valueOf(value.get("totalRentRoom").toString());
                                    int totalAll = totalService+totalRoom;
                                    tvRoom.setText("ค่าห้อง X "+value.get("rentDay").toString()+" วัน");
                                    tvPriceRoom.setText(totalRoom+".-");
                                    totalFinal.setText(totalAll+".-");
                                    tvServicePrice.setText(totalService+".-");

                                    Type type2 = new TypeToken<ArrayList<PayModel>>() {}.getType();
                                    payModelArrayList = gson.fromJson(value.get("listPay").toString(), type2);
                                    setShowPay();

                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("CHKRES", "requestCode : " + requestCode + " resultCode: " + resultCode);
        if (resultCode == Activity.RESULT_OK && requestCode == LAUNCH_EDIT) {
            Log.d("CHKRES", "success");
            getDataShow();
        }
    }

    private void setShowPay() {
        viewPay.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewPay.setLayoutManager(linearLayoutManager);
        PaysViewAdapter paysViewAdapter = new PaysViewAdapter(this, payModelArrayList, new PaysViewAdapter.OnClickChoose() {
            @Override
            public void OnClickChoose(int position, String keyCate) {

            }
        });
        viewPay.setAdapter(paysViewAdapter);
    }

    private void setShowService() {
        viewSevice.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewSevice.setLayoutManager(linearLayoutManager);
        ServiceViewAdapter serviceAdapter = new ServiceViewAdapter(this, serviceModelList, new ServiceViewAdapter.OnClickChoose() {
            @Override
            public void OnClickChoose(int position, String keyCate) {

            }
        });
        viewSevice.setAdapter(serviceAdapter);
    }
}