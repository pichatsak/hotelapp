package com.devpos.hotelapp.adaptors;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devpos.hotelapp.MyApplication;
import com.devpos.hotelapp.R;
import com.devpos.hotelapp.models.RoomModelSearch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;


public class RoomSearchAdapter extends RecyclerView.Adapter<RoomSearchAdapter.DailyViewHolder> {
    private Context mContext;
    private ArrayList<RoomModelSearch> mRoomList;
    private RecyclerView.Adapter mAdapter;
    private int posCur = 0;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private OnClickChoose mOnClickChoose;

    public RoomSearchAdapter(Context context, ArrayList<RoomModelSearch> data, OnClickChoose listener) {
        mContext = context;
        mRoomList = data;
        this.mOnClickChoose = listener;
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_room_search, viewGroup, false);
        final DailyViewHolder viewHolder = new DailyViewHolder(v, mOnClickChoose);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final DailyViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        final RoomModelSearch roomModel = mRoomList.get(i);
        viewHolder.tvRoomName.setText(roomModel.getRoomName());
        viewHolder.contFree.setText(roomModel.getCateName());

        viewHolder.contRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickChoose.OnClickChoose(0, "rent", roomModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRoomList.size();
    }


    public class DailyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvRoomName,contFree;
        LinearLayout contRoom;

        public OnClickChoose onClickChoose;

        public DailyViewHolder(View itemView, OnClickChoose onClickChoose) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            contFree = itemView.findViewById(R.id.contFree);
            contRoom = itemView.findViewById(R.id.contRoom);
            itemView.setOnClickListener(this);
            this.onClickChoose = onClickChoose;
        }

        @Override
        public void onClick(View v) {

        }

    }


    public interface OnClickChoose {
        void OnClickChoose(int position, String statusClick, RoomModelSearch roomModel);
    }

}
