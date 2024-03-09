package app.Components;

import java.io.Serializable;
import java.time.Instant;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

public class SensorData implements SensorDataI {
	private String nodeIdentifier;
	private String sensorIdentifier;
	private Double value;
	
	public SensorData( String nodeIdentifier,
	 String sensorIdentifier,
	 Double value) {
		this.nodeIdentifier = nodeIdentifier;
		this.sensorIdentifier = sensorIdentifier;
		this.value = value;
	}
	@Override
	public String getNodeIdentifier() {
		// TODO Auto-generated method stub
		return this.nodeIdentifier;
	}


	@Override
	public Class<? extends Serializable> getType() {
		// TODO Auto-generated method stub
		return value.getClass();
	}

	@Override
	public Serializable getValue() {
		// TODO Auto-generated method stub
		return this.value;
	}

	@Override
	public Instant getTimestamp() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getSensorIdentifier() {
		// TODO Auto-generated method stub
		return this.sensorIdentifier;
	}
	
	public String toString() {
		return this.sensorIdentifier +" : "+ this.value;
	}

}
