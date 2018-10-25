package com.example.admin.expenses;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.database.Cursor;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.admin.expenses.data.*;
import com.example.admin.expenses.fragments.AddWindowDialogFragment;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ExpensesDatabase db;
    ConstraintLayout layoutMain;
    long currentWindowId;
    ConstraintSet set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = ExpensesDatabase.getInstance(this);
        layoutMain = findViewById(R.id.layout_window_list);
        set = new ConstraintSet();

        setActionBar("Manage your expenses");

        Cursor windowsCursor = db.window().selectAll();
        int previousWindowViewId = 0;

        while (windowsCursor.moveToNext()) {
            TextView windowView = addWindowView(windowsCursor);
            ImageButton deleteWindowButton = addDeletionButtonForWindow();

            set.clone(layoutMain);

            placeViewOnLayout(windowView, previousWindowViewId, ConstraintSet.LEFT);
            placeViewOnLayout(deleteWindowButton, previousWindowViewId, ConstraintSet.RIGHT);

            previousWindowViewId = windowView.getId();
        }

        windowsCursor.close();

        ImageButton addWindowButton = findViewById(R.id.addWindowButton);

        addWindowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openAddWindowDialog();
            }
        });
    }

    private void placeViewOnLayout(View view, int previousElementId, int horizontalAlign) {
        if (previousElementId == 0) {
            set.connect(view.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 40);
        } else {
            set.connect(view.getId(), ConstraintSet.TOP, previousElementId, ConstraintSet.BOTTOM, 20);
        }
        set.connect(view.getId(), horizontalAlign, ConstraintSet.PARENT_ID, horizontalAlign, 80);

        set.applyTo(layoutMain);
    }

    private TextView addWindowView(Cursor cursor) {
        currentWindowId = cursor.getLong(cursor.getColumnIndex("id"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        double plannedSum = 0;

        try{
            plannedSum = cursor.getDouble(cursor.getColumnIndex("planned_sum"));
        } catch (Exception e) {
            plannedSum = 0;
        }

        final TextView windowTextView = new TextView(this);

        String windowText = name;

        if (plannedSum > 0) {
            windowText  += String.format(Locale.getDefault(), "\nPlanned sum: %.2f", plannedSum);
        }

        windowTextView.setText(windowText);

        windowTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openItemsActivity(currentWindowId);
            }
        });

        addViewToLayout(windowTextView);

        return windowTextView;
    }

    private ImageButton addDeletionButtonForWindow() {
        ImageButton button = new ImageButton(this);
        button.setBackground(this.getResources().getDrawable(R.drawable.delete_button));
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                try {
                    db.beginTransaction();
                    db.window().deleteById(currentWindowId);
                    db.setTransactionSuccessful();
                } catch (Exception e) {} finally {
                    db.endTransaction();
                    finish();
                    startActivity(getIntent());
                }
            }
        });

        addViewToLayout(button);

        return button;
    }

    private void addViewToLayout(View view) {
        layoutMain.addView(view);
        view.setId(View.generateViewId());
    }

    private void openAddWindowDialog() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        dismissDialogIfOpen(transaction);
        AddWindowDialogFragment addWindowDialog = new AddWindowDialogFragment();
        addWindowDialog.show(transaction, "dialog");
    }

    private void dismissDialogIfOpen(FragmentTransaction transaction) {
        Fragment dialog = getSupportFragmentManager().findFragmentByTag("dialog");

        if (dialog != null) {
            transaction.remove(dialog);
        }

        transaction.addToBackStack(null);
    }

    public void openItemsActivity(long windowId) {
        Intent intent = new Intent(this, ItemsActivity.class);
        intent.putExtra("window_id", windowId);
        startActivity(intent);
    }

    public void setActionBar(String heading) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(heading);
        actionBar.show();
    }

}
