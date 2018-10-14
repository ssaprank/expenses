package com.example.admin.expenses.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;
import android.arch.persistence.room.Query;

import android.database.Cursor;

@Dao
public interface WindowDao {
    /**
     * Inserts a window into the table.
     *
     * @param window A new window.
     * @return The row ID of the newly inserted window.
     */
    @Insert
    long insert(Window window);

    /**
     * Select all windows.
     *
     * @return A {@link Cursor} of all the windows in the table.
     */
    @Query("SELECT * FROM " + Window.TABLE_NAME)
    Cursor selectAll();

    /**
     * Select a window by ID.
     *
     * @param id The row ID.
     * @return A {@link Cursor} of the selected window.
     */
    @Query("SELECT * FROM " + Window.TABLE_NAME + " WHERE id = :id")
    Cursor selectById(long id);

    /**
     * Select window participants by ID.
     *
     * @param id The row ID.
     * @return A {@link Cursor} of the selected window.
     */
    @Query("SELECT participants FROM " + Window.TABLE_NAME + " WHERE id = :id")
    Cursor selectParticipantsById(long id);

    /**
     * Delete a window by ID.
     *
     * @param id The row ID.
     * @return A number of windows deleted. This should always be {@code 1}.
     */
    @Query("DELETE FROM " + Window.TABLE_NAME + " WHERE id = :id")
    int deleteById(long id);

    /**
     * Update the window (identified by id).
     *
     * @param window The window to update.
     * @return A number of windows updated. This should always be {@code 1}.
     */
    @Update
    int update(Window window);

    /**
     * Update participants by window id
     */
    @Query("UPDATE " + Window.TABLE_NAME + " SET participants = :participants WHERE id = :id")
    int updateParticipantsById(String participants, long id);
}
