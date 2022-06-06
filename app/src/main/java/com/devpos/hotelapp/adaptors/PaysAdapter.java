package com.devpos.hotelapp.adaptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devpos.hotelapp.R;
import com.devpos.hotelapp.models.PayModel;

import java.util.ArrayList;


public class PaysAdapter extends RecyclerView.Adapter<PaysAdapter.DailyViewHolder> {
    private Context mContext;
    private ArrayList<PayModel> mCateList;
    private RecyclerView.Adapter mAdapter;
    private int posCur =0;

    private OnClickChoose mOnClickChoose;
    public PaysAdapter(Context context, ArrayList<PayModel> data, OnClickChoose listener) {
        mContext = context;
        mCateList = data;
        this.mOnClickChoose = listener;
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_pay, viewGroup, false);
        final DailyViewHolder viewHolder = new DailyViewHolder(v, mOnClickChoose);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final DailyViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        final PayModel newsModel = mCateList.get(i);
        viewHolder.typePayShow.setText(newsModel.getTypePay());
        viewHolder.pricePayShow.setText(String.valueOf(newsModel.getPricePay())+".-");
        viewHolder.delRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickChoose.OnClickChoose(i,"");
            }
        });
    }

    public void removeAt(int position) {
        mCateList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mCateList.size());
    }

    public void insertAt(PayModel serviceModel) {
        mCateList.add(mCateList.size(), serviceModel);
        notifyDataSetChanged();
    }

    public void setNewDataList(ArrayList<PayModel> serviceModelArrayList){
        mCateList.clear();
        mCateList.addAll(serviceModelArrayList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mCateList.size();
    }


    public class DailyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView typePayShow,pricePayShow;
        ImageView delRow;

        public OnClickChoose onClickChoose;
        public DailyViewHolder(View itemView,OnClickChoose onClickChoose) {
            super(itemView);
            typePayShow = itemView.findViewById(R.id.typePayShow);
            delRow = itemView.findViewById(R.id.delRow);
            pricePayShow = itemView.findViewById(R.id.pricePayShow);
            itemView.setOnClickListener(this);
            this.onClickChoose = onClickChoose;
        }

        @Override
        public void onClick(View v) {

        }

    }


    public interface OnClickChoose{
        void OnClickChoose(int position, String keyCate);
    }

}
