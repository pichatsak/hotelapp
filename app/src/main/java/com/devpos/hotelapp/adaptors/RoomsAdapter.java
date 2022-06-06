package com.devpos.hotelapp.adaptors;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

import com.devpos.hotelapp.MainActivity;
import com.devpos.hotelapp.MyApplication;
import com.devpos.hotelapp.R;
import com.devpos.hotelapp.SettingRoomActivity;
import com.devpos.hotelapp.models.RoomModel;
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


public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.DailyViewHolder> {
    private Context mContext;
    private ArrayList<RoomModel> mRoomList;
    private RecyclerView.Adapter mAdapter;
    private int posCur = 0;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private OnClickChoose mOnClickChoose;

    public RoomsAdapter(Context context, ArrayList<RoomModel> data, OnClickChoose listener) {
        mContext = context;
        mRoomList = data;
        this.mOnClickChoose = listener;
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_rooms, viewGroup, false);
        final DailyViewHolder viewHolder = new DailyViewHolder(v, mOnClickChoose);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final DailyViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        final RoomModel roomModel = mRoomList.get(i);
        viewHolder.tvRoomName.setText(roomModel.getRoomName());
        if (i == 0) {
            viewHolder.contRoom.setVisibility(View.GONE);
            viewHolder.contAdd.setVisibility(View.VISIBLE);
        } else {
            viewHolder.contRoom.setVisibility(View.VISIBLE);
            viewHolder.contAdd.setVisibility(View.GONE);
        }

        db.collection("rents")
                .whereEqualTo("userId", MyApplication.getUser_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> friendsMap = (Map<String, Object>) document.getData().get("listRents");
                                if(friendsMap.size()>0){
                                    boolean exits = false;
                                    for (Map.Entry<String, Object> entry : friendsMap.entrySet()) {
                                        String key = entry.getKey();
                                        Map<String, Object> value = (Map<String, Object>) entry.getValue();
                                        if(roomModel.getRoomId().equals(value.get("roomId").toString())){
                                            Timestamp timeStart = (Timestamp) value.get("dateStart");
                                            Timestamp timeEnd = (Timestamp) value.get("dateEnd");
                                            Calendar curDate = Calendar.getInstance();
                                            Date dateCur = curDate.getTime();
                                            Date dateStart = timeStart.toDate();
                                            Date dateEnd = timeEnd.toDate();
                                            if(dateCur.getTime()>=dateStart.getTime()&&dateCur.getTime()<=dateEnd.getTime()){
                                                exits = true;
                                                Log.d("CHKRENTS","room : "+roomModel.getRoomName()+" is busy");
                                                viewHolder.contRoom.setBackgroundResource(R.drawable.list);
                                                viewHolder.tvRoomName.setTextColor(mContext.getResources().getColor(R.color.white));
                                                viewHolder.contFree.setVisibility(View.GONE);
                                                viewHolder.contBusy.setVisibility(View.VISIBLE);
                                                viewHolder.contRoom.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialogRent(roomModel,"busy",key);
                                                    }
                                                });
                                            }
                                        }
                                    }
                                    if(!exits){
                                        viewHolder.contRoom.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialogRent(roomModel,"free","");
                                            }
                                        });
                                    }
                                }else{
                                    viewHolder.contRoom.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialogRent(roomModel,"free","");
                                        }
                                    });
                                }

                            }
                        }
                    }
                });

        viewHolder.contAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickChoose.OnClickChoose(i, "add", roomModel);
            }
        });

        viewHolder.contRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (roomModel.getStatusRoom().equals("free")) {
                    dialogRent(roomModel,"free","");
                }
            }
        });
    }

    public void dialogRent(RoomModel roomModel,String status,String rentId) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_menu);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Display display = ((WindowManager) mContext.getSystemService(mContext.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        Log.v("width", width + "");
        dialog.getWindow().setLayout((6 * width) / 9, (6 * height) / 9);
        TextView cancle = dialog.findViewById(R.id.cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        LinearLayout btnRent = dialog.findViewById(R.id.btnRent);
        LinearLayout btnStatusRoom = dialog.findViewById(R.id.btnStatusRoom);
        LinearLayout btnCheckOut = dialog.findViewById(R.id.btnCheckOut);
        LinearLayout btnViewRent = dialog.findViewById(R.id.btnViewRent);
        LinearLayout btnRentAfter = dialog.findViewById(R.id.btnRentAfter);
        LinearLayout btnCancel = dialog.findViewById(R.id.btnCancel);
        LinearLayout editRoom = dialog.findViewById(R.id.editRoom);
        TextView tvNameRoom = dialog.findViewById(R.id.tvNameRoom);
        LinearLayout conManage = dialog.findViewById(R.id.conManage);
        LinearLayout delRoom = dialog.findViewById(R.id.delRoom);
        tvNameRoom.setText("ห้อง " + roomModel.getRoomName());
        if (status.equals("free")) {
            conManage.setVisibility(View.VISIBLE);
            btnRent.setVisibility(View.VISIBLE);
            btnStatusRoom.setVisibility(View.VISIBLE);
            btnRent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnClickChoose.OnClickChoose(0, "rent", roomModel);
                    dialog.dismiss();
                }
            });
            editRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnClickChoose.OnClickChoose(0, "editRoom", roomModel);
                    dialog.dismiss();
                }
            });
            delRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnClickChoose.OnClickChoose(0, "delRoom", roomModel);
                    dialog.dismiss();
                }
            });
        }else if(status.equals("busy")){
            btnRentAfter.setVisibility(View.VISIBLE);
            btnCheckOut.setVisibility(View.VISIBLE);
            btnStatusRoom.setVisibility(View.VISIBLE);
            btnViewRent.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            btnRentAfter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnClickChoose.OnClickChoose(0, "rent", roomModel);
                    dialog.dismiss();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyApplication.setRentIdViewCur(rentId);
                    mOnClickChoose.OnClickChoose(0, "cancelRent", roomModel);
                    dialog.dismiss();
                }
            });
            btnViewRent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyApplication.setRentIdViewCur(rentId);
                    mOnClickChoose.OnClickChoose(0, "viewRent", roomModel);
                }
            });
            btnCheckOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnClickChoose.OnClickChoose(0, "checkOut", roomModel);
                    dialog.dismiss();
                }
            });
        }
        btnStatusRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickChoose.OnClickChoose(0, "viewStatusRoom", roomModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mRoomList.size();
    }


    public class DailyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvRoomName,contFree;
        LinearLayout contAdd, contRoom;
        ImageView contBusy;

        public OnClickChoose onClickChoose;

        public DailyViewHolder(View itemView, OnClickChoose onClickChoose) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            contFree = itemView.findViewById(R.id.contFree);
            contRoom = itemView.findViewById(R.id.contRoom);
            contAdd = itemView.findViewById(R.id.contAdd);
            contBusy = itemView.findViewById(R.id.contBusy);
            itemView.setOnClickListener(this);
            this.onClickChoose = onClickChoose;
        }

        @Override
        public void onClick(View v) {

        }

    }


    public interface OnClickChoose {
        void OnClickChoose(int position, String statusClick, RoomModel roomModel);
    }

}
