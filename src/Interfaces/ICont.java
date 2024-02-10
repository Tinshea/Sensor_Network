package Interfaces;

import java.util.List;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public interface ICont {
	
	public List<ProcessingNodeI> eval(ExecutionStateI es);

}
