package com.example.admin.expenses;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.admin.expenses.data.ExpensesDatabase;
import com.example.admin.expenses.data.Window;
import com.example.admin.expenses.fragments.AddItemDialogFragment;
import com.example.admin.expenses.fragments.AddParticipantDialogFragment;
import com.example.admin.expenses.helpers.Helper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class ItemsActivity extends AppCompatActivity {

    ExpensesDatabase db;
    ConstraintLayout layoutItems;
    LinearLayout layoutParticipants;
    ConstraintSet setItems;
    String[] participants;
    long windowID;
    long currentItemId;
    double totalSpent;
    Helper helper;
    TabHost tabHost;

    final int PARTICIPANT_LIST_ELEMENT_MINIMAL_HEIGHT = 100;
    final int PARTICIPANT_LIST_ELEMENT_LEFT_PADDING = 10;
    final int ADD_BUTTON_DIMENSION = 65;
    final int FONT_SIZE_NORMAL = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeActivity();
        setUpTabs();

        Cursor itemCursor = db.item().selectByWindowId(windowID);

        int previousItemViewId = 0;

        while (itemCursor.moveToNext()) {
            currentItemId = itemCursor.getLong(itemCursor.getColumnIndex("id"));

            TextView itemTextView = getItemTextView(itemCursor);
            increaseTotalSum(itemCursor);
            ImageButton deleteItemButton = addDeletionButtonForItem();
            setItems.clone(layoutItems);

            placeListElementOnLayout(itemTextView, previousItemViewId, ConstraintSet.LEFT);
            placeListElementOnLayout(deleteItemButton, previousItemViewId, ConstraintSet.RIGHT);

            previousItemViewId = itemTextView.getId();
        }

        itemCursor.close();

        setTotalsText();
        setAddItemButton();

        if (participants.length > 0) {
            createParticipantsList();
        }

        setAddParticipantsButton();
    }

    private void initializeActivity()
    {
        Intent intent = getIntent();
        setContentView(R.layout.activity_items);
        initializeFields(intent);
    }

    private void initializeFields(Intent intent)
    {
        db = ExpensesDatabase.getInstance(this);
        setItems = new ConstraintSet();
        layoutItems = findViewById(R.id.layout_item_list);
        layoutParticipants = findViewById(R.id.layout_participants_list);
        windowID = intent.getLongExtra("window_id", 0);
        totalSpent = 0;
        String participantsField = db.window().selectParticipantsByWindowId(windowID);

        if (participantsField != null) {
            participants = participantsField.split(",");
        } else {
            participants = new String[0];
        }

        float displayDensity = getResources().getDisplayMetrics().density;
        helper = new Helper(displayDensity);
        tabHost = findViewById(R.id.tabHost);
        tabHost.setup();
    }

    private void setUpTabs()
    {
        TabHost.TabSpec specs = getTabSpecForTitle("Expenses", R.id.tab_layout_items);

        tabHost.addTab(specs);

        specs = getTabSpecForTitle("Participants", R.id.tab_layout_participants);
        tabHost.addTab(specs);
    }

    private TabHost.TabSpec getTabSpecForTitle(String title, int layoutID)
    {
        TabHost.TabSpec specs = tabHost.newTabSpec(title);

        View view = LayoutInflater.from(this).inflate(R.layout.tab_title, null);
        TextView titleView = view.findViewById(R.id.tabsText);
        titleView.setText(title);

        specs.setIndicator(view);
        specs.setContent(layoutID);
        return specs;
    }

    private TextView getItemTextView(Cursor cursor)
    {
        TextView itemTextView = new TextView(this);
        String description = cursor.getString(cursor.getColumnIndex("description"));
        double sum = cursor.getDouble(cursor.getColumnIndex("sum"));

        String itemText = String.format("%s\nSum:%s\n", description, Double.toString(sum));

        itemTextView.setText(itemText);
        layoutItems.addView(itemTextView);
        itemTextView.setId(View.generateViewId());

        return itemTextView;
    }


    private void increaseTotalSum(Cursor cursor) {
        double sum = cursor.getDouble(cursor.getColumnIndex("sum"));
        totalSpent += sum;
    }

    private ImageButton addDeletionButtonForItem() {
        ImageButton deleteItemButton = new ImageButton(this);
        deleteItemButton.setBackground(this.getResources().getDrawable(R.drawable.delete_button));
        deleteItemButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                try {
                    db.beginTransaction();
                    db.item().deleteById(currentItemId);
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

        return deleteItemButton;
    }

    private void placeListElementOnLayout(View view, int previousElementId, int horizontalAlign) {
        if (previousElementId == 0) {
            setItems.connect(view.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 40);
        } else {
            setItems.connect(view.getId(), ConstraintSet.TOP, previousElementId, ConstraintSet.BOTTOM, 20);
        }
        setItems.connect(view.getId(), horizontalAlign, ConstraintSet.PARENT_ID, horizontalAlign, 80);

        setItems.applyTo(layoutItems);
    }

    private void setTotalsText()
    {
        String text = String.format(Locale.getDefault(), "Total sum: %.2f", totalSpent);

        double plannedSum = db.window().selectPlannedSumByWindowsId(windowID);

        if (plannedSum > 0) {
            text += String.format(Locale.getDefault(), "\nPlanned sum: %.2f", plannedSum);
            text += String.format(Locale.getDefault(), "\nRemaining money to spend: %.2f", (plannedSum - totalSpent));
        }

        TextView totalSumTextView = findViewById(R.id.totals_item_text_view);
        totalSumTextView.setText(text);
    }

    private void setAddItemButton() {
        ImageButton addItemButton = findViewById(R.id.addItemButton);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AddItemDialogFragment dialog = new AddItemDialogFragment();
                setAddButtonListener(dialog);
            }
        });
    }

    private void setAddParticipantsButton() {
        ImageButton addParticipantButton = new ImageButton(this);
        LinearLayout imageLayout = new LinearLayout(this);

        imageLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AddParticipantDialogFragment dialog = new AddParticipantDialogFragment();
                setAddButtonListener(dialog);
            }
        });

        int dimensionPixels = helper.getPixelsFromDps(ADD_BUTTON_DIMENSION);

        imageLayout.setBackgroundResource(R.drawable.list_item_border);
        addParticipantButton.setBackgroundResource(R.drawable.add_button);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(dimensionPixels, dimensionPixels);
        addParticipantButton.setLayoutParams(lp);
        imageLayout.addView(addParticipantButton);
        layoutParticipants.addView(imageLayout);
    }

    private void setAddButtonListener(DialogFragment dialog)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");

        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        Bundle args = new Bundle();
        args.putLong("window_id", windowID);
        args.putString("window_participants", TextUtils.join(",", participants));
        dialog.setArguments(args);
        dialog.show(ft, "dialog");
    }

    private void createParticipantsList()
    {
        int previousParticipantViewId = 0;

        for (int i = 0; i < participants.length; i++) {

            TextView participantView = getParticipantListElementView(participants[i]);

            placeParticipantViewOnList(participantView, previousParticipantViewId);

            previousParticipantViewId = participantView.getId();
        }
    }

    private TextView getParticipantListElementView(String currentParticipant) {
        TextView participantView = new TextView(this);

        String text = getDebtsToOtherParticipants(currentParticipant);

        participantView.setText(text);
        layoutParticipants.addView(participantView);
        participantView.setId(View.generateViewId());

        int minHeight = helper.getPixelsFromDps(PARTICIPANT_LIST_ELEMENT_MINIMAL_HEIGHT);
        float textSize = (float) helper.getPixelsFromDps(FONT_SIZE_NORMAL);
        participantView.setMinHeight(minHeight);
        participantView.setBackgroundResource(R.drawable.list_item_border);
        participantView.setTextSize(textSize);
        participantView.setGravity(Gravity.CENTER_VERTICAL);

        return participantView;
    }

    private String getDebtsToOtherParticipants(String currentParticipant)
    {
        String text = currentParticipant;

        for (int j = 0; j < participants.length; j++) {
            if (currentParticipant.equals(participants[j])) {
                continue;
            }

            text += getPersonalDebtString(currentParticipant, participants[j]);
        }

        return text;
    }

    private String getPersonalDebtString(String firstParticipant, String secondParticipant)
    {
        String text = "";

        double ownedDebt = db.debt().selectPersonalDebt(firstParticipant, secondParticipant, windowID);
        double ownDebt = db.debt().selectPersonalDebt(secondParticipant, firstParticipant, windowID);

        //TODO cache this shit
        double debt = ownDebt - ownedDebt;

        if (debt > 0) {
            text = String.format(Locale.getDefault(), "\nOwns: %.2f to %s\n", debt, secondParticipant);
        }

        return text;
    }

    private void placeParticipantViewOnList(TextView participantView, int previousParticipantViewId) {
        // STUB
    }
}