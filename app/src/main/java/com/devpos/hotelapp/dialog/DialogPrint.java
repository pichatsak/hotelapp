package com.devpos.hotelapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.devpos.hotelapp.R;
import com.devpos.hotelapp.registorActivity;

public class DialogPrint {
    private Context context;
    private String rentId;

    public DialogPrint(Context context, String rentId) {
        this.context = context;
        this.rentId = rentId;
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


    }
}
