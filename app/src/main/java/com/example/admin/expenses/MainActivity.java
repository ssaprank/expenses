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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
            double plannedSum = windowsCursor.getDouble(windowsCursor.getColumnIndex("planned_sum"));
            final long windowId = windowsCursor.getLong(windowsCursor.getColumnIndex("id"));

            // text view containing info about the window
            final TextView windowTextView = new TextView(this);
            windowTextView.setText(String.format(Locale.getDefault(), "%s\nPlanned sum: %.2f", name, plannedSum));

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
                set.connect(windowTextView.getId(), ConstraintSet.TOP, previousElementId, ConstraintSet.BOTTOM, 10);
            }

            set.connect(windowTextView.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 80);
            set.applyTo(layoutMain);

            // Now connect the deletion button to the text view
            set.connect(deleteWindowButton.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 40);
            if (firstElement) {
                set.connect(deleteWindowButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 40);
                firstElement = false;
            } else {
                set.connect(deleteWindowButton.getId(), ConstraintSet.TOP, previousElementId, ConstraintSet.BOTTOM, 40);
            }
            set.applyTo(layoutMain);

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
