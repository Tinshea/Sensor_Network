package Tests.AST.Query;

import AST.Cont.ICont;
import AST.Gather.FGather;
import AST.Gather.IGather;
import AST.Query.GQuery;
import app.Models.ExecutionState;
import app.Models.ProcessingNode;
import app.Models.QueryResult;
import app.Models.SensorData;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GQueryTest {
    private FGather fGather;
    private ExecutionStateI es;
    private final String sensorID = "sensor123";

    @Test
    void testEvalExecutesContAndUpdatesResult() {
        fGather = new FGather(sensorID);

        // Création d'un ProcessingNode avec des données de capteur
        Set<SensorDataI> sensorDataSet = new HashSet<>();
        sensorDataSet.add(new SensorData(sensorID, "Temperature", 25.5));  // Valeur fictive pour les besoins du test

        ProcessingNode processingNode = new ProcessingNode("node1", null, new HashSet<>(), sensorDataSet);

        // Configuration de l'ExecutionState pour utiliser notre ProcessingNode
        es = new ExecutionState(processingNode, null);
    }

}