package app.Models;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class ExecutionState implements ExecutionStateI, Cloneable {
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
	}

	@Override
	public ProcessingNodeI getProcessingNode() {
		return this.pn;
	}

	@Override
	public void updateProcessingNode(ProcessingNodeI pn) {
		this.pn = pn;
	}

	@Override
	public QueryResultI getCurrentResult() {
		return qr;
	}

	@Override
	public void addToCurrentResult(QueryResultI result) {
		qr.gatheredSensorsValues().addAll(result.gatheredSensorsValues());
		qr.positiveSensorNodes().addAll(result.positiveSensorNodes());
	}

	@Override
	public boolean isDirectional() {
		return this.directional;
	}

	@Override
	public Set<Direction> getDirections() {
		return this.directions;
	}

	@Override
	public boolean noMoreHops() {
		return this.hops >= this.maxhops;
	}

	@Override
	public void incrementHops() {
		this.hops++;
	}

	@Override
	public boolean isFlooding() {
		return this.flooding;
	}

	@Override
	public boolean withinMaximalDistance(PositionI p) {
		return this.p.distance(p) <= maxDistance;
	}

	public void setMaxDistance(double max) {
		this.maxDistance = max;
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
		return this.directions = directions;
	}

	public void setPosition(PositionI p) {
		this.p = p;

	}

	@Override
	public ExecutionState clone() throws CloneNotSupportedException {
		ExecutionState cloned = (ExecutionState) super.clone();
		if (this.directions != null) {
			cloned.directions = new HashSet<>(this.directions);
		}

		return cloned;
	}

	public Integer getMaxHops() {
		return this.maxhops;
	}

	public Integer getHops() {
		return hops;
	}

	public Double getMaxDistance() {
		return this.maxDistance;
	}

	public PositionI getPosition() {
		return this.p;
	}

}
