package com.example.admin.expenses.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;
import android.arch.persistence.room.Query;

import android.database.Cursor;

@Dao
public abstract class WindowDao implements BaseDao<Window> {

    @Query("SELECT * FROM " + Window.TABLE_NAME)
    public abstract Cursor selectAll();

    @Query("SELECT * FROM " + Window.TABLE_NAME + " WHERE id = :id")
    public abstract Cursor selectById(long id);

    @Query("DELETE FROM " + Window.TABLE_NAME + " WHERE id = :id")
    public abstract int deleteById(long id);

    @Query("UPDATE " + Window.TABLE_NAME + " SET participants = :participants WHERE id = :id")
    public abstract int updateParticipantsByWindowId(String participants, long id);
}
