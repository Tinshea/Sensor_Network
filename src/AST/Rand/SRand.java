package AST.Rand;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

import java.io.Serializable;

public class SRand implements IRand , Serializable {
	private static final long serialVersionUID = 23L;
	
	private String sensorId;
	
	  public SRand(String sensorId) {
		    this.sensorId = sensorId;
		  }
	@Override
	public double eval(ExecutionStateI curentNode) {
		SensorDataI currentNodeSensor = curentNode.getProcessingNode().getSensorData(sensorId);
		return (Double)currentNodeSensor.getValue();
	}

}
