package com.example.admin.expenses.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Entity;

@Entity(tableName = Window.TABLE_NAME)
public class Window {
    public static final String TABLE_NAME = "Window";

    /** The unique ID of the window. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = "id")
    public long id;

    /** Name of the window */
    @ColumnInfo(name = "name")
    public String name;

    /** Spending limit for this window. */
    @ColumnInfo(name = "planned_sum")
    public double planned;

    /** People participating in spendings of this window. */
    /** Comma separated string */
    @ColumnInfo(name = "participants")
    public double participants;
}
