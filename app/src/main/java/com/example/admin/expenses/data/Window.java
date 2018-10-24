package com.example.admin.expenses.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Entity;

@Entity(tableName = Window.TABLE_NAME, inheritSuperIndices = true)
public class Window extends BaseEntity {
    public static final String TABLE_NAME = "Window";

    /** Name of the window */
    @ColumnInfo(name = "name")
    public String name;

    /** Spending limit for this window. */
    @ColumnInfo(name = "planned_sum")
    public double planned;

    /** Comma separated string of people participating in this window. */
    @ColumnInfo(name = "participants")
    public String participants;
}
