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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.admin.expenses.data.*;
import com.example.admin.expenses.fragments.AddItemDialogFragment;
import com.example.admin.expenses.fragments.AddWindowDialogFragment;
import com.example.admin.expenses.helpers.Helper;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ExpensesDatabase db;
    LinearLayout layoutMain;
    ConstraintSet set;
    Helper helper;

    final int LIST_ELEMENT_MINIMAL_HEIGHT = 100;
    final int FONT_SIZE_NORMAL = 10;
    final int ADD_BUTTON_DIMENSION = 45;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = ExpensesDatabase.getInstance(this);
        layoutMain = findViewById(R.id.layout_window_list);
        set = new ConstraintSet();
        helper = new Helper(getResources().getDisplayMetrics().density);

        setActionBar();

        Cursor windowsCursor = db.window().selectAll();

        while (windowsCursor.moveToNext()) {
            addWindowLayout(windowsCursor);
        }

        windowsCursor.close();

        setAddWindowButton();
    }

    private void addWindowLayout(Cursor cursor) {
        TextView windowView = getWindowView(cursor);
        ImageButton deleteWindowButton = getDeletionButtonForWindow(cursor);

        ConstraintLayout layout = new ConstraintLayout(this);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(windowView);
        layout.addView(deleteWindowButton);

        int minHeight = helper.getPixelsFromDps(LIST_ELEMENT_MINIMAL_HEIGHT);
        float textSize = (float) helper.getPixelsFromDps(FONT_SIZE_NORMAL);
        layout.setMinHeight(minHeight);
        layout.setBackgroundResource(R.drawable.list_item_border);
        windowView.setTextSize(textSize);

        ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        set.connect(windowView.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 20);
        set.connect(windowView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.connect(windowView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        set.applyTo(layout);

        set.connect(deleteWindowButton.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 20);
        set.connect(deleteWindowButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.connect(deleteWindowButton.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        set.applyTo(layout);

        layoutMain.addView(layout);
    }

    private void setAddWindowButton() {
        ImageButton addWindowButton = new ImageButton(this);
        LinearLayout imageLayout = new LinearLayout(this);
        int minHeight = helper.getPixelsFromDps(LIST_ELEMENT_MINIMAL_HEIGHT);
        imageLayout.setMinimumHeight(minHeight);
        imageLayout.setGravity(Gravity.CENTER_VERTICAL);

        imageLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openAddWindowDialog();
            }
        });

        addWindowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openAddWindowDialog();
            }
        });

        int dimensionPixels = helper.getPixelsFromDps(ADD_BUTTON_DIMENSION);

        imageLayout.setBackgroundResource(R.drawable.list_item_border);
        addWindowButton.setBackgroundResource(R.drawable.add_button);
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(dimensionPixels, dimensionPixels);
        lp.setMargins(24,0,0,0);
        addWindowButton.setLayoutParams(lp);
        imageLayout.addView(addWindowButton);
        layoutMain.addView(imageLayout);
    }

    private TextView getWindowView(Cursor cursor) {
        final long windowID = cursor.getLong(cursor.getColumnIndex("id"));
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
                openItemsActivity(windowID);
            }
        });

        windowTextView.setId(View.generateViewId());

        return windowTextView;
    }

    private ImageButton getDeletionButtonForWindow(Cursor cursor) {
        final long windowID = cursor.getLong(cursor.getColumnIndex("id"));
        ImageButton button = new ImageButton(this);
        button.setBackground(this.getResources().getDrawable(R.drawable.delete_button));
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                try {
                    db.beginTransaction();
                    db.window().deleteById(windowID);
                    db.setTransactionSuccessful();
                } catch (Exception e) {} finally {
                    db.endTransaction();
                    finish();
                    startActivity(getIntent());
                }

                try {
                    db.beginTransaction();
                    db.debt().deleteByWindowId(windowID);
                    db.setTransactionSuccessful();
                } catch (Exception e) {} finally {
                    db.endTransaction();
                    finish();
                    startActivity(getIntent());
                }
            }
        });

        button.setId(View.generateViewId());

        return button;
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

    public void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(getResources().getString(R.string.windows_title));
        actionBar.show();
    }

}
