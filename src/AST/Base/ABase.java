package AST.Base;

import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class ABase implements IBase {

	private PositionI position;

	public ABase(PositionI position) {
		this.position = position;
	}

	public PositionI eval(ExecutionStateI es) {
		/*
		 * A vérifier, page 26 du PDF
		 */
		return this.position;
	}

}
