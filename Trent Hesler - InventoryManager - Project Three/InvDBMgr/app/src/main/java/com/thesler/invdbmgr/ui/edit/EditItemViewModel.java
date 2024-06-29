package com.thesler.invdbmgr.ui.edit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditItemViewModel extends ViewModel {

    public EditItemViewModel() {
        //Do something
    }

    //Set the item quantity TextView's text
    public LiveData<String> getItemQty(String itemQty) {
        MutableLiveData<String> mItemQty = new MutableLiveData<>();
        mItemQty.setValue("New item quantity (currently " + itemQty + "): ");
        return mItemQty;
    }
}