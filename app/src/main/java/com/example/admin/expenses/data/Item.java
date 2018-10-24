package com.example.admin.expenses.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Entity;

@Entity(tableName = Item.TABLE_NAME, inheritSuperIndices = true)
public class Item extends BaseEntity {
    public static final String TABLE_NAME = "Item";

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "sum")
    public double sum;

    @ColumnInfo(name = "timestamp")
    public long created_timestamp;

    @ColumnInfo(name="window_id")
    @ForeignKey(entity = Window.class, parentColumns = {"id"}, childColumns = {"windowID"}, onDelete = ForeignKey.CASCADE)
    public long windowID;
}
