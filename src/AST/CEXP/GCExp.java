package AST.CEXP;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import AST.Rand.IRand;

public class GCExp implements Icexp {

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
