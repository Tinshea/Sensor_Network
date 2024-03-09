package AST;

import Interfaces.IRand;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class CRand implements IRand {
	
	private Double capteur;
	
	  public CRand(Double capteur) {
		    this.capteur = capteur;
		  }
	@Override
	public double eval(ProcessingNodeI curentNode) {
		// TODO Auto-generated method stub
		return capteur;
	}

}
