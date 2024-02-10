package AST;

import java.util.ArrayList;

import Interfaces.IBase;
import Interfaces.ICont;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;


public class FCont implements ICont{

	private IBase base;
	private double distanceMax;
	
	public FCont(IBase base,double distanceMax) {
		this.base = base;
		this.distanceMax = distanceMax;
		
	}
	
	public IBase getBase() {
		return this.base;
		
	}
	
	public double getDistanceMax() {
		return this.distanceMax;
	}

	@Override
	public ArrayList<String> eval(ExecutionStateI es) {
		PositionI p = es.getProcessingNode().getPosition();
		ArrayList<String> res = new ArrayList<>();
		double r = distanceMax;
		PositionI p = base.eval();
		if(noeuds.size()==0) {
			return null;
		}
		for(ProcessingNodeI n : noeuds) {
			double temp = p.distance(n.getPosition());// A réfléchir
			if(temp<distanceMax) {
				res.add(n);
			}
		}
		return res;
	}
}
