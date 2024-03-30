package AST;

import Interfaces.Ibexp;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class SBExp implements Ibexp {
	
	
	private String sensorId;
	
	 public SBExp(String sensorId) {
		    this.sensorId = sensorId;
		  }
	
	public boolean eval(ProcessingNodeI curentNode){
		SensorDataI currentNodeSensor = curentNode.getSensorData(sensorId);
		return (boolean)currentNodeSensor.getValue();
	}

}
	