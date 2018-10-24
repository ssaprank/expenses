package com.example.admin.expenses.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = Debt.TABLE_NAME, inheritSuperIndices = true)
public class Debt extends BaseEntity{
    public static final String TABLE_NAME = "Debt";

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
