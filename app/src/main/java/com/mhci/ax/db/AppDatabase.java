package com.mhci.ax.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.mhci.ax.db.dao.FavouriteDao;
import com.mhci.ax.db.dao.PhraseDao;
import com.mhci.ax.db.entity.Favourite;
import com.mhci.ax.db.entity.Phrase;

/**
 * Created by monkeyhanny on 11/3/2018.
 */
@Database(entities = {Phrase.class, Favourite.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "translation-db";
    private static AppDatabase sInstance;

    public abstract PhraseDao phraseDao();

    public abstract FavouriteDao favouriteDao();


    public static AppDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        return sInstance;
    }


}
