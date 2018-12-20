package com.example.admin.expenses;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.database.Cursor;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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

    final int LIST_ELEMENT_MINIMAL_HEIGHT = 120;
    final int FONT_SIZE_NORMAL = 7;
    final int ADD_BUTTON_DIMENSION = 45;
    final double STATIC_BAR_WIDTH_DIMINISHING_COEFFICIENT = 0.95;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_main);
        db = ExpensesDatabase.getInstance(this);
        layoutMain = findViewById(R.id.layout_window_list);
        set = new ConstraintSet();
        helper = new Helper(getResources().getDisplayMetrics().density);

        Cursor windowsCursor = db.window().selectAll();

        while (windowsCursor.moveToNext()) {
            addWindowLayout(windowsCursor);
        }

        windowsCursor.close();

        setAddWindowButton();
    }

    private void addWindowLayout(Cursor cursor) {
        final long windowID = cursor.getLong(cursor.getColumnIndex("id"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        double plannedSum = cursor.getDouble(cursor.getColumnIndex("planned_sum"));
        double windowTotalSpent = getWindowSpent(windowID);

        TextView windowView = getWindowView(name, plannedSum, windowTotalSpent);

        windowView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openItemsActivity(windowID);
            }
        });

        ImageButton deleteWindowButton = getDeletionButtonForWindow(cursor);
        View emptyStaticBarView = getEmptyStaticBarView();
        View fillingStaticBarView = getFillingStaticBarView(windowTotalSpent, plannedSum);

        ConstraintLayout layout = new ConstraintLayout(this);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(windowView);
        layout.addView(deleteWindowButton);

        if (plannedSum > 0) {
            layout.addView(emptyStaticBarView);
            layout.addView(fillingStaticBarView);
        }

        float textSize = (float) helper.getPixelsFromDps(FONT_SIZE_NORMAL);
        windowView.setTextSize(textSize);

        ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        set.connect(windowView.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 20);
        set.connect(windowView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        if (plannedSum > 0) {
            set.connect(windowView.getId(), ConstraintSet.BOTTOM, emptyStaticBarView.getId(), ConstraintSet.TOP, 25);
            set.connect(windowView.getId(), ConstraintSet.BOTTOM, fillingStaticBarView.getId(), ConstraintSet.TOP, 25);
        } else {
            set.connect(windowView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 25);
        }
        set.applyTo(layout);

        set.connect(emptyStaticBarView.getId(), ConstraintSet.TOP, windowView.getId(), ConstraintSet.BOTTOM, 25);
        set.connect(emptyStaticBarView.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 5);
        set.connect(emptyStaticBarView.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 5);
        set.applyTo(layout);

        set.connect(fillingStaticBarView.getId(), ConstraintSet.TOP, windowView.getId(), ConstraintSet.BOTTOM, 25);
        set.connect(fillingStaticBarView.getId(), ConstraintSet.LEFT, emptyStaticBarView.getId(), ConstraintSet.LEFT);
        set.applyTo(layout);

        set.connect(deleteWindowButton.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 20);
        set.connect(deleteWindowButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.connect(deleteWindowButton.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        set.applyTo(layout);

        int minHeight = helper.getPixelsFromDps(LIST_ELEMENT_MINIMAL_HEIGHT);
        layout.setMinHeight(minHeight);
        layout.setBackgroundResource(R.drawable.list_item_border);
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

    private TextView getWindowView(String name, double plannedSum, double windowTotalSpent) {
        final TextView windowTextView = new TextView(this);

        String windowText = name;

        if (plannedSum > 0) {
            windowText  += String.format(
                    Locale.getDefault(),
                    "\n%s: %.2f",
                    getResources().getString(R.string.items_activity_planned_sum),
                    plannedSum
            );

            windowText  += String.format(
                    Locale.getDefault(),
                    "\n%s: %.2f",
                    getResources().getString(R.string.items_activity_total_sum),
                    windowTotalSpent
            );
        }

        windowTextView.setText(windowText);
        windowTextView.setId(View.generateViewId());

        return windowTextView;
    }

    private double getWindowSpent(long windowID)
    {
        double sum = db.item().selectTotalAmountByWindowId(windowID);
        sum += db.windowChildParent().selectChildrenTotalSum(windowID);

        return sum;
    }

    private View getEmptyStaticBarView()
    {
        View view = new View(this);
        view.setBackgroundResource(R.drawable.window_spent_static_bar);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30);

        view.setLayoutParams(params);
        view.setId(View.generateViewId());

        return view;
    }

    private View getFillingStaticBarView(double spent, double plannedSpent)
    {
        if (plannedSpent == 0) {
            View view =  new View(this);
            view.setId(View.generateViewId());
            return view;
        }

        int percentage = (int) (spent / plannedSpent * 100);

        if (percentage > 100) {
            percentage = 100;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        View view = new View(this);
        view.setId(View.generateViewId());

        if (percentage <= 0) {
            return view;
        }

        int barWidth = (int)(width / 100 * percentage * STATIC_BAR_WIDTH_DIMINISHING_COEFFICIENT);

        if (percentage == 100) {
            view.setBackgroundResource(R.drawable.window_spent_static_bar_red);
        } else if (percentage < 50) {
            view.setBackgroundResource(R.drawable.window_spent_static_bar_green);
        } else {
            view.setBackgroundResource(R.drawable.window_spent_static_bar_yellow);
        }

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(barWidth, 30);
        view.setLayoutParams(params);

        return view;
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
                    db.item().deleteByWindowId(windowID);
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
