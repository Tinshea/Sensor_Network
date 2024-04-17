package Tests.AST.Rand;

import AST.Rand.CRand;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CRandTest {

    @Test
    public void testEvalReturnsCorrectValue() {
        // Given
        double expectedValue = 42.0;
        CRand cRand = new CRand(expectedValue);

        // When
        double result = cRand.eval(null);

        // Then
        assertEquals(expectedValue, result, 0.0, "The eval method should return the exact value set at construction.");
    }

    @Test
    public void testEvalWithNegativeValue() {
        // Given
        double negativeValue = -42.0;
        CRand cRand = new CRand(negativeValue);

        // When
        double result = cRand.eval(null);

        // Then
        assertEquals(negativeValue, result, 0.0, "The eval method should correctly return a negative value.");
    }

    @Test
    public void testEvalWithZeroValue() {
        // Given
        double zeroValue = 0.0;
        CRand cRand = new CRand(zeroValue);

        // When
        double result = cRand.eval(null);

        // Then
        assertEquals(zeroValue, result, 0.0, "The eval method should correctly return zero.");
    }

    @Test
    public void testEvalWithVeryLargeValue() {
        // Given
        double largeValue = Double.MAX_VALUE;
        CRand cRand = new CRand(largeValue);

        // When
        double result = cRand.eval(null);

        // Then
        assertEquals(largeValue, result, 0.0, "The eval method should correctly handle very large values.");
    }

    @Test
    public void testEvalWithVerySmallValue() {
        // Given
        double smallValue = Double.MIN_VALUE;
        CRand cRand = new CRand(smallValue);

        // When
        double result = cRand.eval(null);

        // Then
        assertEquals(smallValue, result, 0.0, "The eval method should correctly handle very small values.");
    }
}
