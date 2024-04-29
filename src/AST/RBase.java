package AST;

import Interfaces.IBase;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class RBase implements IBase{

	private NodeInfoI noeudInitial;
	/*
	 * A modifier, par le mot-clé this auquel
	cas on prendra pour position de référence celle du nœud recevant initialement
	 la requête. 
	 */
	public RBase(NodeInfoI noeudInitial) {
		this.noeudInitial = noeudInitial;
	}
	
	public PositionI eval(ExecutionStateI es) {
		return noeudInitial.nodePosition();
	}
}
