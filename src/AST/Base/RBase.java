package AST.Base;

import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class RBase implements IBase{

	/*
	 A modifier, par le mot-clé this auquel
	cas on prendra pour position de référence celle du nœud recevant initialement
	 la requête. 
	 */
	public RBase() {
	}
	
	public PositionI eval(ExecutionStateI es) {
		return  es.getProcessingNode().getPosition();
		//return noeudInitial.nodePosition();
	}
}
