package AST.CEXP;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public interface Icexp {
	public boolean eval(ExecutionStateI es);
}
