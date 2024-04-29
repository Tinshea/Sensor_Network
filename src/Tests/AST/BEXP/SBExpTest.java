package Tests.AST.BEXP;

import AST.BEXP.SBExp;
import app.Models.ExecutionState;
import app.Models.Position;
import app.Models.ProcessingNode;
import app.Models.SensorData;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;

public class SBExpTest {




    @Test
    public void testEval() {
        // Given
        String sensorId = "sensorTest";
        SensorDataI sensor = new SensorData("noeudTest","sensorTest",true);
        Set<SensorDataI> sensorDataISet = new HashSet<SensorDataI>();
        sensorDataISet.add(sensor);
        ProcessingNodeI processingNode = new ProcessingNode("noeudTest",new Position(2.0,3.0),null,sensorDataISet);
        ExecutionStateI es = new ExecutionState(processingNode,null);
        SBExp sbExp = new SBExp(sensorId);
        boolean result = sbExp.eval(es);

        assertTrue(result, "The eval method should return true when the sensor data value is true.");
    }

    // Serialization tests are similar to the previous examples
}
