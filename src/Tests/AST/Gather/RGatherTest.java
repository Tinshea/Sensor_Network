package Tests.AST.Gather;

import app.Models.ExecutionState;
import app.Models.ProcessingNode;
import app.Models.SensorData;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import AST.Gather.FGather;
import AST.Gather.IGather;
import AST.Gather.RGather;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class RGatherTest {

    private RGather rGather;
    private ExecutionStateI es;
    private final String sensorID = "sensor123";
    private IGather simpleGather;

    @BeforeEach
    void setUp() {
        // Création d'un ProcessingNode avec des données de capteur
        Set<SensorDataI> sensorDataSet = new HashSet<>();
        sensorDataSet.add(new SensorData(sensorID, "Temperature", 25.5));  // Valeur fictive pour les besoins du test

        ProcessingNode processingNode = new ProcessingNode("node1", null, new HashSet<>(), sensorDataSet);

        // Configuration de l'ExecutionState pour utiliser notre ProcessingNode
        es = new ExecutionState(processingNode, null);

        // Utiliser une instance simple de FGather comme gather
        simpleGather = new FGather("sensor456");  // Assurez-vous que sensor456 est une ID valide dans votre ProcessingNode

        // Initialisation de RGather avec simpleGather et sensorID
        rGather = new RGather(sensorID, simpleGather);
    }

    @Test
    void testEvalCombinesResultsCorrectly() {
        // Exécution de eval pour récupérer les données de capteur combinées
        List<SensorDataI> result = rGather.eval(es);

        // Vérification que le résultat contient les bonnes données de capteur
        assertNotNull(result, "La liste des données de capteur ne devrait pas être nulle.");
        assertEquals(2, result.size(), "La liste devrait contenir exactement deux données de capteur.");
    }
}
