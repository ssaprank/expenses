package com.example.admin.expenses.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Entity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.admin.expenses.R;
import com.example.admin.expenses.data.Debt;
import com.example.admin.expenses.data.ExpensesDatabase;
import com.example.admin.expenses.data.Item;

import java.util.ArrayList;
import java.util.Arrays;

public class AddItemDialogFragment extends DialogFragment {

    Activity activity;
    View dialogView;
    Spinner itemOwnerSpinner;
    String[] participants;
    ExpensesDatabase db;
    long windowID;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activity = getActivity();

        if (activity == null) {
            logErrorAndQuit("No activity found.");
        }

        Context activityContext = activity.getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        db = ExpensesDatabase.getInstance(activityContext);
        dialogView = inflater.inflate(R.layout.dialog_add_item, null);
        itemOwnerSpinner = dialogView.findViewById(R.id.item_owner);
        builder.setView(dialogView);

        Bundle arguments = getArguments();

        if (arguments == null) {
            logErrorAndQuit("No arguments from activity");
        }

        String participantsArg = arguments.getString("window_participants");
        windowID = arguments.getLong("window_id");

        if (participantsArg == null || windowID == 0) {
            logErrorAndQuit("Activity arguments are incomplete or missing.");
        } else {
            createViewsForParticipants(participantsArg);
        }

        builder.setPositiveButton(R.string.confirm_adding_window, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Item item = getItemFromDialogForm();

                if (item.sum > 0 && participants.length > 0) {
                    calculateAndInsertEqualShare(item);
                }

                insertItem(item);
            }
        });

        builder.setNegativeButton(R.string.cancel_adding_window, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AddItemDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }

    private void createViewsForParticipants(String participantsArg) {
        participants = participantsArg.split(",");
        fillSpinnerWithParticipants();
        createCheckboxesForParticipants();
    }

    private void fillSpinnerWithParticipants() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, participants);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemOwnerSpinner.setAdapter(adapter);
    }

    private void createCheckboxesForParticipants() {
        LinearLayout checkboxContainerLayout = dialogView.findViewById(R.id.participant_checkbox_layout);

        for (int i = 0; i < participants.length; i++) {
            CheckBox checkbox = getParticipantCheckbox(participants[i]);
            checkboxContainerLayout.addView(checkbox);
        }
    }

    private CheckBox getParticipantCheckbox(String participant) {
        CheckBox participantCheckbox = new CheckBox(getActivity());
        participantCheckbox.setChecked(true);
        participantCheckbox.setText(participant);
        participantCheckbox.setId(View.generateViewId());
        participantCheckbox.setTag("participant_checkbox_" + participant);
        return participantCheckbox;
    }

    private Item getItemFromDialogForm() {
        Item item = new Item();

        EditText descriptionView = dialogView.findViewById(R.id.item_description);
        EditText sumView = dialogView.findViewById(R.id.item_sum);
        item.windowID = getArguments().getLong("window_id");
        item.description = descriptionView.getText().toString();
        item.sum = Double.parseDouble(sumView.getText().toString());
        item.created_timestamp = System.currentTimeMillis() / 1000;

        return item;
    }

    private void calculateAndInsertEqualShare(Item item) {
        ArrayList<String> sharingParticipants = getSharingParticipants();

        if (sharingParticipants.size() > 0) {
            double equalShare = item.sum / sharingParticipants.size();

            insertDebtsForSharingParticipants(sharingParticipants, equalShare);
        }
    }

    private ArrayList<String> getSharingParticipants() {
        ArrayList<String> sharingParticipants = new ArrayList<>();

        for (int i = 0; i < participants.length; i++) {
            if (isParticipantCheckboxChecked(participants[i])) {
                sharingParticipants.add(participants[i]);
            }
        }

        return sharingParticipants;
    }

    private boolean isParticipantCheckboxChecked(String participant) {
        CheckBox participantCheckbox = dialogView.findViewWithTag("participant_checkbox_" + participant);

        return participantCheckbox.isChecked();
    }

    private String getItemOwner() {
        return itemOwnerSpinner.getSelectedItem().toString();
    }

    private void insertDebtsForSharingParticipants(ArrayList<String> sharingParticipants, double equalShare) {
        String debtOwner = getItemOwner();

        for (int i = 0; i < sharingParticipants.size(); i++) {
            Debt debt = createDebt(equalShare, debtOwner, sharingParticipants.get(i));
            insertDebt(debt);
        }
    }

    private Debt createDebt(double sum, String owner, String debtor) {
        Debt debt = new Debt();
        debt.amount = sum;
        debt.owner = owner;
        debt.debtor = debtor;
        debt.windowID = windowID;
        return debt;
    }

    private void insertDebt(Debt debt) {
        try {
            db.beginTransaction();
            db.debt().insert(debt);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("DATABASE", "Exception was thrown while inserting new debt:\n" + e.getMessage());
        } finally {
            db.endTransaction();
            closeDialogAndRestartActivity();
        }
    }

    private void insertItem(Item item) {
        try {
            db.beginTransaction();
            db.item().insert(item);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("DATABASE", "Exception was thrown while inserting new item:\n" + e.getMessage());
        } finally {
            db.endTransaction();
            closeDialogAndRestartActivity();
        }
    }

    private void logErrorAndQuit(String errorMessage) {
        Log.d("RUNTIME",getClass() + ": " + errorMessage);
        closeDialogAndRestartActivity();
    }

    private void closeDialogAndRestartActivity() {
        AddItemDialogFragment.this.dismiss();
        restartActivity();
    }

    private void restartActivity() {
        Intent intent = activity.getIntent();
        activity.finish();
        startActivity(intent);
    }
}
