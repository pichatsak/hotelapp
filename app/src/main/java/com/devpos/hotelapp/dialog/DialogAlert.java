package com.devpos.hotelapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.devpos.hotelapp.R;

public class DialogAlert {
    private Context context;
    private String txt;
    private String status;

    public DialogAlert(Context context, String txt, String status) {
        this.context = context;
        this.txt = txt;
        this.status = status;
    }

    public DialogAlert openDialog(){
        if(status.equals("warning")){
            getShowWarning();
        }else if(status.equals("error")){
            getShowError();
        }
        return null;
    }

    private void getShowError() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_error);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView btnok = dialog.findViewById(R.id.btnok);
        tvTitle.setText(txt);
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void getShowWarning() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_warning);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView btnok = dialog.findViewById(R.id.btnok);
        tvTitle.setText(txt);
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

}
