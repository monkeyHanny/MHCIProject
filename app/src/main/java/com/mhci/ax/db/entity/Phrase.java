package com.mhci.ax.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by monkeyhanny on 11/3/2018.
 */
@Entity(tableName = "phrase")
public class Phrase {
    @PrimaryKey(autoGenerate = true)
    private int phraseId;

    @ColumnInfo(name = "original")
    private String original;

    @ColumnInfo(name = "translated")
    private String translated;

    @ColumnInfo(name = "target")
    private String target;

    public Phrase(String original, String translated, String target) {
        this.original = original;
        this.translated = translated;
        this.target = target;
    }

    public int getPhraseId() {
        return phraseId;
    }

    public void setPhraseId(int phraseId) {
        this.phraseId = phraseId;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
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
