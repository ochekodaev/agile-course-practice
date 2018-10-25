package ru.unn.agile.complexnumber.model;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class RumiantsevaMariaTest {
    @Test
    public void canAddComplexNumbers() {
        ComplexNumber z1 = new ComplexNumber(-10, 20);
        ComplexNumber z2 = new ComplexNumber(30, -40);

        ComplexNumber z = z1.add(z2);

        assertEquals(new ComplexNumber(20, -20), z);
    }

    @Test
    public void canMultiplyComplexNumbers() {
        ComplexNumber z1 = new ComplexNumber(5, 4);
        ComplexNumber z2 = new ComplexNumber(6, 3);

        ComplexNumber z = z1.multiply(z2);

        assertEquals(new ComplexNumber(18, 39), z);
    }

    @Test
    public void canMultiplyComplexNumberByZero() {
        ComplexNumber z1 = new ComplexNumber(2, 2);
        ComplexNumber z2 = new ComplexNumber(0, 0);

        ComplexNumber z = z1.multiply(z2);

        assertEquals(new ComplexNumber(0, 0), z);
    }
}
