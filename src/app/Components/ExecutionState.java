package app.Components;

import java.util.Set;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class ExecutionState implements ExecutionStateI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6009720675170853565L;
	private ProcessingNodeI pn;
	private QueryResultI qr;
	private boolean directional = false;
	private boolean flooding = false;
	private Set<Direction> directions;
	private int hops = 0;
	private int maxhops;
	private Double maxDistance;
	private PositionI p;
	
	public ExecutionState(ProcessingNodeI pn, QueryResultI qr) {
		this.pn = pn;
		this.qr = qr;
		this.p = pn.getPosition();
	}

	@Override
	public ProcessingNodeI getProcessingNode() {
		// TODO Auto-generated method stub
		return this.pn;
	}

	@Override
	public void updateProcessingNode(ProcessingNodeI pn) {
		// TODO Auto-generated method stub
		this.pn = pn;
	}

	@Override
	public QueryResultI getCurrentResult() {
		// TODO Auto-generated method stub
		return qr;
	}

	@Override
	public void addToCurrentResult(QueryResultI result) {
		// TODO Auto-generated method stub
		qr.gatheredSensorsValues().addAll(result.gatheredSensorsValues());
	}

	@Override
	public boolean isDirectional() {
		// TODO Auto-generated method stub
		return this.directional;
	}

	@Override
	public Set<Direction> getDirections() {
		// TODO Auto-generated method stub
		return this.directions;
	}

	@Override
	public boolean noMoreHops() {
		// TODO Auto-generated method stub
		return this.hops >= this.maxhops;
	}

	@Override
	public void incrementHops() {
		// TODO Auto-generated method stub
			this.hops++;
	}

	@Override
	public boolean isFlooding() {
		// TODO Auto-generated method stub
		return this.flooding;
	}

	@Override
	public boolean withinMaximalDistance(PositionI p) {
		// TODO Auto-generated method stub
		return this.p.distance(p)<=maxDistance;
	}
	public void setMaxDistance(double max) {
		this.maxDistance =max;
	}
	public void setFlooding() {
		this.flooding = true;
	}
	public void setDirectional() {
		this.directional = true;
	}
	public void setMaxSauts(int sauts) {
		this.maxhops = sauts;
	}
	
	public Set<Direction> setDirections(Set<Direction> directions) {
		// TODO Auto-generated method stub
		return this.directions = directions;
	}

	public void setPosition(PositionI p) {
		// TODO Auto-generated method stub
		this.p =p;
		
	}
}
