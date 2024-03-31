package app.Models;

import java.util.ArrayList;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

public class QueryResult implements QueryResultI{
	private static final long serialVersionUID = 7080596922014476376L;
	
	protected ArrayList<SensorDataI> sd = new ArrayList<>();
	private boolean isGather = false;
	private boolean isBoolean= false;
	protected ArrayList<String> sensitiveNodes = new ArrayList<>();
	public QueryResult(ArrayList<SensorDataI> sd,ArrayList<String> sensitiveNodes) {
		this.sd = sd;
		this.sensitiveNodes = sensitiveNodes;
		}
	
	@Override
	public boolean isBooleanRequest() {
		return this.isBoolean;
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
		this.isBoolean = false;
		this.isGather = true;
	}
	public void setBoolean() {
		this.isBoolean = true;
		this.isGather = false;
	}

}
