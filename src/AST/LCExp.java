package AST;

import Interfaces.Icexp;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import Interfaces.IRand;

public class LCExp implements Icexp {

	private IRand rand1;
	private IRand rand2;
	
	public LCExp(IRand rand1, IRand rand2) {
		    this.rand1 = rand1;
		    this.rand1 = rand2;
		  }
	public boolean eval(ProcessingNodeI curentNode) {
		return rand1.eval(curentNode) < rand2.eval(curentNode);
		}

}
