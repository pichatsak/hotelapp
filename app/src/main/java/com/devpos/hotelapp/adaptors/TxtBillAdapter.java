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
import com.devpos.hotelapp.models.TextFinalBillModel;

import java.util.ArrayList;


public class TxtBillAdapter extends RecyclerView.Adapter<TxtBillAdapter.DailyViewHolder> {
    private Context mContext;
    private ArrayList<TextFinalBillModel> mCateList;
    private RecyclerView.Adapter mAdapter;
    private int posCur =0;

    private OnClickChoose mOnClickChoose;
    public TxtBillAdapter(Context context, ArrayList<TextFinalBillModel> data, OnClickChoose listener) {
        mContext = context;
        mCateList = data;
        this.mOnClickChoose = listener;
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_txt_bill, viewGroup, false);
        final DailyViewHolder viewHolder = new DailyViewHolder(v, mOnClickChoose);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final DailyViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        final TextFinalBillModel newsModel = mCateList.get(i);

        viewHolder.typeShow.setText(newsModel.getTxtShow());
//        viewHolder.pricePayShow.setText(String.valueOf(newsModel.getPricePay())+".-");
        viewHolder.delRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickChoose.OnClickChoose(i,"del");
            }
        });

        viewHolder.editRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickChoose.OnClickChoose(i,"edit");
            }
        });
    }

    public void removeAt(int position) {
        mCateList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mCateList.size());
    }

    public void insertAt(TextFinalBillModel serviceModel) {
        mCateList.add(mCateList.size(), serviceModel);
        notifyDataSetChanged();
    }

    public void setNewDataList(ArrayList<TextFinalBillModel> serviceModelArrayList){
        mCateList.clear();
        mCateList.addAll(serviceModelArrayList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mCateList.size();
    }


    public class DailyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView typeShow;
        ImageView delRow,editRow;

        public OnClickChoose onClickChoose;
        public DailyViewHolder(View itemView,OnClickChoose onClickChoose) {
            super(itemView);
            typeShow = itemView.findViewById(R.id.typeShow);
            delRow = itemView.findViewById(R.id.delRow);
            editRow = itemView.findViewById(R.id.editRow);
            itemView.setOnClickListener(this);
            this.onClickChoose = onClickChoose;
        }

        @Override
        public void onClick(View v) {

        }

    }


    public interface OnClickChoose{
        void OnClickChoose(int position, String status);
    }

}
