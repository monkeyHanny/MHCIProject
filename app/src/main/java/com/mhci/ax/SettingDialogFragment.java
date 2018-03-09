package com.mhci.ax;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by monkeyhanny on 9/3/2018.
 */

public class SettingDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Spinner firstSpinner, secondSpinner;
    private Button btnSwap;
    private TextView tvDone;

    static SettingDialogFragment newInstance() {
        return new SettingDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog);
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
        secondSpinner.setSelection(1);

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
        if (view == firstSpinner) {

        } else if (view == secondSpinner) {

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if (v == btnSwap) {
            int curFirst = firstSpinner.getSelectedItemPosition();
            int curSecond = secondSpinner.getSelectedItemPosition();

            firstSpinner.setSelection(curSecond, true);
            secondSpinner.setSelection(curFirst, true);
        } else if (v == tvDone) {
            Intent i = new Intent(getActivity(), TranslateActivity.class);
            startActivity(i);
        }
    }
}
