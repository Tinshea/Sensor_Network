package AST.BEXP;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

import java.io.Serializable;

public class NotBExp implements Ibexp , Serializable {
	private static final long serialVersionUID = 5L;
	
	private Ibexp bexp;
	
	public NotBExp(Ibexp bexp) {
		this.bexp = bexp;
		}

	public boolean eval(ExecutionStateI es){
		return !bexp.eval(es);
	}

}
