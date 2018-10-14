package com.example.admin.expenses.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Entity;

@Entity(tableName = Item.TABLE_NAME)
public class Item {
    public static final String TABLE_NAME = "Item";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = "id")
    public long id;

    @ColumnInfo(name="window_id")
    @ForeignKey(entity = Window.class, parentColumns = {"id"}, childColumns = {"windowID"}, onDelete = ForeignKey.CASCADE)
    public long windowID;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "sum")
    public double sum;

    @ColumnInfo(name = "timestamp")
    public long created_timestamp;
}
