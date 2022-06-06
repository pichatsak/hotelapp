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
import com.devpos.hotelapp.models.ServiceModel;

import java.util.ArrayList;


public class ServiceViewAdapter extends RecyclerView.Adapter<ServiceViewAdapter.DailyViewHolder> {
    private Context mContext;
    private ArrayList<ServiceModel> mCateList;
    private RecyclerView.Adapter mAdapter;
    private int posCur =0;

    private OnClickChoose mOnClickChoose;
    public ServiceViewAdapter(Context context, ArrayList<ServiceModel> data, OnClickChoose listener) {
        mContext = context;
        mCateList = data;
        this.mOnClickChoose = listener;
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_service_view, viewGroup, false);
        final DailyViewHolder viewHolder = new DailyViewHolder(v, mOnClickChoose);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final DailyViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        final ServiceModel newsModel = mCateList.get(i);
        viewHolder.tvSevice.setText(newsModel.getNameService());
        viewHolder.price.setText(String.valueOf(newsModel.getPrice())+".-");

    }

    public void removeAt(int position) {
        mCateList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mCateList.size());
    }

    public void insertAt(ServiceModel serviceModel) {
        mCateList.add(mCateList.size(), serviceModel);
        notifyDataSetChanged();
    }

    public void setNewDataList(ArrayList<ServiceModel> serviceModelArrayList){
        mCateList.clear();
        mCateList.addAll(serviceModelArrayList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mCateList.size();
    }


    public class DailyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvSevice,price;

        public OnClickChoose onClickChoose;
        public DailyViewHolder(View itemView,OnClickChoose onClickChoose) {
            super(itemView);
            tvSevice = itemView.findViewById(R.id.tvSevice);
            price = itemView.findViewById(R.id.price);
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
