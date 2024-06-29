package com.thesler.invdbmgr.ui.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.thesler.invdbmgr.databinding.FragmentEditItemBinding;

public class EditItemFragment extends Fragment {

    private FragmentEditItemBinding binding;
    public static String origItemName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EditItemViewModel editItemViewModel =
                new ViewModelProvider(this).get(EditItemViewModel.class);

        binding = FragmentEditItemBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Bind the TextView and EditText so we can change their contents
        final EditText editItemName = binding.editItemName;
        final TextView editItemQty = binding.itemQtyTV;

        //Store the item's original name, in case it gets changed on this screen
        origItemName = getArguments().getString("itemName");

        //Set text based on bundle
        editItemName.setText(getArguments().getString("itemName"));
        editItemViewModel.getItemQty(Integer.toString(getArguments().getInt("itemCount"))).observe(getViewLifecycleOwner(), editItemQty::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}