package Interfaces;

import java.util.List;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public interface ICont {
	
	public List<SensorDataI> eval(ExecutionStateI es);

}
