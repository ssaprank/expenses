package com.example.admin.expenses.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Entity;

@Entity(tableName = Item.TABLE_NAME)
public class Item {
    public static final String TABLE_NAME = "Item";

    public static final String WINDOW_TABLE_NAME = "com.example.admin.expenses.data.Window";

    /** The unique ID of the item. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = "id")
    public long id;

    /** Id of the window the Item belongs to */
    @ColumnInfo(name="window_id")
    @ForeignKey(entity = Window.class, parentColumns = {"id"}, childColumns = {"windowID"}, onDelete = ForeignKey.CASCADE)
    public long windowID;

    /** Item description */
    @ColumnInfo(name = "description")
    public String description;

    /** Amount spent/gained. */
    @ColumnInfo(name = "sum")
    public double sum;

    /** Positive or Negative value to show whether it's a spend or a gain */
    @ColumnInfo(name = "sign")
    public boolean sign;

    /** Timestamp of item creation. */
    @ColumnInfo(name = "timestamp")
    public long created_timestamp;

    public Item() throws ClassNotFoundException {
    }
}
