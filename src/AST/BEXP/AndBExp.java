package AST.BEXP;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

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

	public boolean eval(ExecutionStateI es){
		return bexp1.eval(es) && bexp2.eval(es);
	}

}
