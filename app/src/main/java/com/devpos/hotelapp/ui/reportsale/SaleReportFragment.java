package com.devpos.hotelapp.ui.reportsale;

import static com.google.firebase.firestore.DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.devpos.hotelapp.MyApplication;
import com.devpos.hotelapp.R;
import com.devpos.hotelapp.SettingRoomActivity;
import com.devpos.hotelapp.databinding.FragmentGalleryBinding;
import com.devpos.hotelapp.databinding.FragmentSalereportBinding;
import com.devpos.hotelapp.models.RoomModelView;
import com.devpos.hotelapp.models.ServiceModel;
import com.devpos.hotelapp.ui.gallery.GalleryViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import ir.androidexception.datatable.DataTable;
import ir.androidexception.datatable.model.DataTableHeader;
import ir.androidexception.datatable.model.DataTableRow;

public class SaleReportFragment extends Fragment {

    private FragmentSalereportBinding binding;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Calendar dateStartChoose = null;
    private Calendar dateEndChoose = null;
    DataTable dataTable;
    TextView dateStart, dateEnd, getBtn;
    ArrayList<RoomModelView> roomModelViewArrayList = new ArrayList<>();
    ImageView backhome;
    boolean statusDataStart = false;
    boolean statusDataEnd = false;
    DecimalFormat df = new DecimalFormat("#,###.00");


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSalereportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dateStart = root.findViewById(R.id.dateStart);
        dateEnd = root.findViewById(R.id.dateEnd);
        getBtn = root.findViewById(R.id.getBtn);
        dataTable = root.findViewById(R.id.data_table);
        backhome = root.findViewById(R.id.backhome);

