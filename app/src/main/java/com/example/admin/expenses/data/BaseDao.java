package com.example.admin.expenses.data;

import android.arch.persistence.room.Insert;

public interface BaseDao<T> {
    @Insert
    long insert(T entity);
}
