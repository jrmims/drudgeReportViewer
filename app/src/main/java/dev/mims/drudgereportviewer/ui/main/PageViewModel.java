package dev.mims.drudgereportviewer.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Map;

import dev.mims.drudgereportviewer.DrudgeItem;

public class PageViewModel extends ViewModel {
    // Store List of Maps. Keys: title, url, img
    private MutableLiveData<Map<String, List<DrudgeItem>>> mLinkMap = new MutableLiveData<>();

    public void storeLinks(Map<String, List<DrudgeItem>> linkMap){
        mLinkMap.setValue(linkMap);
    }

    public LiveData<Map<String,List<DrudgeItem>>> getLinks() {return mLinkMap;}

}