        getDataRoom();
        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.setIsBackFromFragment(true);
                Log.d("CHKBACK", "set bak : " + MyApplication.isIsBackFromFragment());
                getActivity().onBackPressed();
            }
        });

        getBtn.setOnClickListener(view -> {
            Log.d("CHKDATE", dateStart.getText().toString() + ":" + dateEnd.getText().toString());
            if (dateStart.getText().toString().equals("") || dateEnd.getText().toString().equals("")) {
                Toast.makeText(getContext(), "กรุณาเลือกวันที่ให้ถูกต้อง", Toast.LENGTH_SHORT).show();
            } else {
                getDataShow();
            }
        });

        dateStart.setOnClickListener(view -> {
            final Calendar currentDate = Calendar.getInstance();
            dateStartChoose = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (datePicker, year, monthOfYear, dayOfMonth) -> {
                dateStartChoose.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                    dateStartChoose.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    dateStartChoose.set(Calendar.MINUTE, minute);
                    dateStart.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dateStartChoose.getTime()));
                    statusDataStart = true;
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();

            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
            datePickerDialog.show();
        });
        dateEnd.setOnClickListener(view -> {
            final Calendar currentDate = Calendar.getInstance();
            dateEndChoose = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                    dateEndChoose.set(year, monthOfYear, dayOfMonth);
                    new TimePickerDialog(getContext(), (view12, hourOfDay, minute) -> {
                        dateEndChoose.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateEndChoose.set(Calendar.MINUTE, minute);
                        dateEnd.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dateEndChoose.getTime()));
                        statusDataEnd = true;
                    }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();

                }
            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
            datePickerDialog.getDatePicker().setMinDate(dateStartChoose.getTimeInMillis());
            datePickerDialog.show();
        });
        return root;
    }

    private void getDataRoom() {
        db.collection("rooms")
                .whereEqualTo("userId", MyApplication.getUser_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> roomMap = (Map<String, Object>) document.getData().get("listRoom");
                                roomModelViewArrayList = new ArrayList<>();
                                for (Map.Entry<String, Object> entry : roomMap.entrySet()) {
                                    String key = entry.getKey();
                                    Map<String, Object> value = (Map<String, Object>) entry.getValue();
                                    RoomModelView roomModelView = new RoomModelView();
                                    roomModelView.setRoomName(value.get("roomName").toString());
                                    roomModelView.setKey(key);
                                    roomModelViewArrayList.add(roomModelView);
                                }

                            }
                        }
                    }
                });
    }

    private void getDataShow() {
        db.collection("rents")
                .whereEqualTo("userId", MyApplication.getUser_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> rentMap = (Map<String, Object>) document.getData().get("listRents");
                                DataTableHeader header = new DataTableHeader.Builder()
                                        .item("วันเวลา", 14)
                                        .item("ห้อง", 14)
                                        .item("ผู้เช่า", 14)
                                        .item("ยอดรวม", 14).build();
                                ArrayList<DataTableRow> rows = new ArrayList<>();
                                int numItem =0;
                                for (Map.Entry<String, Object> entry : rentMap.entrySet()) {
                                    String key = entry.getKey();
                                    Map<String, Object> value = (Map<String, Object>) entry.getValue();
                                    String keyRoom = value.get("roomId").toString();

                                    Timestamp timeStampGet = (Timestamp) value.get("dateCreate");
                                    Date dateSale = timeStampGet.toDate();


                                    if (dateSale.getTime() >= dateStartChoose.getTimeInMillis() && dateSale.getTime()<= dateEndChoose.getTimeInMillis()) {
                                        for (RoomModelView roomModelViewGet : roomModelViewArrayList) {
                                            if (roomModelViewGet.getKey().equals(keyRoom)) {
                                                numItem++;
                                                Gson gson = new Gson();
                                                Type type = new TypeToken<ArrayList<ServiceModel>>() {
                                                }.getType();
                                                ArrayList<ServiceModel> serviceModelsGet = gson.fromJson(value.get("listService").toString(), type);
                                                float totalSv = 0;
                                                for (ServiceModel serviceModel : serviceModelsGet) {
                                                    totalSv += serviceModel.getPrice();
                                                }
                                                float totalAll = Float.parseFloat(value.get("totalRentRoom").toString()) + totalSv;
                                                Timestamp timeStamp = (Timestamp) value.get("dateCreate");
                                                Date date = timeStamp.toDate();

                                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                                String dateString = formatter.format(date);

                                                DataTableRow row = new DataTableRow.Builder()
                                                        .value(dateString)
                                                        .value(roomModelViewGet.getRoomName())
                                                        .value(value.get("rentName").toString())
                                                        .value(df.format(totalAll))
                                                        .build();
                                                rows.add(row);
                                                break;
                                            }
                                        }
                                    }

                                }


                                getOld(rows,header);
                            }
                        }
                    }
                });

    }

    public void getOld(ArrayList<DataTableRow> rows,DataTableHeader header){
        db.collection("rentsEnd")
                .whereEqualTo("userId", MyApplication.getUser_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> rentMap = (Map<String, Object>) document.getData().get("listRents");
                                DataTableHeader header = new DataTableHeader.Builder()
                                        .item("วันเวลา", 14)
                                        .item("ห้อง", 14)
                                        .item("ผู้เช่า", 14)
                                        .item("ยอดรวม", 14).build();
                                ArrayList<DataTableRow> rows = new ArrayList<>();
                                int numItem =0;
                                for (Map.Entry<String, Object> entry : rentMap.entrySet()) {
                                    String key = entry.getKey();
                                    Map<String, Object> value = (Map<String, Object>) entry.getValue();
                                    String keyRoom = value.get("roomId").toString();

                                    Timestamp timeStampGet = (Timestamp) value.get("dateCreate");
                                    Date dateSale = timeStampGet.toDate();


                                    if (dateSale.getTime() >= dateStartChoose.getTimeInMillis() && dateSale.getTime()<= dateEndChoose.getTimeInMillis()) {
                                        for (RoomModelView roomModelViewGet : roomModelViewArrayList) {
                                            if (roomModelViewGet.getKey().equals(keyRoom)) {
                                                numItem++;
                                                Gson gson = new Gson();
                                                Type type = new TypeToken<ArrayList<ServiceModel>>() {
                                                }.getType();
                                                ArrayList<ServiceModel> serviceModelsGet = gson.fromJson(value.get("listService").toString(), type);
                                                float totalSv = 0;
                                                for (ServiceModel serviceModel : serviceModelsGet) {
                                                    totalSv += serviceModel.getPrice();
                                                }
                                                float totalAll = Float.parseFloat(value.get("totalRentRoom").toString()) + totalSv;
                                                Timestamp timeStamp = (Timestamp) value.get("dateCreate");
                                                Date date = timeStamp.toDate();

                                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                                String dateString = formatter.format(date);

                                                DataTableRow row = new DataTableRow.Builder()
                                                        .value(dateString)
                                                        .value(roomModelViewGet.getRoomName())
                                                        .value(value.get("rentName").toString())
                                                        .value(df.format(totalAll))
                                                        .build();
                                                rows.add(row);
                                                break;
                                            }
                                        }
                                    }

                                }

                            }
                            dataTable.setHeaderTextSize(12);
                            dataTable.setHeader(header);
                            dataTable.setRowTextSize(12);
                            dataTable.setRows(rows);
                            dataTable.inflate(getContext());
                        }
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}