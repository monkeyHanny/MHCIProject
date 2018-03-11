package com.mhci.ax.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by monkeyhanny on 11/3/2018.
 */
@Entity(tableName = "favourite")
public class Favourite {
    @PrimaryKey(autoGenerate = true)
    private int favId;

    @ColumnInfo(name = "originalLanguage")
    private String originalLanguage;


    @ColumnInfo(name = "targetLanguage")
    private String target;


    @ColumnInfo(name = "translated")
    private String translated;


    @ColumnInfo(name = "originalText")
    private String originalText;


    public Favourite(String originalLanguage, String translated, String target, String originalText) {
        this.originalLanguage = originalLanguage;
        this.originalText = originalText;
        this.translated = translated;
        this.target = target;
    }

    public int getFavId() {
        return favId;
    }

    public void setFavId(int favId) {
        this.favId = favId;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getTranslated() {
        return translated;
    }

    public void setTranslated(String translated) {
        this.translated = translated;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
