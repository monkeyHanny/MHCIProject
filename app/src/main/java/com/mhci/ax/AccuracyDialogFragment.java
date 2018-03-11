package com.mhci.ax;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.mhci.ax.db.ViewModel;
import com.mhci.ax.db.entity.Phrase;
import com.mhci.ax.services.PackageManagerUtils;
import com.mhci.ax.services.PermissionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.mhci.ax.TranslateActivity.ANDROID_CERT_HEADER;
import static com.mhci.ax.TranslateActivity.ANDROID_PACKAGE_HEADER;
import static com.mhci.ax.TranslateActivity.CAMERA_IMAGE_REQUEST;
import static com.mhci.ax.TranslateActivity.CAMERA_PERMISSIONS_REQUEST;
import static com.mhci.ax.TranslateActivity.FILE_NAME;
import static com.mhci.ax.TranslateActivity.REQ_CODE_SPEECH;
import static com.mhci.ax.TranslateActivity.scaleBitmapDown;
import static com.mhci.ax.services.Utils.API_KEY;
import static com.mhci.ax.services.Utils.getOriginal;
import static com.mhci.ax.services.Utils.getTarget;

/**
 * Created by monkeyhanny on 11/3/2018.
 */

public class AccuracyDialogFragment extends DialogFragment implements View.OnClickListener {
    private EditText etInput;
    private Button btnOcr, btnMic;
    private TextView btnUpdate, btnCancel, txtHeading;
    private String targetLanguage, originalLanguage;
    public static String KEY_ORIGINAL = "original";
    private String txtOriginal;
    private ViewModel mViewModel;

    private OnAccuracyUpdatedListener mAccuracyUpdatedListener = null;


    static AccuracyDialogFragment newInstance(String original) {
        AccuracyDialogFragment f = new AccuracyDialogFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString(KEY_ORIGINAL, original);
        f.setArguments(args);
        return f;
    }


    public interface OnAccuracyUpdatedListener {

        public void onAccuracyUpdated(String updatedTxt);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog);
        targetLanguage = getTarget(getActivity());
        originalLanguage = getOriginal(getActivity());
    }

    public void setOnAccuracyUpdatedListener(OnAccuracyUpdatedListener listener) {
        mAccuracyUpdatedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_accuracy, container, false);
        etInput = v.findViewById(R.id.etImproved);
        btnOcr = v.findViewById(R.id.btnAccuracyOcr);
        btnOcr.setOnClickListener(this);
        btnMic = v.findViewById(R.id.btnAccuracySpeech);
        btnMic.setOnClickListener(this);
        btnUpdate = v.findViewById(R.id.tvUpdate);
        btnUpdate.setOnClickListener(this);
        btnCancel = v.findViewById(R.id.tvCancel);
        btnCancel.setOnClickListener(this);
        txtHeading = v.findViewById(R.id.tvHeadingAccuracy);
        txtOriginal = getArguments().getString(KEY_ORIGINAL);
        return v;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String heading = txtHeading.getText().toString();

        final Handler handler = new Handler();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                TranslateOptions options = TranslateOptions.newBuilder()
                        .setApiKey(API_KEY)
                        .build();
                final Translate translate = options.getService();
                final Translation translation =
                        translate.translate(heading,
                                Translate.TranslateOption.sourceLanguage(originalLanguage),
                                Translate.TranslateOption.targetLanguage(targetLanguage));
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String translatedTxt = translation.getTranslatedText();
                        txtHeading.setText(translatedTxt + "(" + heading + ")");
                    }
                });
                return null;
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {
        if (v == btnUpdate) {
            String updatedText = etInput.getText().toString();
            mViewModel = ViewModelProviders.of(getActivity()).get(ViewModel.class);

            mViewModel.insert(new Phrase(txtOriginal, updatedText, targetLanguage));

            if (null != mAccuracyUpdatedListener) {
                mAccuracyUpdatedListener.onAccuracyUpdated(updatedText);
            }
            this.dismiss();

        } else if (v == btnCancel) {
            this.dismiss();
        } else if (v == btnMic) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, targetLanguage);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    "Hi, Speak something...");
            try {
                startActivityForResult(intent, REQ_CODE_SPEECH);
            } catch (ActivityNotFoundException a) {

            }
        } else if (v == btnOcr) {
            startCamera();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    Uri photoUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", getCameraFile());
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
                                MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri),
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

                                    String packageName = getActivity().getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = PackageManagerUtils.getSignature(getActivity().getPackageManager(), packageName);

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

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "";

        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        if (labels != null) {

            message = labels.get(0).getDescription().trim();

        }

        return message;
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                getActivity(),
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }
}
