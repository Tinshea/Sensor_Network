package AST;

import Interfaces.Ibexp;

public class SBExp implements Ibexp {
	
	
	private String sensorId;
	
	 public SBExp(String sensorId) {
		    this.sensorId = sensorId;
		  }
	 public String getsensorId() {
			// TODO Auto-generated method stub
			return sensorId;
		}

}
