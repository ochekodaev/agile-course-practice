package ru.unn.agile.lengthconverter.model;

public class LengthConverter {
    public final double convertMetersToMillimeters(final double value)
    {
        return value*1000;
    }

    public final double convertMillimetersToMeters(final double value)
    {
        return value*0.001;
    }
}
