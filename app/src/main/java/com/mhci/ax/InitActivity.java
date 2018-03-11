package com.mhci.ax;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import static com.mhci.ax.services.Utils.ifInit;
import static com.mhci.ax.services.Utils.setInit;

/**
 * Created by monkeyhanny on 22/2/2018.
 */

public class InitActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        if (ifInit(this)) {
            setInit(this);
            showDialog();
        } else {
            Intent i = new Intent(this, TranslateActivity.class);
            startActivity(i);
            finish();
        }

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
        DialogFragment newFragment = SettingDialogFragment.newInstance();
        newFragment.show(ft, "dialog");
    }
}
