package AST;

import Interfaces.ICRand;

public class CRand implements ICRand {
	
	private Double capteur;
	
	  public CRand(Double capteur) {
		    this.capteur = capteur;
		  }
	@Override
	public Double getcapteur() {
		// TODO Auto-generated method stub
		return capteur;
	}

}
