package AST.Rand;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class CRand implements IRand {
	
	private Double capteur;
	
	  public CRand(Double capteur) {
		    this.capteur = capteur;
		  }
	@Override
	public double eval(ExecutionStateI curentNode) {
		return this.capteur;
	}

}
