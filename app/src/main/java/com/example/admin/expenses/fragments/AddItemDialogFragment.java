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
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.admin.expenses.R;
import com.example.admin.expenses.data.ExpensesDatabase;
import com.example.admin.expenses.data.Item;

public class AddItemDialogFragment extends DialogFragment {

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ExpensesDatabase db = ExpensesDatabase.getInstance(getActivity().getApplicationContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View myFragmentView = inflater.inflate(R.layout.dialog_add_item, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(myFragmentView);

        builder.setPositiveButton(R.string.confirm_adding_window, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Item item = null;
                try {
                    item = new Item();
                } catch (ClassNotFoundException e) {
                    Log.d("DEBUG", "Item class was not found");
                    AddItemDialogFragment.this.dismiss();
                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    startActivity(intent);
                }

                EditText descriptionView = myFragmentView.findViewById(R.id.item_description);
                EditText sumView = myFragmentView.findViewById(R.id.item_sum);
                item.windowID = getArguments().getLong("window_id");
                item.description = descriptionView.getText().toString();
                item.sum = Double.parseDouble(sumView.getText().toString());
                item.created_timestamp = System.currentTimeMillis() / 1000;

                db.beginTransaction();
                try {
                    long newid = db.item().insert(item);
                    db.setTransactionSuccessful();
                    Log.d("DATABASE", String.format("ID %d was inserted", newid));
                } catch (Exception e) {
                    Log.d("DATABASE", "Exception was thrown while inserting new window:\n" + e.getMessage());
                } finally {
                    db.endTransaction();
                    AddItemDialogFragment.this.dismiss();
                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    startActivity(intent);
                }
            }
        });

        builder.setNegativeButton(R.string.cancel_adding_window, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AddItemDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }
}
