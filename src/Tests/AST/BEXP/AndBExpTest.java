package Tests.AST.BEXP;

import AST.BEXP.AndBExp;
import AST.BEXP.SBExp;
import app.Models.ExecutionState;
import app.Models.Position;
import app.Models.ProcessingNode;
import app.Models.SensorData;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AndBExpTest {
    @Test
    public void testAndEvaluation() {
        // Create SensorData instances
        SensorData sensorTrue = new SensorData("sensor1", "type1", true);
        SensorData sensorFalse = new SensorData("sensor2", "type2", false);

        // Create a set of SensorData for the ProcessingNode
        Set<SensorDataI> sensorDataISet = new HashSet<>();
        sensorDataISet.add(sensorTrue);
        sensorDataISet.add(sensorFalse);

        // Create ProcessingNode
        ProcessingNode processingNode = new ProcessingNode("nodeTest", new Position(2.0, 3.0), null, sensorDataISet);

        // Create ExecutionState
        ExecutionState es = new ExecutionState(processingNode, null);

        // True & True scenario
        SBExp trueExp1 = new SBExp("sensor1");
        SBExp trueExp2 = new SBExp("sensor1");
        AndBExp andTrueTrue = new AndBExp(trueExp1, trueExp2);
        assertTrue(andTrueTrue.eval(es), "AND expression should be true when both are true.");

        // True & False scenario
        SBExp falseExp = new SBExp("sensor2");
        AndBExp andTrueFalse = new AndBExp(trueExp1, falseExp);
        assertFalse(andTrueFalse.eval(es), "AND expression should be false when one is false.");

        // False & True scenario
        AndBExp andFalseTrue = new AndBExp(falseExp, trueExp1);
        assertFalse(andFalseTrue.eval(es), "AND expression should be false when one is false.");

        // False & False scenario
        SBExp falseExp2 = new SBExp("sensor2");
        AndBExp andFalseFalse = new AndBExp(falseExp, falseExp2);
        assertFalse(andFalseFalse.eval(es), "AND expression should be false when both are false.");
    }
}