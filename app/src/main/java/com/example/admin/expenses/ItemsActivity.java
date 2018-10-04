package com.example.admin.expenses;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.admin.expenses.data.ExpensesDatabase;
import com.example.admin.expenses.fragments.AddItemDialogFragment;

import java.util.Locale;

public class ItemsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        final long windowId = intent.getLongExtra("window_id", 0);

        if (windowId == 0) {
            // STUB - HANDLE THE EXCEPTION IF WRONG WINDOW ID PROVIDED
        }

        setContentView(R.layout.activity_items);
        final ExpensesDatabase db = ExpensesDatabase.getInstance(this);
        Cursor itemCursor = db.item().selectByWindowId(windowId);

        boolean firstElement = true;
        int previousElementId = 0;
        double totalSpent = 0;

        ConstraintSet set = new ConstraintSet();
        ConstraintLayout layoutItems = findViewById(R.id.layout_item_list);

        // Iterate over the list of windows
        while (itemCursor.moveToNext()) {
            String description = itemCursor.getString(itemCursor.getColumnIndex("description"));
            double sum = itemCursor.getDouble(itemCursor.getColumnIndex("sum"));
            final long itemId = itemCursor.getLong(itemCursor.getColumnIndex("id"));

            totalSpent += sum;
            String sumString = Double.toString(sum);

            String itemText = String.format("%s\nSum:%s\n", description, sumString);

            TextView itemTextView = new TextView(this);

            itemTextView.setText(itemText);
            layoutItems.addView(itemTextView);
            itemTextView.setId(View.generateViewId());

            // button view for item deletion
            ImageButton deleteItemButton = new ImageButton(this);
            deleteItemButton.setBackground(this.getResources().getDrawable(R.drawable.delete_button));
            deleteItemButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    try {
                        db.beginTransaction();
                        db.item().deleteById(itemId);
                        db.setTransactionSuccessful();
                    } catch (Exception e) {} finally {
                        db.endTransaction();
                        finish();
                        startActivity(getIntent());
                    }
                }
            });

            layoutItems.addView(deleteItemButton);
            deleteItemButton.setId(View.generateViewId());

            set.clone(layoutItems);

            // if it's the first window - let it match constraint layout
            // otherwise - let it be constrained relative to the previous window
            if (firstElement) {
                set.connect(itemTextView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 40);
            } else {
                set.connect(itemTextView.getId(), ConstraintSet.TOP, previousElementId, ConstraintSet.BOTTOM, 10);
            }

            set.connect(itemTextView.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 80);
            set.applyTo(layoutItems);

            // Now connect the deletion button to the text view
            set.connect(deleteItemButton.getId(), ConstraintSet.LEFT, itemTextView.getId(), ConstraintSet.RIGHT, 40);
            if (firstElement) {
                set.connect(deleteItemButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 40);
                firstElement = false;
            } else {
                set.connect(deleteItemButton.getId(), ConstraintSet.TOP, previousElementId, ConstraintSet.BOTTOM, 10);
            }
            set.applyTo(layoutItems);

            previousElementId = itemTextView.getId();
        }

        itemCursor.close();

        String totalSumString = String.format(Locale.getDefault(), "Total sum: %.2f", totalSpent);

        Cursor windowPlannedSumCursor = db.window().selectById(windowId);

        if (windowPlannedSumCursor.moveToFirst() && windowPlannedSumCursor.getCount() >= 1) {
            int index = windowPlannedSumCursor.getColumnIndex("planned_sum");

            if (index > 0) {
                double windowPlannedSum = windowPlannedSumCursor.getDouble(index);

                if (windowPlannedSum > 0) {
                    totalSumString += String.format(Locale.getDefault(), "\nPlanned sum: %.2f", windowPlannedSum);
                    totalSumString += String.format(Locale.getDefault(), "\nRemaining money to spend: %.2f", (windowPlannedSum - totalSpent));
                }
            }
            windowPlannedSumCursor.close();
        }


        // Text view showing the total amount of money spent
        TextView totalSumTextView =  findViewById(R.id.totals_item_text_view);
        totalSumTextView.setText(totalSumString);

        ImageButton addItemButton = findViewById(R.id.addItemButton);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");

                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                AddItemDialogFragment addItemDialog = new AddItemDialogFragment();
                Bundle args = new Bundle();
                args.putLong("window_id", windowId);
                addItemDialog.setArguments(args);
                addItemDialog.show(ft, "dialog");
            }
        });

    }
}