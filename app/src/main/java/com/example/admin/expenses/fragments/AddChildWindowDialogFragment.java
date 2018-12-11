package com.example.admin.expenses.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.admin.expenses.R;
import com.example.admin.expenses.data.ExpensesDatabase;
import com.example.admin.expenses.data.Item;
import com.example.admin.expenses.data.Window;

import java.util.ArrayList;

public class AddChildWindowDialogFragment extends AddingDialogFragment {
    long windowID;
    Spinner childWindowSpinner;

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
        dialogView = inflater.inflate(R.layout.dialog_add_child_window, null);
        builder.setView(dialogView);

        childWindowSpinner = dialogView.findViewById(R.id.child_window_spinner);

        Bundle arguments = getArguments();

        if (arguments == null) {
            logErrorAndQuit("No arguments from activity");
        }

        windowID = arguments.getLong("window_id");
        Log.d("DEBUG", "before filling");
        fillSpinnerValues();
        Log.d("DEBUG", "after filling");

        builder.setPositiveButton(R.string.confirm_adding_window, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                long childWindowID = getIdFromSpinner();
                Window.ChildParent windowRelation = new Window.ChildParent(childWindowID, windowID);

                try {
                    db.beginTransaction();
                    db.windowChildParent().insert(windowRelation);
                    db.setTransactionSuccessful();
                } catch (Exception e) {} finally {
                    db.endTransaction();
                }

                closeDialogAndRestartActivity();
            }
        });
        Log.d("DEBUG", "after setting button");

        builder.setNegativeButton(R.string.cancel_adding_window, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AddChildWindowDialogFragment.this.getDialog().cancel();
            }
        });
        Log.d("DEBUG", "before create");

        return builder.create();
    }

    private void fillSpinnerValues() {
        Cursor windowCursor = db.window().selectAll();
        ArrayList<String> possibleChildren = new ArrayList<String>();

        while (windowCursor.moveToNext()) {
            long id = windowCursor.getLong(windowCursor.getColumnIndex("id"));

            if (id ==  windowID) {
                continue;
            }

            if (db.windowChildParent().isParentWindow(id)) {
                continue;
            }

            if (db.windowChildParent().isChildWindow(id, windowID)) {
                continue;
            }

            String name = windowCursor.getString(windowCursor.getColumnIndex("name"));

            possibleChildren.add(name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, possibleChildren);
        adapter.setDropDownViewResource(R.layout.additem_dialog_spinner);

        childWindowSpinner.setAdapter(adapter);
    }

    private long getIdFromSpinner() {
        String selectedName = childWindowSpinner.getSelectedItem().toString();
        return db.window().selectIdByName(selectedName);
    }

}
