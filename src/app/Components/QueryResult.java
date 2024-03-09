package app.Components;

import java.util.ArrayList;
import java.util.List;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

public class QueryResult implements QueryResultI{
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
		// TODO Auto-generated method stub
		return !this.isGather;
	}

	@Override
	public ArrayList<String> positiveSensorNodes() {
		// TODO Auto-generated method stub
		return this.sensitiveNodes;
	}

	@Override
	public boolean isGatherRequest() {
		// TODO Auto-generated method stub
		return this.isGather;
	}

	@Override
	public ArrayList<SensorDataI> gatheredSensorsValues() {
		// TODO Auto-generated method stub
		return  this.sd;
	}
	
	public void setGather() {
		this.isGather = true;
	}
	public void setBoolean() {
		this.isGather = false;
	}

}
