package com.mrapps.batteryinfo.meter;


public class ValueFormatterImpl implements ValueFormatter {
    @Override
    public String getFormattedValue(double value) {
        return String.valueOf(value);
    }
}
