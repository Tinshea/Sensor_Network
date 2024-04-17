package AST.BEXP;

import AST.BEXP.Ibexp;
import AST.CEXP.Icexp;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

import java.io.Serializable;

public class CExpBExp implements Ibexp , Serializable {
	private static final long serialVersionUID = 4L;
	
	private Icexp cexp;
	
	 public CExpBExp(Icexp cexp) {
		    this.cexp = cexp;
		  }
	@Override
	public boolean eval(ExecutionStateI es){
		return cexp.eval(es)	;
	 }


}

