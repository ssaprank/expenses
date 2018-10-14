package com.example.admin.expenses.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

@Dao
public interface DebtDao {
    /**
     * Inserts a debt into the table.
     *
     * @param debt A new debt.
     * @return The row ID of the newly inserted debt.
     */
    @Insert
    long insert(Debt debt);

    /**
     * Select all debts that belong to a window.
     *
     * @return A {@link Cursor} of all the debts in the table that belong to a specific window.
     */
    @Query("SELECT * FROM " + Debt.TABLE_NAME + " WHERE window_id = :windowId")
    Cursor selectByWindowId(long windowId);

    /**
     * Select a debt.
     *
     * @param owner The debt owner.
     * @param debtor The debtor.
     * @param windowId Id of the debt's window.
     * @return A {@link Cursor} of the selected debt.
     */
    @Query("SELECT SUM(amount) FROM " + Debt.TABLE_NAME + " WHERE owner = :owner AND debtor = :debtor AND window_id = :windowId")
    Cursor selectPersonalDebt(String owner, String debtor, long windowId);

    /**
     * Delete a debt.
     *
     * @param owner The debt owner.
     * @param debtor The debtor.
     * @param windowId Id of the debt's window.
     * @return A number of items deleted. This should always be {@code 1}.
     */
    @Query("DELETE FROM " + Debt.TABLE_NAME + " WHERE owner = :owner AND debtor = :debtor AND window_id = :windowId")
    int deleteById(String owner, String debtor, long windowId);
}
