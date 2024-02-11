package app.Components;

import java.util.ArrayList;
import java.util.List;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

public class QueryResult implements QueryResultI{
	protected List<SensorDataI> sd = new ArrayList<>();
	
	public QueryResult() {}
	
	@Override
	public boolean isBooleanRequest() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList<String> positiveSensorNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isGatherRequest() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ArrayList<SensorDataI> gatheredSensorsValues() {
		// TODO Auto-generated method stub
		return (ArrayList<SensorDataI>) this.sd;
	}

}
