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

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mhci.ax.services.Utils.apiCortical;
import static com.mhci.ax.services.Utils.apiSearch;

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
    private static String cxId = "013798557058526750109:dg9gmocpn9c";


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

                                apiCortical.getKeyword(curText).enqueue(new Callback<JsonArray>() {
                                    @Override
                                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                                        if (response.body().size() > 0) {
                                            if (imgCardview.getVisibility() == View.GONE) {
                                                imgCardview.setVisibility(View.VISIBLE);
                                            }
                                            String keyword = response.body().get(0).getAsString();
                                            Log.v("extract keyword", "keyword: " + response.body());

                                            txtKeyword.setText(keyword);
                                            apiSearch.getImgUrl(API_KEY, cxId, keyword).enqueue(new Callback<JsonObject>() {
                                                @Override
                                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                                    String link = response.body().get("items").getAsJsonArray().get(0).getAsJsonObject().get("link").getAsString();
                                                    Picasso.get().load(link).into(imgRelated);
                                                }

                                                @Override
                                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                                    Log.v("image url", "error: " + t.getMessage());
                                                }
                                            });

                                        } else {
                                            imgCardview.setVisibility(View.GONE);
                                        }

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
