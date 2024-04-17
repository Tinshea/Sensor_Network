package AST.BEXP;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

import java.io.Serializable;

public class SBExp implements Ibexp, Serializable {
	
	private String sensorId;
	
	 public SBExp(String sensorId) {
		    this.sensorId = sensorId;
		  }
	
	public boolean eval(ExecutionStateI es){
		SensorDataI currentNodeSensor = es.getProcessingNode().getSensorData(sensorId);
		return (boolean)currentNodeSensor.getValue();
	}

}
	