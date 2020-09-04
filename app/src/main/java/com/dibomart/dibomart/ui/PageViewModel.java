package com.dibomart.dibomart.ui;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    private static MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private static MutableLiveData<Integer> itemCount = new MutableLiveData<>();
    private LiveData<Integer> mText = Transformations.map(mIndex, new Function<Integer, Integer>() {
        @Override
        public Integer apply(Integer input) {
            return input;
        }
    });

    private LiveData<Integer> mItem = Transformations.map(itemCount, new Function<Integer, Integer>() {
        @Override
        public Integer apply(Integer input) {
            return input;
        }
    });

    public static void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<Integer> getText() {
        return mText;
    }

    public static void setitemIndex(int index) {
        itemCount.setValue(index);
    }

    public LiveData<Integer> getItemText() {
        return mItem;
    }

}