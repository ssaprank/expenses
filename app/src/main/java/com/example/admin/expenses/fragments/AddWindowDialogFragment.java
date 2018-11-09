package com.example.admin.expenses.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.admin.expenses.R;
import com.example.admin.expenses.data.ExpensesDatabase;
import com.example.admin.expenses.data.Window;

public class AddWindowDialogFragment extends AddingDialogFragment {

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activity = getActivity();

        if (activity == null) {
            logErrorAndQuit("No activity found.");
        }

        db = ExpensesDatabase.getInstance(activity.getApplicationContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_add_window, null);
        builder.setView(dialogView);

        builder.setPositiveButton(R.string.confirm_adding_window, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            Window window = getWindowObjectFromViews();
            insertEntity(window);
            closeDialogAndRestartActivity();
            }
        });

        builder.setNegativeButton(R.string.cancel_adding_window, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AddWindowDialogFragment.this.getDialog().cancel();
                }
        });

        return builder.create();
    }

    private Window getWindowObjectFromViews()
    {
        Window window = new Window();
        EditText nameView = dialogView.findViewById(R.id.windowName);
        EditText plannedSumView = dialogView.findViewById(R.id.plannedSum);
        window.name = nameView.getText().toString();

        try {
            window.planned = Double.parseDouble(plannedSumView.getText().toString());
        } catch (Exception e) {
            window.planned = 0;
        }

        return window;
    }
}
