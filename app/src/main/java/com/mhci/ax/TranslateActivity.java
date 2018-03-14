package com.mhci.ax;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mapzen.speakerbox.Speakerbox;
import com.mhci.ax.db.ViewModel;
import com.mhci.ax.db.entity.Favourite;
import com.mhci.ax.services.PackageManagerUtils;
import com.mhci.ax.services.PermissionUtils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mhci.ax.services.Utils.API_KEY;
import static com.mhci.ax.services.Utils.apiCortical;
import static com.mhci.ax.services.Utils.apiSearch;
import static com.mhci.ax.services.Utils.cxId;
import static com.mhci.ax.services.Utils.getOriginal;
import static com.mhci.ax.services.Utils.getTarget;

/**
 * Created by monkeyhanny on 9/3/2018.
 */

public class TranslateActivity extends FragmentActivity implements View.OnClickListener, AccuracyDialogFragment.OnAccuracyUpdatedListener, SettingDialogFragment.onSettingChangedListener {
    private EditText etInput;
    private Button btnOCR, btnSpeech, btnClear, btnFav, btnEdit, btnSpeaker;
    private TextView txtTranslated, txtKeyword;
    private LinearLayout btnContainer, translatedContainer, favContainer;
    private CardView imgCardview, favCardview;
    private ImageView imgRelated;
    private String targetLanguage, originalLanguage;

    private ViewModel mViewModel;
    private HashSet<String> originalList = new HashSet<>();
    private boolean isFav = false;

    public static final int REQ_CODE_SPEECH = 100;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    public static final String FILE_NAME = "temp.jpg";
    public static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    public static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        mViewModel = ViewModelProviders.of(TranslateActivity.this).get(ViewModel.class);

