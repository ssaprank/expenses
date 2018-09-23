package com.example.admin.expenses.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import android.database.Cursor;

@Dao
public interface ItemDao {
    /**
     * Inserts an item into the table.
     *
     * @param item A new item.
     * @return The row ID of the newly inserted window.
     */
    @Insert
    long insert(Item item);

    /**
     * Select all items.
     *
     * @return A {@link Cursor} of all the items in the table.
     */
    @Query("SELECT * FROM " + Item.TABLE_NAME)
    Cursor selectAll();

    /**
     * Select all items that belong to a window.
     *
     * @return A {@link Cursor} of all the items in the table that belong to a specific window.
     */
    @Query("SELECT * FROM " + Item.TABLE_NAME + " WHERE window_id = :windowId")
    Cursor selectByWindowId(long windowId);

    /**
     * Select an item by ID.
     *
     * @param id The row ID.
     * @return A {@link Cursor} of the selected item.
     */
    @Query("SELECT * FROM " + Item.TABLE_NAME + " WHERE id = :id")
    Cursor selectById(long id);

    /**
     * Delete an item by ID.
     *
     * @param id The row ID.
     * @return A number of items deleted. This should always be {@code 1}.
     */
    @Query("DELETE FROM " + Item.TABLE_NAME + " WHERE id = :id")
    int deleteById(long id);

}
