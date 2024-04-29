package AST;

import Interfaces.Ibexp;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class OrBExp implements Ibexp {
	
	
	private Ibexp bexp1;
	private Ibexp bexp2;
	
	  public OrBExp(Ibexp bexp1,Ibexp bexp2 ) {
		    this.bexp1 = bexp1;
		    this.bexp2 = bexp1;
		  }
	
	public boolean eval(ProcessingNodeI curentNode){
		return bexp1.eval(curentNode) || bexp2.eval(curentNode);
	}

}
