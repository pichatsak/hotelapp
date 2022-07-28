package com.devpos.hotelapp.adaptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devpos.hotelapp.R;
import com.devpos.hotelapp.models.CateRoomModels;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;


public class CateRoomAdapter extends RecyclerView.Adapter<CateRoomAdapter.DailyViewHolder> {
    private Context mContext;
    private ArrayList<CateRoomModels> mCateList;
    private RecyclerView.Adapter mAdapter;
    private int posCur =0;

    private OnClickChoose mOnClickChoose;
    public CateRoomAdapter(Context context, ArrayList<CateRoomModels> data,OnClickChoose listener) {
        mContext = context;
        mCateList = data;
        this.mOnClickChoose = listener;
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_cate_room, viewGroup, false);
        final DailyViewHolder viewHolder = new DailyViewHolder(v, mOnClickChoose);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final DailyViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        final CateRoomModels newsModel = mCateList.get(i);
        viewHolder.tvCateName.setText(newsModel.getCateName());
        if(posCur==i){
            viewHolder.contMain.setBackground(mContext.getDrawable(R.drawable.list));
            viewHolder.tvCateName.setTextColor(mContext.getResources().getColor(R.color.white));
        }else{
            viewHolder.contMain.setBackground(mContext.getDrawable(R.drawable.list1));
            viewHolder.tvCateName.setTextColor(mContext.getResources().getColor(R.color.main));
        }
        viewHolder.contMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posCur = i;
                notifyDataSetChanged();
                mOnClickChoose.OnClickChoose(i,newsModel.getCateKey(),"view");
            }
        });
        viewHolder.contMain.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                mOnClickChoose.OnClickChoose(i,newsModel.getCateKey(),"edit");
                return true;
            }

        });

    }


    @Override
    public int getItemCount() {
        return mCateList.size();
    }


    public class DailyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvCateName;
        LinearLayout contMain;

        public OnClickChoose onClickChoose;
        public DailyViewHolder(View itemView,OnClickChoose onClickChoose) {
            super(itemView);
            tvCateName = itemView.findViewById(R.id.tvCateName);
            contMain = itemView.findViewById(R.id.contMain);
            itemView.setOnClickListener(this);
            this.onClickChoose = onClickChoose;
        }

        @Override
        public void onClick(View v) {

        }

    }


    public interface OnClickChoose{
        void OnClickChoose(int position, String keyCate,String status);
    }

}
