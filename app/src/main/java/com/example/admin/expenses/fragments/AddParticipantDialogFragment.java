package com.example.admin.expenses.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

public class AddParticipantDialogFragment extends AddingDialogFragment {

    long windowID;
    String windowParticipants;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        activity = getActivity();

        if (activity == null) {
            logErrorAndQuit("No activity found.");
        }

        Context activityContext = activity.getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        db = ExpensesDatabase.getInstance(activityContext);
        dialogView = inflater.inflate(R.layout.dialog_add_participant, null);

        builder.setView(dialogView);
        builder.setPositiveButton(R.string.confirm_adding_window, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String newParticipantName = getParticipantNameFromTextView();

                if (!newParticipantName.isEmpty()) {
                    parseWindowArguments();
                    appendToParticipants(newParticipantName);
                    updateWindowParticipants();
                }
            }
        });

        builder.setNegativeButton(R.string.cancel_adding_window, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AddParticipantDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }

    private String getParticipantNameFromTextView() {
        EditText participantNameView = dialogView.findViewById(R.id.participant_name);
        return participantNameView.getText().toString();
    }

    private void parseWindowArguments() {
        Bundle arguments = getArguments();

        if (arguments == null) {
            logErrorAndQuit("No window arguments provided");
        }

        try {
            windowID = arguments.getLong("window_id");
            windowParticipants = arguments.getString("window_participants");
        } catch (NullPointerException ex) {
            logErrorAndQuit("Window arguments are incomplete");
        }
    }

    private void appendToParticipants(String newName) {
        if (windowParticipants == null || windowParticipants.isEmpty()) {
            windowParticipants = newName;
        } else {
            windowParticipants = windowParticipants + "," + newName;
        }
    }

    private void updateWindowParticipants() {
        try {
            db.beginTransaction();
            db.window().updateParticipantsByWindowId(windowParticipants, windowID);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("DATABASE", "Exception was thrown while inserting new participant:\n" + e.getMessage());
        } finally {
            db.endTransaction();
            closeDialogAndRestartActivity();
        }
    }
}
