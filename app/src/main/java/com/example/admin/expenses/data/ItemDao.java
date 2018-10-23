package com.example.admin.expenses.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import android.database.Cursor;

@Dao
public interface ItemDao {

    @Insert
    long insert(Item item);

    @Query("SELECT * FROM " + Item.TABLE_NAME + " WHERE window_id = :windowId")
    Cursor selectByWindowId(long windowId);

    @Query("DELETE FROM " + Item.TABLE_NAME + " WHERE id = :id")
    int deleteById(long id);
}
