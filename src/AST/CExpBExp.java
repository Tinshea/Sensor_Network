package AST;

import Interfaces.Ibexp;
import Interfaces.Icexp;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class CExpBExp implements Ibexp {
	
	private Icexp cexp;
	
	 public CExpBExp(Icexp cexp) {
		    this.cexp = cexp;
		  }
	@Override
	public boolean eval(ProcessingNodeI curentNode){
		return cexp.eval(curentNode);
		}


}

