package AST;

import Interfaces.Ibexp;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class NotBExp implements Ibexp {
	
	private Ibexp bexp;
	
	public NotBExp(Ibexp bexp) {
		this.bexp = bexp;
		}

	public boolean eval(ProcessingNodeI curentNode){
		return !bexp.eval(curentNode);
	}

}
