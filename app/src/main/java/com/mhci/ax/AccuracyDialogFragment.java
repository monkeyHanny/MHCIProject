package com.mhci.ax;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by monkeyhanny on 11/3/2018.
 */

public class AccuracyDialogFragment extends DialogFragment {
    private EditText etInput;
    private Button btnOcr, btnMic;
    private TextView btnUpdate, btnCancel;

    static AccuracyDialogFragment newInstance() {
        return new AccuracyDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_accuracy, container, false);
        etInput = v.findViewById(R.id.etImproved);
        btnOcr = v.findViewById(R.id.btnAccuracyOcr);
        btnMic = v.findViewById(R.id.btnAccuracySpeech);
        btnUpdate = v.findViewById(R.id.tvUpdate);
        btnCancel = v.findViewById(R.id.tvCancel);
        return v;

    }
}
