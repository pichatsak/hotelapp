package com.devpos.hotelapp.dialog;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.devpos.hotelapp.R;
import com.devpos.hotelapp.registorActivity;

import io.realm.Realm;

public class DialogPrint {
    private Context context;
    private String rentId;


    private OnClickDialog mOnClick;

    public DialogPrint(Context context, String rentId, OnClickDialog listener) {
        this.context = context;
        this.rentId = rentId;
        this.mOnClick = listener;
    }

    public interface OnClickDialog {
        void OnConfirm();
    }

    public void openDialog(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_print);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        ImageView closedialog = dialog.findViewById(R.id.closedialog);
        closedialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        TextView printBillSuc = dialog.findViewById(R.id.printBillSuc);
        printBillSuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mOnClick.OnConfirm();

            }
        });


    }

}
