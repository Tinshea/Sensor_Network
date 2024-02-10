package AST;

import Enums.Dir;
import Interfaces.IBase;
import Interfaces.INoeud;
import Interfaces.PositionI;

public class RBase implements IBase{

	private INoeud noeudInitial;
	/*
	 * A modifier, par le mot-clé this auquel
	cas on prendra pour position de référence celle du nœud recevant initialement
	 la requête. 
	 */
	public RBase(INoeud noeudInitial) {
		this.noeudInitial = noeudInitial;
	}
	
	public PositionI eval() {
		return noeudInitial.getPosition();
	}
}
