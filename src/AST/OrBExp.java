package AST;

import Interfaces.Ibexp;

public class OrBExp implements Ibexp {
	
	
	private Ibexp bexp1;
	private Ibexp bexp2;
	
	  public OrBExp(Ibexp bexp1,Ibexp bexp2 ) {
		    this.bexp1 = bexp1;
		    this.bexp2 = bexp1;
		  }
	public Ibexp getbexp1() {
		// TODO Auto-generated method stub
		return bexp1;
	}

	public Ibexp getbexp2() {
		// TODO Auto-generated method stub
		return bexp2;
	}

}
