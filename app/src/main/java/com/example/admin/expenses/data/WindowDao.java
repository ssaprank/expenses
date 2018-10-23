package com.example.admin.expenses.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;
import android.arch.persistence.room.Query;

import android.database.Cursor;

@Dao
public interface WindowDao {

    @Insert
    long insert(Window window);

    @Query("SELECT * FROM " + Window.TABLE_NAME)
    Cursor selectAll();

    @Query("SELECT * FROM " + Window.TABLE_NAME + " WHERE id = :id")
    Cursor selectById(long id);

    @Query("DELETE FROM " + Window.TABLE_NAME + " WHERE id = :id")
    int deleteById(long id);

    @Query("UPDATE " + Window.TABLE_NAME + " SET participants = :participants WHERE id = :id")
    int updateParticipantsByWindowId(String participants, long id);
}
