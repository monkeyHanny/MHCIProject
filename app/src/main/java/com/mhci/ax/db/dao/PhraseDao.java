package com.mhci.ax.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mhci.ax.db.entity.Phrase;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by monkeyhanny on 11/3/2018.
 */
@Dao
public interface PhraseDao {
    @Query("SELECT * FROM phrase")
    List<Phrase> getAll();

    @Query("SELECT * FROM phrase WHERE original = :original AND target = :target")
    Phrase loadByOriginal(String original,String target);

    @Insert(onConflict = REPLACE)
    void insert(Phrase phrase);

    @Delete
    void deletePhrase(Phrase phrase);

}
