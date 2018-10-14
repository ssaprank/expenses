package com.example.admin.expenses.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = Debt.TABLE_NAME, primaryKeys = {"owner", "debtor", "window_id"})
public class Debt {
    public static final String TABLE_NAME = "Debt";

    /** The unique ID of the window. */
    @NonNull
    @ColumnInfo(name = "owner")
    public String owner;

    /** Spending limit for this window. */
    @NonNull
    @ColumnInfo(name = "debtor")
    public double debtor;

    /** People participating in spendings of this window. */
    /** Comma separated string */
    @ColumnInfo(name = "amount")
    public double amount;

    /** People participating in spendings of this window. */
    /** Comma separated string */
    @ColumnInfo(name = "window_id")
    @NonNull
    @ForeignKey(entity = Window.class, parentColumns = {"id"}, childColumns = {"windowID"}, onDelete = ForeignKey.CASCADE)
    public double windowId;
}
