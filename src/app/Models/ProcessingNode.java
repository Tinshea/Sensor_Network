package app.Models;

import java.util.Set;

import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class ProcessingNode implements ProcessingNodeI {
	private String nodeIdentifier;
	private PositionI position;
	private Set<NodeInfoI> neighbours;
	private SensorDataI sensorData;
	
	public ProcessingNode( String nodeIdentifier,
	 PositionI position,
	 Set<NodeInfoI> neighbours,
	 SensorDataI sensorData) {
		this.nodeIdentifier = nodeIdentifier;
		this.neighbours = neighbours;
		this.sensorData = sensorData;
		this.position = position;
	}
	
	@Override
	public String getNodeIdentifier() {
		return this.nodeIdentifier;
	}

	@Override
	public PositionI getPosition() {
		return this.position;
	}

	@Override
	public Set<NodeInfoI> getNeighbours() {
		return this.neighbours;
	}

	@Override
	public SensorDataI getSensorData(String sensorIdentifier) {
		return this.sensorData;
	}

	@Override
	public QueryResultI propagateRequest(String nodeIdentifier, RequestContinuationI requestContinuation)
			throws Exception {
		return null;
	}

	@Override
	public void propagateRequestAsync(String nodeIdentifier, RequestContinuationI requestContinuation)throws Exception {

	}

}
