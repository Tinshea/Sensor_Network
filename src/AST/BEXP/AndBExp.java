package AST.BEXP;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

import java.io.Serializable;

public class AndBExp implements Ibexp , Serializable {
	private static final long serialVersionUID = 3L;

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
