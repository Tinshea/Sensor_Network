package AST.CEXP;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import AST.Rand.IRand;

import java.io.Serializable;

/**
 * The class GCExp must be used to compare two Icexp, it return true if the first Icexp is strictly superior
 * than the second Icexp.
 */
public class GCExp implements Icexp , Serializable {
	private static final long serialVersionUID = 9L;

	private IRand rand1;
	private IRand rand2;
	
	public GCExp(IRand rand1, IRand rand2) {
		    this.rand1 = rand1;
		    this.rand2 = rand2;
		  }
	public boolean eval(ExecutionStateI es) {
		return rand1.eval(es) > rand2.eval(es);
		}

}
