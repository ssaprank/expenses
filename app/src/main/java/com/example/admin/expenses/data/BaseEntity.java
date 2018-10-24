package com.example.admin.expenses.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.lang.annotation.Annotation;

@Entity
public class BaseEntity implements Entity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = "id")
    public long id;

    @Override
    public String tableName() {
        return null;
    }

    @Override
    public Index[] indices() {
        return new Index[0];
    }

    @Override
    public boolean inheritSuperIndices() {
        return false;
    }

    @Override
    public String[] primaryKeys() {
        return new String[0];
    }

    @Override
    public ForeignKey[] foreignKeys() {
        return new ForeignKey[0];
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
