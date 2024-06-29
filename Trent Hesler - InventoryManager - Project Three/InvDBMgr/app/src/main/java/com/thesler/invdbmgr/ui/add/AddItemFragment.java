package com.thesler.invdbmgr.ui.add;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.thesler.invdbmgr.databinding.FragmentAddItemBinding;

public class AddItemFragment extends Fragment {

    private FragmentAddItemBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddItemViewModel addItemViewModel =
                new ViewModelProvider(this).get(AddItemViewModel.class);
        binding = FragmentAddItemBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}