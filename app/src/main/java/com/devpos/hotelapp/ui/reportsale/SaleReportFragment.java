package com.devpos.hotelapp.ui.reportsale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.devpos.hotelapp.R;
import com.devpos.hotelapp.databinding.FragmentGalleryBinding;
import com.devpos.hotelapp.databinding.FragmentSalereportBinding;
import com.devpos.hotelapp.ui.gallery.GalleryViewModel;

public class SaleReportFragment extends Fragment {

    private FragmentSalereportBinding binding;

    TextView dateStart,dateEnd,getBtn;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentSalereportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dateStart = root.findViewById(R.id.dateStart);
        dateEnd = root.findViewById(R.id.dateEnd);
        getBtn = root.findViewById(R.id.getBtn);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}