package app.Models;

import java.io.Serializable;
import java.time.Instant;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

public class SensorData implements SensorDataI {
	
	private static final long serialVersionUID = -4202232058504256513L;
	
	private String nodeIdentifier;
	private String sensorIdentifier;
	private Double value;
	private Instant timestamp; 
	
	public SensorData( String nodeIdentifier,
	 String sensorIdentifier,
	 Double value) {
		this.nodeIdentifier = nodeIdentifier;
		this.sensorIdentifier = sensorIdentifier;
		this.value = value;
		this.timestamp = Instant.now();
	}
	@Override
	public String getNodeIdentifier() {
		return this.nodeIdentifier;
	}

	@Override
	public Class<? extends Serializable> getType() {
		return value.getClass();
	}

	@Override
	public Serializable getValue() {
		return this.value;
	}

	@Override
	public String getSensorIdentifier() {
		return this.sensorIdentifier;
	}
	
	public String toString() {
		return this.sensorIdentifier +" : "+ this.value;
	}
	@Override
	   public Instant getTimestamp() {
        return timestamp;
    }

}
