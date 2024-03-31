package AST.BEXP;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class NotBExp implements Ibexp {
	
	private Ibexp bexp;
	
	public NotBExp(Ibexp bexp) {
		this.bexp = bexp;
		}

	public boolean eval(ExecutionStateI es){
		return !bexp.eval(es);
	}

}
