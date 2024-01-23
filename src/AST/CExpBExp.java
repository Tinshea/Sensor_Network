package AST;

import Interfaces.Ibexp;
import Interfaces.Icexp;

public class CExpBExp implements Ibexp {
	
	
	private Icexp cexp;
	
	 public CExpBExp(Icexp cexp) {
		    this.cexp = cexp;
		  }
	 
	 public Icexp getcexp() {
			// TODO Auto-generated method stub
			return cexp;
		}

}
