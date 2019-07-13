package dev.mims.drudgereportviewer.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Map;

public class PageViewModel extends ViewModel {
    // Store List of Maps. Keys: title, url, img
    private MutableLiveData<Map<String, List<Map<String,String>>>> mLinkMap = new MutableLiveData<>();

    public void storeLinks(Map<String, List<Map<String,String>>> linkMap){
        mLinkMap.setValue(linkMap);
    }

    public LiveData<Map<String,List<Map<String,String>>>> getLinks() {return mLinkMap;}

}