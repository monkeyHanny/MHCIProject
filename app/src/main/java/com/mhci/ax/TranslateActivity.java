package com.mhci.ax;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.Translation;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mhci.ax.services.Utils.api;

/**
 * Created by monkeyhanny on 9/3/2018.
 */

public class TranslateActivity extends Activity implements View.OnClickListener {
    private EditText etInput;
    private Button btnOCR, btnSpeech, btnClear, btnFav, btnEdit, btnSpeaker;
    private TextView txtTranslated, txtKeyword;
    private LinearLayout btnContainer, translatedContainer;
    private CardView imgCardview;
    private ImageView imgRelated;
    private static String API_KEY = "AIzaSyAI4w45k1d98TWDhtcPg0BhmYm5K831QM8";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        etInput = findViewById(R.id.etInput);
        btnOCR = findViewById(R.id.btnOcr);
        btnSpeech = findViewById(R.id.btnSpeech);
        btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);
        btnFav = findViewById(R.id.btnFav);
        btnEdit = findViewById(R.id.btnEdit);
        btnSpeaker = findViewById(R.id.btnSpeaker);
        btnContainer = findViewById(R.id.btnContainer);
        translatedContainer = findViewById(R.id.translatedContentContainer);
        txtTranslated = findViewById(R.id.tvTranslated);
        imgCardview = findViewById(R.id.imgCardview);
        txtKeyword = findViewById(R.id.tvKeyword);
        imgRelated = findViewById(R.id.imgRelated);

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    btnClear.setVisibility(View.VISIBLE);
                    btnContainer.setVisibility(View.GONE);
                    translatedContainer.setVisibility(View.VISIBLE);
                } else {
                    btnClear.setVisibility(View.GONE);
                    btnContainer.setVisibility(View.VISIBLE);
                    translatedContainer.setVisibility(View.GONE);
                    imgCardview.setVisibility(View.GONE);
                }
            }

            private Timer timer = new Timer();
            private final long DELAY = 500; // milliseconds

            @Override
            public void afterTextChanged(Editable s) {
                final String curText = s.toString();

                final Handler handler = new Handler();

                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        TranslateOptions options = TranslateOptions.newBuilder()
                                                .setApiKey(API_KEY)
                                                .build();
                                        final Translate translate = options.getService();
                                        final Translation translation =
                                                translate.translate(curText,
                                                        TranslateOption.sourceLanguage("en"),
                                                        Translate.TranslateOption.targetLanguage("id"));
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                String translatedTxt = translation.getTranslatedText();
                                                txtTranslated.setText(translatedTxt);
                                            }
                                        });
                                        return null;
                                    }
                                }.execute();
                                api.getKeyword(curText).enqueue(new Callback<JsonArray>() {
                                    @Override
                                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                                        imgCardview.setVisibility(View.VISIBLE);
                                        txtKeyword.setText(response.body().get(0).getAsString());
                                    }

                                    @Override
                                    public void onFailure(Call<JsonArray> call, Throwable t) {
                                        Log.v("get keyword", "error: " + t.getMessage());
                                    }
                                });
                            }
                        },
                        DELAY
                );


            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == btnClear) {
            etInput.getText().clear();

        }
    }
}
