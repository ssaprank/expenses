package com.example.admin.expenses.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = Window.TABLE_NAME, inheritSuperIndices = true)
public class Window extends BaseEntity {
    public static final String TABLE_NAME = "Window";

    /** Name of the window */
    @ColumnInfo(name = "name")
    public String name;

    /** Spending limit for this window. */
    @ColumnInfo(name = "planned_sum")
    public double planned;

    /** Comma separated string of people participating in this window. */
    @ColumnInfo(name = "participants")
    public String participants;

    @Entity(
            tableName=ChildParent.TABLE_NAME,
            primaryKeys={"parent_id", "child_id"},
            foreignKeys={
                    @ForeignKey(
                            entity=Window.class,
                            parentColumns="id",
                            childColumns="parent_id",
                            onDelete=CASCADE),
                    @ForeignKey(
                            entity=Window.class,
                            parentColumns="id",
                            childColumns="child_id",
                            onDelete=CASCADE)},
            indices={@Index(value="parent_id"), @Index(value="child_id")}
    )
    public static class ChildParent {
        public static final String TABLE_NAME = "window_child_parent";

        @NonNull
        @ColumnInfo(name = "parent_id")
        public long parentId;
        @NonNull
        @ColumnInfo(name = "child_id")
        public long childId;

        public ChildParent(long childId, long parentId) {
            this.childId = childId;
            this.parentId = parentId;
        }

    }
}
