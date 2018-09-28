package com.example.admin.expenses;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentTransaction;
import android.database.Cursor;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.admin.expenses.data.*;
import com.example.admin.expenses.fragments.AddWindowDialogFragment;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ExpensesDatabase db = ExpensesDatabase.getInstance(this);
        setActionBar("Manage your expenses");
        Cursor windowsCursor = db.window().selectAll();

        boolean firstElement = true;
        int previousElementId = 0;

        //int windowsCount = db.window().selectAll().getCount();
        ConstraintSet set = new ConstraintSet();
        ConstraintLayout layoutMain = findViewById(R.id.layout_window_list);

        // Iterate over the list of windows
        while (windowsCursor.moveToNext()) {
            String name = windowsCursor.getString(windowsCursor.getColumnIndex("name"));
            double plannedSum = 0;
            final long windowId = windowsCursor.getLong(windowsCursor.getColumnIndex("id"));

            try{
                plannedSum = windowsCursor.getDouble(windowsCursor.getColumnIndex("planned_sum"));
            } catch (Exception e) {
                plannedSum = 0;
            }

            // text view containing info about the window
            final TextView windowTextView = new TextView(this);

            String plannedSumText = name;
            int currentSpent = 0;
            ProgressBar progressBar = null;

            // set planned sum and current spent progress bar
            if (plannedSum > 0) {
                plannedSumText += String.format(Locale.getDefault(), "\nPlanned sum: %.2f", plannedSum);
                Cursor itemsCursor = db.item().selectByWindowId(windowId);

                while (itemsCursor.moveToNext()) {
                    try{
                        currentSpent += (int)itemsCursor.getDouble(itemsCursor.getColumnIndex("sum"));
                    } catch (RuntimeException re) {
                        continue;
                    }
                }

                itemsCursor.close();

                progressBar = new ProgressBar(this, null, R.style.Widget_AppCompat_ProgressBar_Horizontal);
                progressBar.setMax((int)plannedSum);
                progressBar.setProgress(currentSpent);
                progressBar.setVisibility(View.VISIBLE);
                layoutMain.addView(progressBar);
                progressBar.setId(View.generateViewId());
                progressBar.setIndeterminate(false);

                Log.d("DEBUG", String.format(Locale.getDefault(), "%d currently spent", currentSpent));
            }

            windowTextView.setText(plannedSumText);

            windowTextView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    openItemsActivity(windowId);
                }
            });

            layoutMain.addView(windowTextView);
            windowTextView.setId(View.generateViewId());

            // button view for item deletion
            ImageButton deleteWindowButton = new ImageButton(this);
            deleteWindowButton.setBackground(this.getResources().getDrawable(R.drawable.delete_button));
            deleteWindowButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    try {
                        db.beginTransaction();
                        db.window().deleteById(windowId);
                        db.setTransactionSuccessful();
                    } catch (Exception e) {} finally {
                        db.endTransaction();
                        finish();
                        startActivity(getIntent());
                    }
                }
            });

            layoutMain.addView(deleteWindowButton);
            deleteWindowButton.setId(View.generateViewId());

            set.clone(layoutMain);

            // if it's the first window - let it match constraint layout
            // otherwise - let it be constrained relative to the previous window
            if (firstElement) {
                set.connect(windowTextView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 40);
            } else {
                set.connect(windowTextView.getId(), ConstraintSet.TOP, previousElementId, ConstraintSet.BOTTOM, 20);
            }
            set.connect(windowTextView.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 80);

            set.applyTo(layoutMain);

            // Now connect the deletion button to the text view
            set.connect(deleteWindowButton.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 40);
            if (firstElement) {
                set.connect(deleteWindowButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 40);
                firstElement = false;
            } else {
                set.connect(deleteWindowButton.getId(), ConstraintSet.TOP, previousElementId, ConstraintSet.BOTTOM, 60);
            }

            set.applyTo(layoutMain);

            // Finally, if there was planned sum and its progress bar is not null - show it too
            if (progressBar != null) {
                Log.d("DEBUG", "progress bar is not null");
                set.connect(progressBar.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                set.connect(progressBar.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                set.connect(progressBar.getId(), ConstraintSet.TOP, windowTextView.getId(), ConstraintSet.BOTTOM, 20);

                set.applyTo(layoutMain);
            }

            previousElementId = windowTextView.getId();
        }
        windowsCursor.close();

        ImageButton addWindowButton = findViewById(R.id.addWindowButton);

        addWindowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");

                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                AddWindowDialogFragment addWindowDialog = new AddWindowDialogFragment();
                addWindowDialog.show(ft, "dialog");
            }
        });
    }

    public void openItemsActivity(long windowId)
    {
        Intent intent = new Intent(this, ItemsActivity.class);
        intent.putExtra("window_id", windowId);
        startActivity(intent);
    }

    public void setActionBar(String heading) {
        // TODO Auto-generated method stub

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(heading);
        actionBar.show();

    }

}
