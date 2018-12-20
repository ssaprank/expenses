package com.example.admin.expenses.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.database.Cursor;

@Dao
public abstract class WindowChildParentDao implements BaseDao<Window.ChildParent> {
    @Query("SELECT COUNT(1) FROM " + Window.ChildParent.TABLE_NAME + " WHERE parent_id = :id")
    public abstract boolean isParentWindow(long id);

    @Query("SELECT COUNT(1) FROM " + Window.ChildParent.TABLE_NAME + " WHERE child_id = :id AND parent_id = :parentId")
    public abstract boolean isChildWindow(long id, long parentId);

    @Query("SELECT COUNT(1) FROM " + Window.ChildParent.TABLE_NAME + " WHERE child_id = :id AND parent_id IS NOT NULL AND parent_id > 0")
    public abstract boolean hasParent(long id);

    @Query("SELECT * FROM " + Window.ChildParent.TABLE_NAME + " WHERE parent_id = :windowId")
    public abstract Cursor selectChildren(long windowId);

    @Query("SELECT SUM(sum) FROM " + Item.TABLE_NAME + " WHERE window_id IN (" +
            "SELECT child_id FROM " + Window.ChildParent.TABLE_NAME + " WHERE parent_id = :windowId" +
            ")")
    public abstract double selectChildrenTotalSum(long windowId);

    @Query("DELETE FROM " + Window.ChildParent.TABLE_NAME + " WHERE child_id = :id AND parent_id = :parentId")
    public abstract int delete(long id, long parentId);
}
