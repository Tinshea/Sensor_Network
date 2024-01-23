package AST;

import Interfaces.IGather;

public class RGather implements IGather {

	private String sensorID;
	private IGather gather;
	
	public RGather(String sensorID,IGather gather) {
		this.sensorID = sensorID;
		this.gather = gather;
	}
	@Override
	public String getSensorID() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public IGather  getGather() {
		return this.gather;
	}

}
