package com.example.android2project;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ItemViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<String>> selectedItem = new MutableLiveData<ArrayList<String>>();

    public void setData(ArrayList<String> item)
    {
        selectedItem.setValue(item);
    }

    public LiveData<ArrayList<String>> getSelectedItem()
    {
        return selectedItem;
    }
}
