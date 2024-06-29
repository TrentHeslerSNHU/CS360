package com.thesler.invdbmgr.ui.add;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddItemViewModel extends ViewModel {

    public AddItemViewModel() {
        //Do something
    }

    public LiveData<String> getItemName(String itemName) {
        MutableLiveData<String> mItemName = new MutableLiveData<>();
        mItemName.setValue(itemName);
        return mItemName;
    }
    public LiveData<String> getItemQty(String itemQty) {
        MutableLiveData<String> mItemQty = new MutableLiveData<>();
        mItemQty.setValue(itemQty);
        return mItemQty;
    }
}