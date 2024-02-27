package AST;

import java.util.ArrayList;

import Interfaces.IBase;
import Interfaces.ICont;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
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
	public ArrayList<SensorDataI> eval(ExecutionStateI es) {
		PositionI p = base.eval(es);
		// mettre pos et distanceMax
		ArrayList<SensorDataI> res = new ArrayList<>();
		if(es.getProcessingNode().getNeighbours().isEmpty()) {
			return res;
		}
		for(NodeInfoI n : es.getProcessingNode().getNeighbours()) {
			if(p.distance(n.nodePosition())<=distanceMax) {
			 res.add(n.nodeIdentifier());
			}
		}
		return res;
	}	
}
