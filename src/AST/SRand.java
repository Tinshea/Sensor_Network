package AST;

import Interfaces.IRand;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class SRand implements IRand {
	
	private String sensorId;
	
	  public SRand(String sensorId) {
		    this.sensorId = sensorId;
		  }
	@Override
	public double eval(ProcessingNodeI curentNode) {
		SensorDataI currentNodeSensor = curentNode.getSensorData(sensorId);
		return (Double)currentNodeSensor.getValue();
	}

}
