package com.example.admin.expenses.fragments;

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

public class AddParticipantDialogFragment extends DialogFragment {

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final ExpensesDatabase db = ExpensesDatabase.getInstance(getActivity().getApplicationContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View myFragmentView = inflater.inflate(R.layout.dialog_add_participant, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(myFragmentView);

        builder.setPositiveButton(R.string.confirm_adding_window, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                EditText participantNameView = myFragmentView.findViewById(R.id.participant_name);
                String participantName = participantNameView.getText().toString();

                if (!participantName.isEmpty()) {
                    long windowId = getArguments().getLong("window_id");
                    String windowParticipants = getArguments().getString("window_participants");
                    String fullParticipantsString = "";

                    if (windowParticipants.isEmpty()) {
                        fullParticipantsString = participantName;
                    } else {
                        fullParticipantsString = windowParticipants + "," + participantName;
                    }

                    db.beginTransaction();
                    try {
                        db.window().updateParticipantsById(fullParticipantsString, windowId);
                        db.setTransactionSuccessful();
                    } catch (Exception e) {
                        Log.d("DATABASE", "Exception was thrown while inserting new window:\n" + e.getMessage());
                    } finally {
                        db.endTransaction();
                        AddParticipantDialogFragment.this.dismiss();
                        Intent intent = getActivity().getIntent();
                        getActivity().finish();
                        startActivity(intent);
                    }
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
}
