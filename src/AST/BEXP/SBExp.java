package AST.BEXP;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class SBExp implements Ibexp {
	
	
	private String sensorId;
	
	 public SBExp(String sensorId) {
		    this.sensorId = sensorId;
		  }
	
	public boolean eval(ExecutionStateI curentNode){
		SensorDataI currentNodeSensor = curentNode.getProcessingNode().getSensorData(sensorId);
		return (boolean)currentNodeSensor.getValue();
	}

}
	