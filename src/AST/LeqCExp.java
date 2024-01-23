package AST;

import Interfaces.Icexp;
import Interfaces.Irand;

public class LeqCExp implements Icexp {

	private Irand rand1;
	private Irand rand2;
	
	 public LeqCExp(Irand rand1, Irand rand2) {
		    this.rand1 = rand1;
		    this.rand1 = rand2;
		  }
	@Override
	public Irand getrand1() {
		// TODO Auto-generated method stub
		return rand1;
	}

	@Override
	public Irand getrand2() {
		// TODO Auto-generated method stub
		return rand2;
	}

}
