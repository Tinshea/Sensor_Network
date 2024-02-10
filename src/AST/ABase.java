package AST;

import Enums.Dir;
import Interfaces.IBase;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;

public class ABase implements IBase {

	private PositionI position;
	
	public PositionI eval() {
		/*
		 * A vérifier, page 26 du PDF
		 */
		return position;
	}

}
