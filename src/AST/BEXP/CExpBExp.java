package AST.BEXP;

import AST.BEXP.Ibexp;
import AST.CEXP.Icexp;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class CExpBExp implements Ibexp {
	
	private Icexp cexp;
	
	 public CExpBExp(Icexp cexp) {
		    this.cexp = cexp;
		  }
	@Override
	public boolean eval(ExecutionStateI es){
		return cexp.eval(es)	;
	 }


}

