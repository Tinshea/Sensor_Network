package AST;

import Interfaces.IBase;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class ABase implements IBase {
	
	public PositionI eval(ExecutionStateI es) {
		/*
		 * A vérifier, page 26 du PDF
		 */
		return es.getProcessingNode().getPosition();
	}

}
