package com.example.admin.expenses.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = Debt.TABLE_NAME)
public class Debt {
    public static final String TABLE_NAME = "Debt";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = "id")
    public long id;

    @NonNull
    @ColumnInfo(name = "owner")
    public String owner;

    @NonNull
    @ColumnInfo(name = "debtor")
    public String debtor;

    /** Comma separated string */
    @ColumnInfo(name = "amount")
    public double amount;

    @NonNull
    @ColumnInfo(name = "window_id")
    @ForeignKey(entity = Window.class, parentColumns = {"id"}, childColumns = {"windowID"}, onDelete = ForeignKey.CASCADE)
    public double windowID;
}
