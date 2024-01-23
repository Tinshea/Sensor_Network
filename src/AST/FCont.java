package AST;

import Interfaces.IBase;

public class FCont {

	private IBase base;
	private Double distanceMax;
	
	public FCont(IBase base,Double distanceMax ) {
		this.base = base;
		this.distanceMax = distanceMax;
	}
	
	public IBase getBase() {
		return this.base;
		
	}
	
	public Double getDistanceMax() {
		return this.distanceMax;
	}
}
