package com.mhci.ax.db.dao;

/**
 * Created by monkeyhanny on 11/3/2018.
 */

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mhci.ax.db.entity.Favourite;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface FavouriteDao {
    @Query("SELECT * FROM favourite WHERE originalLanguage = :original AND targetLanguage = :target")
    List<Favourite> getAll(String original, String target);

    @Insert(onConflict = REPLACE)
    void insert(Favourite favourite);

    @Query("DELETE FROM favourite WHERE originalText = :originalTxt AND targetLanguage = :target")
    void deleteFav(String originalTxt, String target);
}
