package com.example.admin.expenses.helpers;

public class Helper {

    private float displayDensity;

    public Helper(float displayDensity)
    {
        this.displayDensity = displayDensity;
    }

    public int getPixelsFromDps(int dps)
    {
        return (int) (dps * displayDensity + 0.5f);
    }
}
