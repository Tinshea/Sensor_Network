package AST;

import Interfaces.IRand;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class CRand implements IRand {
	
	private Double capteur;
	
	  public CRand(Double capteur) {
		    this.capteur = capteur;
		  }
	  
	  /***
	   *O[[CRand double]]σ : R = R[[double]])
	   */
	@Override
	public double eval(ProcessingNodeI curentNode) {
		return capteur;
	}

}