        etInput = findViewById(R.id.etInput);
        btnOCR = findViewById(R.id.btnOcr);
        btnOCR.setOnClickListener(this);
        btnSpeech = findViewById(R.id.btnSpeech);
        btnSpeech.setOnClickListener(this);
        btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);
        btnFav = findViewById(R.id.btnFav);
        btnFav.setOnClickListener(this);
        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(this);
        btnSpeaker = findViewById(R.id.btnSpeaker);
        btnSpeaker.setOnClickListener(this);
        btnContainer = findViewById(R.id.btnContainer);
        translatedContainer = findViewById(R.id.translatedContentContainer);
        favContainer = findViewById(R.id.favContainer);
        txtTranslated = findViewById(R.id.tvTranslated);
        imgCardview = findViewById(R.id.imgCardview);
        favCardview = findViewById(R.id.favCardview);
        txtKeyword = findViewById(R.id.tvKeyword);
        imgRelated = findViewById(R.id.imgRelated);

        originalLanguage = getOriginal(this);
        targetLanguage = getTarget(this);

        Log.v("original: ", "origianl: " + originalLanguage);
        Log.v("target", "target: " + targetLanguage);

        displayFav();

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
                    favCardview.setVisibility(View.GONE);
                } else {
                    btnClear.setVisibility(View.GONE);
                    btnContainer.setVisibility(View.VISIBLE);
                    translatedContainer.setVisibility(View.GONE);
                    imgCardview.setVisibility(View.GONE);
                    displayFav();
                    btnFav.setBackground(getDrawable(R.drawable.ic_favorite_white));
                    isFav = false;
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


                                String translated = mViewModel.getPhrase(curText, targetLanguage);
                                if (!translated.trim().isEmpty()) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            txtTranslated.setText(translated);
                                        }
                                    });
                                } else {
                                    new AsyncTask<Void, Void, Void>() {
                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            TranslateOptions options = TranslateOptions.newBuilder()
                                                    .setApiKey(API_KEY)
                                                    .build();
                                            final Translate translate = options.getService();
                                            final Translation translation =
                                                    translate.translate(curText,
                                                            TranslateOption.sourceLanguage(originalLanguage),
                                                            Translate.TranslateOption.targetLanguage(targetLanguage));
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    String translatedTxt = translation.getTranslatedText();
                                                    txtTranslated.setText(translatedTxt);

                                                    if (originalList.contains(curText)) {
                                                        btnFav.setBackground(getDrawable(R.drawable.ic_favorite_red));
                                                        isFav = true;
                                                    }
                                                }
                                            });
                                            return null;
                                        }
                                    }.execute();

                                }
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
        } else if (v == btnOCR) {
            startCamera();
        } else if (v == btnSpeech) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, originalLanguage);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    "Hi, Speak something...");
            try {
                startActivityForResult(intent, REQ_CODE_SPEECH);
            } catch (ActivityNotFoundException a) {

            }
        } else if (v == btnFav) {
            Favourite cur = new Favourite(originalLanguage,
                    txtTranslated.getText().toString(), targetLanguage, etInput.getText().toString());
            if (!isFav) {
                btnFav.setBackground(getDrawable(R.drawable.ic_favorite_red));
                mViewModel.addFavourite(cur);
                Log.v("btnFav", "add fav");
            } else {
                btnFav.setBackground(getDrawable(R.drawable.ic_favorite_white));
                mViewModel.deleteFav(cur);
                originalList.remove(cur.getOriginalText());
                Log.v("btnFav", "remove fav");
            }

        } else if (v == btnSpeaker) {
            Speakerbox speakerbox = new Speakerbox(TranslateActivity.this.getApplication());
            speakerbox.play(txtTranslated.getText());

        } else if (v == btnEdit) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            // Create and show the dialog.
            AccuracyDialogFragment newFragment = AccuracyDialogFragment.newInstance(etInput.getText().toString());

            newFragment.show(ft, "dialog");
            newFragment.setOnAccuracyUpdatedListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.item_setting:
                showDialog();

                return true;

            default:
                break;
        }

        return false;
    }

    private void showDialog() {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        SettingDialogFragment newFragment = SettingDialogFragment.newInstance();
        newFragment.setOnSettingChangedListener(this);
        newFragment.show(ft, "dialog");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etInput.setText(result.get(0));
                    Log.v("REQ_CODE_SPEECH", "result: " + result.get(0));

                }
                break;
            }
            case CAMERA_IMAGE_REQUEST: {
                if (resultCode == RESULT_OK) {
                    Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
                    uploadImage(photoUri);
                }
                break;

            }
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);

                callCloudVision(bitmap);

            } catch (IOException e) {
                Log.d("OCR", "Image picking failed because " + e.getMessage());

            }
        } else {
            Log.d("OCR", "Image picker gave us a null image.");
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {


        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("DOCUMENT_TEXT_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d("OCR", "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d("OCR", "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d("OCR", "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                etInput.setText(result);
            }
        }.execute();
    }

    public static Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "";

        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        if (labels != null) {

            message = labels.get(0).getDescription().trim();

        }

        return message;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
        }
    }

    @Override
    public void onAccuracyUpdated(String updatedTxt) {
        txtTranslated.setText(updatedTxt);
    }

    private void insertFav(Favourite favourite, LinearLayout parent) {
        TextView original = new TextView(this);
        original.setText(favourite.getOriginalText());

        TextView translated = new TextView(this);
        translated.setText(favourite.getTranslated());

        LinearLayout.LayoutParams paramsO = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsO.setMargins(0, 0, 0, measurePx(8));
        original.setLayoutParams(paramsO);

        parent.addView(original);

        LinearLayout.LayoutParams paramsT = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsT.setMargins(0, 0, 0, measurePx(16));
        translated.setLayoutParams(paramsT);

        parent.addView(translated);
    }

    private int measurePx(int dp) {
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
        return px;
    }

    private void displayFav() {
        if (favContainer.getChildCount() > 1) {
            while (favContainer.getChildCount() > 1) {
                favContainer.removeViewAt(1);
            }
        }
        List<Favourite> tempList = mViewModel.getAllFavourites(originalLanguage, targetLanguage);
        if (tempList.size() > 0) {
            favCardview.setVisibility(View.VISIBLE);
            for (Favourite favourite : tempList) {
                insertFav(favourite, favContainer);
                if (!originalList.contains(favourite.getOriginalText())) {

                    originalList.add(favourite.getOriginalText());
                }

            }
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    public void onSettingChanged(String firstLanguage, String secondLanguage) {
        originalLanguage = firstLanguage;
        targetLanguage = secondLanguage;
        etInput.getText().clear();
    }
}
