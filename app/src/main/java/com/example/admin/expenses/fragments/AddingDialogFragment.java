package com.example.admin.expenses.fragments;

import android.app.Activity;
import android.content.Entity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;

import com.example.admin.expenses.data.BaseEntity;
import com.example.admin.expenses.data.ExpensesDatabase;

public class AddingDialogFragment extends DialogFragment {
    protected Activity activity;
    protected View dialogView;
    protected ExpensesDatabase db;

    protected void insertEntity(BaseEntity entity) {
        try {
            db.beginTransaction();
            db.getDaoObjectForEntity(entity).insert(entity);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("DATABASE", "Exception was thrown while inserting new " + entity.getClass());
            Log.d("DATABASE", e.getMessage());
        } finally {
            db.endTransaction();
            closeDialogAndRestartActivity();
        }
    }

    protected void logErrorAndQuit(String errorMessage) {
        Log.d("RUNTIME",getClass() + ": " + errorMessage);
        closeDialogAndRestartActivity();
    }

    protected void closeDialogAndRestartActivity() {
        this.dismiss();
        restartActivity();
    }

    protected void restartActivity() {
        Intent intent = activity.getIntent();
        activity.finish();
        startActivity(intent);
    }
}
