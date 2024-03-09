package app.Models;

import java.util.ArrayList;
import java.util.List;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

public class QueryResult implements QueryResultI{
	private static final long serialVersionUID = 7080596922014476376L;
	
	protected ArrayList<SensorDataI> sd = new ArrayList<>();
	private boolean isGather;
	protected ArrayList<String> sensitiveNodes = new ArrayList<>();
	public QueryResult(ArrayList<SensorDataI> sd,boolean isGather,ArrayList<String> sensitiveNodes) {
		this.sd = sd;
		this.isGather = isGather;
		this.sensitiveNodes = sensitiveNodes;
		}
	
	@Override
	public boolean isBooleanRequest() {
		return !this.isGather;
	}

	@Override
	public ArrayList<String> positiveSensorNodes() {
		return this.sensitiveNodes;
	}

	@Override
	public boolean isGatherRequest() {
		return this.isGather;
	}

	@Override
	public ArrayList<SensorDataI> gatheredSensorsValues() {
		return  this.sd;
	}
	
	public void setGather() {
		this.isGather = true;
	}
	public void setBoolean() {
		this.isGather = false;
	}

}
