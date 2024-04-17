package Tests.AST.Gather;

import app.Models.ProcessingNode;
import app.Models.SensorData;
import app.Models.ExecutionState;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import AST.Gather.FGather;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FGatherTest {

    private FGather fGather;
    private ExecutionStateI es;
    private final String sensorID = "sensor123";

    @BeforeEach
    void setUp() {
        // Configuration de FGather avec un sensorID
        fGather = new FGather(sensorID);

        // Création d'un ProcessingNode avec des données de capteur
        Set<SensorDataI> sensorDataSet = new HashSet<>();
        sensorDataSet.add(new SensorData(sensorID, "Temperature", 25.5));  // Valeur fictive pour les besoins du test

        ProcessingNode processingNode = new ProcessingNode("node1", null, new HashSet<>(), sensorDataSet);

        // Configuration de l'ExecutionState pour utiliser notre ProcessingNode
        es = new ExecutionState(processingNode, null);
    }

    @Test
    void testEvalReturnsCorrectSensorData() {
        // Exécution de eval pour récupérer les données de capteur
        List<SensorDataI> result = fGather.eval(es);

        // Vérification que le résultat contient la bonne donnée de capteur
        assertNotNull(result, "La liste des données de capteur ne devrait pas être nulle.");
        assertEquals(1, result.size(), "La liste devrait contenir exactement une donnée de capteur.");
        SensorDataI data = result.get(0);
        assertNotNull(data, "La donnée de capteur ne devrait pas être nulle.");
        assertEquals(sensorID, data.getNodeIdentifier(), "L'identifiant du capteur devrait correspondre à celui spécifié.");
        assertEquals(25.5, data.getValue(), "La valeur de la donnée de capteur devrait correspondre à la valeur fictive définie.");
    }
}
