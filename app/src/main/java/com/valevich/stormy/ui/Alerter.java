package com.valevich.stormy.ui;

import android.app.Activity;
import android.content.Context;

/**
 * Created by NotePad.by on 14.11.2015.
 */
public class Alerter {
    public void alertUserAboutError(Context context) {
        Activity myActivity = (Activity) context;
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(myActivity.getFragmentManager(),"error_dialog");
    };
}
