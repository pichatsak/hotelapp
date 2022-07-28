package com.devpos.hotelapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.devpos.hotelapp.RealmDB.PrintSetRm;
import com.devpos.hotelapp.async.AsyncBluetoothEscPosPrint;
import com.devpos.hotelapp.async.AsyncEscPosPrint;
import com.devpos.hotelapp.async.AsyncEscPosPrinter;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class SettingPrinter extends AppCompatActivity {
    TextView saveData,test_print;

    Realm realm = Realm.getDefaultInstance();
    EditText tv_size_pp,tv_dpi,tv_per_line;

    public static final int PERMISSION_BLUETOOTH = 1;
    public static final int PERMISSION_BLUETOOTH_ADMIN = 2;
    public static final int PERMISSION_BLUETOOTH_CONNECT = 3;
    public static final int PERMISSION_BLUETOOTH_SCAN = 4;

    private BluetoothConnection selectedDevice;
    private ImageView backsetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_printer);
        tv_size_pp = findViewById(R.id.tv_size_pp);
        tv_dpi = findViewById(R.id.tv_dpi);
        tv_per_line = findViewById(R.id.tv_per_line);
        saveData = findViewById(R.id.saveData);
        test_print = findViewById(R.id.test_print);
        backsetting = findViewById(R.id.backsetting);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSaveData();
            }
        });

        RealmResults<PrintSetRm> printSetRms = realm.where(PrintSetRm.class).findAll();
        tv_size_pp.setText(String.valueOf(Math.round(printSetRms.get(0).getSizePaper())));
        tv_dpi.setText(String.valueOf(printSetRms.get(0).getDpiPrinter()));
        tv_per_line.setText(String.valueOf(printSetRms.get(0).getPerLine()));

        test_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testPP();
            }
        });

        backsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    public void browseBluetoothDevice() {
        final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();
        if (bluetoothDevicesList != null) {
            final String[] items = new String[bluetoothDevicesList.length + 1];
//            items[0] = "Default printer";
            Log.d("CHKDV","list : "+bluetoothDevicesList.length);
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
                Log.d("CHKDV","name : "+device.getDevice().getName());
                items[++i] = device.getDevice().getName();

            }

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("เลือกเครื่องพิมพ์");
            if(bluetoothDevicesList.length==0){
                items[0] = "ไม่พบเครื่องพิมพ์";
                alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
            }else{
                alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int index = i - 1;
                        if (index == -1) {
                            selectedDevice = null;
                        } else {
                            selectedDevice = bluetoothDevicesList[index];
                            setTestSend();
                        }
                    }
                });
            }

            AlertDialog alert = alertDialog.create();
            if(bluetoothDevicesList.length==0){
                alert.setCanceledOnTouchOutside(true);
            }else{
                alert.setCanceledOnTouchOutside(false);
            }
            alert.show();

        }
    }

    private void setTestSend() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PERMISSION_BLUETOOTH_ADMIN);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_BLUETOOTH_CONNECT);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
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
        SimpleDateFormat format = new SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss");
        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, printSetRms.get(0).getDpiPrinter(), printSetRms.get(0).getSizePaper(), printSetRms.get(0).getPerLine());
        return printer.addTextToPrint(
                "[L]\n" +
                        "[C]<u><font size='big'>ORDER 0000</font></u>\n" +
                        "[L]\n" +
                        "[C]<u type='double'>" + format.format(new Date()) + "</u>\n" +
                        "[C]\n" +
                        "[C]================================\n" +
                        "[L]\n" +
                        "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99€\n" +
                        "[L]  + Size : S\n" +
                        "[L]\n" +
                        "[L]<b>AWESOME HAT</b>[R]24.99€\n" +
                        "[L]  + Size : 57/58\n" +
                        "[L]\n" +
                        "[C]--------------------------------\n" +
                        "[R]TOTAL PRICE :[R]34.98€\n" +
                        "[R]TAX :[R]4.23€\n" +
                        "[L]\n" +
                        "[C]================================\n" +
                        "[L]\n" +
                        "[L]<u><font color='bg-black' size='tall'>Customer :</font></u>\n" +
                        "[L]Raymond DUPONT\n" +
                        "[L]5 rue des girafes\n" +
                        "[L]31547 PERPETES\n"
        );
    }


    private void testPP() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PERMISSION_BLUETOOTH_ADMIN);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_BLUETOOTH_CONNECT);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
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
                    browseBluetoothDevice();
                }
            }
        }
    }

    private void setSaveData() {
        float getSizeEd = Float.parseFloat(tv_size_pp.getText().toString());
        int dipEd = Integer.parseInt(tv_dpi.getText().toString());
        int perLineEd = Integer.parseInt(tv_per_line.getText().toString());
        realm.beginTransaction();
        PrintSetRm printSetRm = realm.where(PrintSetRm.class).findFirst();
        printSetRm.setSizePaper(getSizeEd);
        printSetRm.setDpiPrinter(dipEd);
        printSetRm.setPerLine(perLineEd);
        realm.commitTransaction();
        Toast.makeText(this, "บันทึกข้อมูลเรียบร้อย", Toast.LENGTH_SHORT).show();
    }
}