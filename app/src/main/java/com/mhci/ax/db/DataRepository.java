package com.mhci.ax.db;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.mhci.ax.db.dao.FavouriteDao;
import com.mhci.ax.db.dao.PhraseDao;
import com.mhci.ax.db.entity.Favourite;
import com.mhci.ax.db.entity.Phrase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by monkeyhanny on 11/3/2018.
 */

public class DataRepository {
    private PhraseDao mPhraseDao;
    private FavouriteDao mFavouriteDao;


    public DataRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        mPhraseDao = db.phraseDao();
        mFavouriteDao = db.favouriteDao();
    }

    public String getPhraseByOriginal(String originalTxt, String targetCode) {
        String translated;
        try {
            translated = new getPhraseByOriginalAsyncTask(mPhraseDao).execute(originalTxt, targetCode).get();
            return translated;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "";

    }

    private static class getPhraseByOriginalAsyncTask extends AsyncTask<String, Void, String> {

        private PhraseDao mAsyncTaskDao;

        getPhraseByOriginalAsyncTask(PhraseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected String doInBackground(final String... params) {
            Phrase mPhrase = mAsyncTaskDao.loadByOriginal(params[0], params[1]);
            try {
                Log.v("getPhraseAsyncTask", "mPhrases: " + mPhrase.getOriginal() + " target code: " + mPhrase.getTarget());
                String txt = mPhrase.getTranslated();
                return txt;
            } catch (Exception e) {
                return "";
            }

        }

    }

    public void insert(Phrase phrase) {
        new insertAsyncTask(mPhraseDao).execute(phrase);
    }


    private static class insertAsyncTask extends AsyncTask<Phrase, Void, Void> {

        private PhraseDao mAsyncTaskDao;

        insertAsyncTask(PhraseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Phrase... params) {
            mAsyncTaskDao.insert(params[0]);
            Phrase p = mAsyncTaskDao.getAll().get(0);
            Log.v("insertAsyncTask", "mPhrases: " + mAsyncTaskDao.getAll().size() + "  " + p.getOriginal() + p.getTranslated());
            return null;
        }
    }


    public void insertFav(Favourite favourite) {
        new insertFavAsyncTask(mFavouriteDao).execute(favourite);
    }


    private static class insertFavAsyncTask extends AsyncTask<Favourite, Void, Void> {

        private FavouriteDao mAsyncTaskDao;

        insertFavAsyncTask(FavouriteDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Favourite... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    public List<Favourite> getAllFav(String originalLanguage, String targetLanguage) {
        List<Favourite> mFavouriteList = new ArrayList<>();
        try {
            mFavouriteList = new getAllFavAsyncTask(mFavouriteDao).execute(originalLanguage, targetLanguage).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return mFavouriteList;
    }

    private static class getAllFavAsyncTask extends AsyncTask<String, Void, List<Favourite>> {

        private FavouriteDao mAsyncTaskDao;

        getAllFavAsyncTask(FavouriteDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected List<Favourite> doInBackground(final String... params) {

            List<Favourite> mFavList = new ArrayList<>();
            try {
                mFavList = mAsyncTaskDao.getAll(params[0], params[1]);

            } catch (Exception e) {
                Log.v("All Fav Task", "e: " + e.getMessage());
            }
            return mFavList;

        }

    }

    public void deleteFavourite(Favourite favourite) {
        new deleteFavAsyncTask(mFavouriteDao).execute(favourite.getOriginalText(), favourite.getTarget());
    }

    private static class deleteFavAsyncTask extends AsyncTask<String, Void, Void> {

        private FavouriteDao mAsyncTaskDao;

        deleteFavAsyncTask(FavouriteDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            mAsyncTaskDao.deleteFav(params[0], params[1]);
            return null;
        }
    }
}
