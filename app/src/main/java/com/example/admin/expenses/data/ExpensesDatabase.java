package com.example.admin.expenses.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

/**
 * The Room database.
 */
@Database(entities = {BaseEntity.class, Window.class, Item.class, Debt.class}, version = 6, exportSchema = false)
public abstract class ExpensesDatabase extends RoomDatabase {

    /**
     * @return The DAO for the Window table.
     */
    @SuppressWarnings("WeakerAccess")
    public abstract WindowDao window();

    /**
     * @return The DAO for the Item table.
     */
    @SuppressWarnings("WeakerAccess")
    public abstract ItemDao item();

    /**
     * @return The DAO for the Debt table.
     */
    @SuppressWarnings("WeakerAccess")
    public abstract DebtDao debt();

    /** The only instance */
    private static ExpensesDatabase sInstance;

    public BaseDao getDaoObjectForEntity(BaseEntity entity)
    {
        if (entity instanceof Item) {
            return item();
        } else if (entity instanceof Debt) {
            return debt();
        } else if (entity instanceof Window) {
            return window();
        }

        return null;
    }

    /**
     * Gets the singleton instance of ExpensesDatabase.
     *
     * @param context The context.
     * @return The singleton instance of ExpensesDatabase.
     */
    public static synchronized ExpensesDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room
                    .databaseBuilder(context.getApplicationContext(), ExpensesDatabase.class, "ex")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return sInstance;
    }

    /**
     * Switches the internal implementation with an empty in-memory database.
     *
     * @param context The context.
     */
    @VisibleForTesting
    public static void switchToInMemory(Context context) {
        sInstance = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                ExpensesDatabase.class).build();
    }
}