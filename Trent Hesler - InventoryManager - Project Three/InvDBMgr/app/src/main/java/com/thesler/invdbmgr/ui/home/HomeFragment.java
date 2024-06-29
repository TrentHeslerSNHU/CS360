package com.thesler.invdbmgr.ui.home;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thesler.invdbmgr.util.InvDBMgr;
import com.thesler.invdbmgr.util.ItemRecyclerViewAdapter;
import com.thesler.invdbmgr.R;
import com.thesler.invdbmgr.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    RecyclerView mRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mRecyclerView = root.findViewById(R.id.itemList);
        InvDBMgr invDB = new InvDBMgr(getContext());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemRecyclerViewAdapter adapter = new ItemRecyclerViewAdapter(getContext(),invDB.getItems());

        //Set what happens when an item from the recycler is clicked
        adapter.setClickListener(new ItemRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Create a bundle to pass to the EditItem fragment
                Bundle editData = new Bundle();
                editData.putInt("itemId",adapter.getItem(position).getId());
                editData.putString("itemName",adapter.getItem(position).getName());
                editData.putInt("itemCount",adapter.getItem(position).getCount());

                //Go to the EditItem fragment to make changes
                Navigation.findNavController(view).navigate(R.id.editItemFragment,editData);

                //Alert the recycler that the dataset has changed
                adapter.notifyDataSetChanged();
            }
        });

        //Set/refresh the recycler when fragment view is created
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //Navigate to the AddItem fragment when you click the FAB
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.addItemFragment);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}