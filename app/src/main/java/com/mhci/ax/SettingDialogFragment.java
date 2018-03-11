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

import com.mhci.ax.db.ViewModel;

import static com.mhci.ax.services.Utils.ifInit;
import static com.mhci.ax.services.Utils.setPreferences;

/**
 * Created by monkeyhanny on 9/3/2018.
 */

public class SettingDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Spinner firstSpinner, secondSpinner;
    private Button btnSwap;
    private TextView tvDone;
    private boolean isInit = true;
    private ViewModel mViewModel;
    private int firstIndex, secondIndex;


    static SettingDialogFragment newInstance() {
        return new SettingDialogFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog);
        isInit = ifInit(getActivity());
        /*if (isInit) {
            mViewModel = ViewModelProviders.of(getActivity()).get(ViewModel.class);
            mViewModel.getAllPhrases().observe(getActivity(), phrases -> {
                Log.v("get all phrases", "phrase count: " + phrases.size());
            });
        }*/
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

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        firstIndex = firstSpinner.getSelectedItemPosition();
        secondIndex = secondSpinner.getSelectedItemPosition();
        if (v == btnSwap) {

            firstSpinner.setSelection(secondIndex, true);
            secondSpinner.setSelection(firstIndex, true);
        } else if (v == tvDone) {
            String codes[] = getActivity().getResources().getStringArray(R.array.language_codes);
            String originalCode = codes[firstIndex];
            String targetCode = codes[secondIndex];
            Log.v("setting: ", "origianl: " + originalCode);
            Log.v("setting", "target: " + targetCode);
            setPreferences(getActivity(), originalCode, targetCode);
            Intent i = new Intent(getActivity(), TranslateActivity.class);
            startActivity(i);
        }
    }
}
