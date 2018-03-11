package com.mhci.ax.db;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.mhci.ax.db.entity.Favourite;
import com.mhci.ax.db.entity.Phrase;

import java.util.List;

/**
 * Created by monkeyhanny on 11/3/2018.
 */

public class ViewModel extends AndroidViewModel {
    private DataRepository mRepository;
    private LiveData<Phrase> mPhrase;


    public ViewModel(Application application) {
        super(application);
        mRepository = new DataRepository(application);
    }

    public String getPhrase(String originalTxt, String targetCode) {
        return mRepository.getPhraseByOriginal(originalTxt, targetCode);
    }

    public void insert(Phrase phrase) {
        mRepository.insert(phrase);
    }

    public void addFavourite(Favourite favourite) {
        mRepository.insertFav(favourite);
    }

    public List<Favourite> getAllFavourites(String originalCode, String targetCode) {
        return mRepository.getAllFav(originalCode, targetCode);
    }

    public void deleteFav(Favourite favourite) {
        mRepository.deleteFavourite(favourite);
    }
}
