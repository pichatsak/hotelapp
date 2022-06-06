package com.devpos.hotelapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.devpos.hotelapp.R;

public class DialogConfirm {
    private Context context;
    private String status;
    private String textShow;
    private OnClickDialog mOnClick;

    public DialogConfirm(Context context, String status,String textShow,OnClickDialog listener) {
        this.context = context;
        this.status = status;
        this.mOnClick = listener;
        this.textShow = textShow;
    }

    public DialogConfirm openDialog(){
        if(status.equals("delete")){
            getShowDelete();
        }
        return null;
    }

    private void getShowDelete() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_delete);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView cancle = dialog.findViewById(R.id.cancle);
        TextView confirm = dialog.findViewById(R.id.confirm);
        tvTitle.setText(textShow);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mOnClick.OnConfirm();
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mOnClick.OnCancel();
            }
        });

    }

    public interface OnClickDialog {
        void OnConfirm();
        void OnCancel();
    }

}
