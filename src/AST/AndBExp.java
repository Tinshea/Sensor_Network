package AST;

import Interfaces.Ibexp;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class AndBExp implements Ibexp {


	/*
	Commit test
	 */
	
	private Ibexp bexp1;
	private Ibexp bexp2;
	
	  public AndBExp(Ibexp bexp1,Ibexp bexp2 ) {
		    this.bexp1 = bexp1;
		    this.bexp2 = bexp2;
		  }

	public boolean eval(ProcessingNodeI curentNode){
		return bexp1.eval(curentNode) && bexp2.eval(curentNode);
	}

}
