package me.myshows.android.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import me.myshows.android.R;

/**
 * Created by Whiplash on 06.09.2015.
 */
public class ClearCacheDialog extends DialogFragment {

    public static final int REQUEST_CODE = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.clear_cache_dialog_title)
                .setMessage(R.string.clear_cache_dialog_content)
                .setNegativeButton(R.string.clear_cache_dialog_reject, (dialogInterface, i) ->
                        getTargetFragment().onActivityResult(REQUEST_CODE, Activity.RESULT_CANCELED, null))
                .setPositiveButton(R.string.clear_cache_dialog_accept, (dialogInterface1, i) ->
                        getTargetFragment().onActivityResult(REQUEST_CODE, Activity.RESULT_OK, null))
                .create();
    }
}
