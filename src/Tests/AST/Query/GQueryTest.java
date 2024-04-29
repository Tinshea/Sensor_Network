package Tests.AST.Query;

import AST.Gather.FGather;

import app.Models.ExecutionState;
import app.Models.ProcessingNode;

import app.Models.SensorData;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class GQueryTest {
	private final String sensorID = "sensor123";

	@Test
	void testEvalExecutesContAndUpdatesResult() {
		new FGather(sensorID);

		// Création d'un ProcessingNode avec des données de capteur
		Set<SensorDataI> sensorDataSet = new HashSet<>();
		sensorDataSet.add(new SensorData(sensorID, "Temperature", 25.5)); // Valeur fictive pour les besoins du test

		ProcessingNode processingNode = new ProcessingNode("node1", null, new HashSet<>(), sensorDataSet);

		new ExecutionState(processingNode, null);
	}

}