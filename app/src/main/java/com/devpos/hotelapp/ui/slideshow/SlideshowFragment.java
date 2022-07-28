package com.devpos.hotelapp.ui.slideshow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.devpos.hotelapp.MainActivity;
import com.devpos.hotelapp.MyApplication;
import com.devpos.hotelapp.R;
import com.devpos.hotelapp.ChangPasswordActivity;
import com.devpos.hotelapp.SettingBillActivity;
import com.devpos.hotelapp.SettingPrinter;
import com.devpos.hotelapp.databinding.FragmentSlideshowBinding;
import com.devpos.hotelapp.loginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private ImageView backhome;
    private LinearLayout SettingUser;
    private LinearLayout sign_out,setPrinter,setBill;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        backhome = root.findViewById(R.id.backhome);
        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.setIsBackFromFragment(true);
                Log.d("CHKBACK","set bak : "+MyApplication.isIsBackFromFragment());
                getActivity().onBackPressed();
            }
        });

        SettingUser = root.findViewById(R.id.SettingUser);
        SettingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent go = new Intent(getActivity(), ChangPasswordActivity.class);
                    startActivity(go);

            }
        });

        sign_out = root.findViewById(R.id.sign_out);
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), loginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        setPrinter = root.findViewById(R.id.setPrinter);
        setPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingPrinter.class);
                startActivity(intent);
            }
        });

        setBill = root.findViewById(R.id.setBill);
        setBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingBillActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}