package AST.Cont;

import AST.Base.IBase;
import app.Models.ExecutionState;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

import java.io.Serializable;


public class FCont implements ICont, Serializable {
	private static final long serialVersionUID = 15L;

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

	/*
	 @Override
	public ArrayList<String> eval2(ExecutionStateI es) {
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
	}*/


	//Amine a fait
	@Override
	public void eval(ExecutionStateI es) {
		((ExecutionState)es).setMaxDistance(distanceMax);
		PositionI p = base.eval(es);
		((ExecutionState)es).setPosition(p);
		((ExecutionState)es).setFlooding();
		
		
	}
}
