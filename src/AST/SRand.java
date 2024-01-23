package AST;

import Interfaces.ISRand;

public class SRand implements ISRand {
	
	private String sensorId;
	
	  public SRand(String sensorId) {
		    this.sensorId = sensorId;
		  }
	@Override
	public String getsensorId() {
		// TODO Auto-generated method stub
		return sensorId;
	}

}
