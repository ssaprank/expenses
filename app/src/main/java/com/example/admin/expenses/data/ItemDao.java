package com.example.admin.expenses.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import android.database.Cursor;

@Dao
public abstract class ItemDao implements BaseDao<Item> {

    @Query("SELECT * FROM " + Item.TABLE_NAME + " WHERE window_id = :windowId")
    public abstract Cursor selectByWindowId(long windowId);

    @Query("DELETE FROM " + Item.TABLE_NAME + " WHERE id = :id")
    public abstract int deleteById(long id);
}
