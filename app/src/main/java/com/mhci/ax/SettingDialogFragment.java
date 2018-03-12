package com.mhci.ax;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import static com.mhci.ax.services.Utils.getOriginal;
import static com.mhci.ax.services.Utils.getTarget;
import static com.mhci.ax.services.Utils.setPreferences;

/**
 * Created by monkeyhanny on 9/3/2018.
 */

public class SettingDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Spinner firstSpinner, secondSpinner;
    private Button btnSwap;
    private TextView tvDone;

    private onSettingChangedListener mSettingChangedListener = null;
    private String firstCode, secondCode;
    private String codes[];

    static SettingDialogFragment newInstance() {
        return new SettingDialogFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog);
        firstCode = getOriginal(getActivity());
        secondCode = getTarget(getActivity());
        codes = getActivity().getResources().getStringArray(R.array.language_codes);

    }

    public interface onSettingChangedListener {

        public void onSettingChanged(String firstLanguage, String secondLanguage);
    }

    public void setOnSettingChangedListener(onSettingChangedListener listener) {
        mSettingChangedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_setting, container, false);
        firstSpinner = v.findViewById(R.id.first_spinner);
        secondSpinner = v.findViewById(R.id.second_spinner);
        btnSwap = v.findViewById(R.id.btnSwap);
        btnSwap.setOnClickListener(this);

        ArrayAdapter<CharSequence> firstAdapter = setupAdapter();
        ArrayAdapter<CharSequence> secondAdapter = setupAdapter();
        // Apply the adapter to the spinner
        firstSpinner.setAdapter(firstAdapter);
        firstSpinner.setOnItemSelectedListener(this);


        secondSpinner.setAdapter(secondAdapter);
        secondSpinner.setOnItemSelectedListener(this);


        int firstIndex = 0, secondIndex = 1;
        if (!firstCode.isEmpty() && !secondCode.isEmpty()) {
            for (int i = 0; i < codes.length; i++) {
                if (codes[i].equals(firstCode))
                    firstIndex = i;
                else if (codes[i].equals(secondCode))
                    secondIndex = i;
            }
            firstSpinner.setSelection(firstIndex, true);
            secondSpinner.setSelection(secondIndex, true);
        } else {
            firstSpinner.setSelection(firstIndex, true);
            secondSpinner.setSelection(secondIndex, true);
        }


        tvDone = v.findViewById(R.id.tvDone);
        tvDone.setOnClickListener(this);

        return v;

    }

    private ArrayAdapter<CharSequence> setupAdapter() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.languages_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        int firstIndex = firstSpinner.getSelectedItemPosition();
        int secondIndex = secondSpinner.getSelectedItemPosition();
        if (v == btnSwap) {

            firstSpinner.setSelection(secondIndex, true);
            secondSpinner.setSelection(firstIndex, true);
        } else if (v == tvDone) {

            String originalCode = codes[firstIndex];
            String targetCode = codes[secondIndex];
            Log.v("setting: ", "origianl: " + originalCode);
            Log.v("setting", "target: " + targetCode);
            setPreferences(getActivity(), originalCode, targetCode);

            if (null != mSettingChangedListener) {
                mSettingChangedListener.onSettingChanged(originalCode, targetCode);
            } else {
                Intent i = new Intent(getActivity(), TranslateActivity.class);
                startActivity(i);
            }


        }
    }
}
