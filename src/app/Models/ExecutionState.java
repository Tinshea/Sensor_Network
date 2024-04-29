package app.Models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class ExecutionState implements ExecutionStateI, Cloneable {
	private static final long serialVersionUID = 6009720675170853565L;
	private ProcessingNodeI pn;
	private QueryResultI queryResult;
	private boolean directional = false;
	private boolean flooding = false;
	private boolean isContinuation = false;
	private Set<Direction> directions;
	private int hops = 0;
	private int maxhops;
	private Double maxDistance;
	private PositionI p;

	public ExecutionState(ProcessingNodeI pn, QueryResultI queryResult) {
		this.pn = pn;
		this.queryResult = queryResult;
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
		return queryResult;
	}

	@Override
	public void addToCurrentResult(QueryResultI result) {
		queryResult.gatheredSensorsValues().addAll(result.gatheredSensorsValues());
		queryResult.positiveSensorNodes().addAll(result.positiveSensorNodes());
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
		cloned.queryResult = ((QueryResult)this.queryResult).clone(); 
		cloned.directional = this.directional;
		cloned.flooding = this.flooding;
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

	@Override
	public boolean isContinuationSet() {
		return this.isContinuation;
	}
	
	public void setContinuation(boolean isContinuation) {
		this.isContinuation = isContinuation;
	}
	
	public String buildQueryResultString() {
	    StringBuilder resultBuilder = new StringBuilder();
	    resultBuilder.append("request result :");

	    if (queryResult.isBooleanRequest()) {
	        resultBuilder.append(queryResult.positiveSensorNodes().toString());
	    } else if (queryResult.isGatherRequest()) {
	        resultBuilder.append(queryResult.gatheredSensorsValues().toString());
	    }

	    return resultBuilder.toString();
	}
	

	public void resetQuery() {
		this.queryResult =  new QueryResult(new ArrayList<>(), new ArrayList<>());
	}


}
