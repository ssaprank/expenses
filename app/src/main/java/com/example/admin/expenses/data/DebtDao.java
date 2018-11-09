package com.example.admin.expenses.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

@Dao
public abstract class DebtDao implements BaseDao<Debt> {

    @Query("SELECT * FROM " + Debt.TABLE_NAME + " WHERE window_id = :windowId")
    public abstract Cursor selectByWindowId(long windowId);

    @Query("SELECT SUM(amount) FROM " + Debt.TABLE_NAME + " WHERE owner = :owner AND debtor = :debtor AND window_id = :windowId")
    public abstract double selectPersonalDebt(String owner, String debtor, long windowId);

    @Query("DELETE FROM " + Debt.TABLE_NAME + " WHERE owner = :owner AND debtor = :debtor AND window_id = :windowId")
    public abstract int delete(String owner, String debtor, long windowId);

    @Query("DELETE FROM " + Debt.TABLE_NAME + " WHERE item_id = :itemID")
    public abstract int deleteByItemId(long itemID);

    @Query("DELETE FROM " + Debt.TABLE_NAME + " WHERE window_id = :windowID")
    public abstract int deleteByWindowId(long windowID);
}
