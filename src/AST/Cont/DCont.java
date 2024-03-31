package AST.Cont;

import AST.Dirs.IDirs;
import app.Models.ExecutionState;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class DCont implements ICont{

	
	private IDirs directions;
	private int maxSauts;
	
	public DCont(IDirs directions,int maxSauts) {
		this.directions = directions;
		this.maxSauts = maxSauts;
	}
	@Override
	public void eval(ExecutionStateI es) {
		((ExecutionState) es).incrementHops();
		((ExecutionState) es).setMaxSauts(maxSauts);
		((ExecutionState) es).setDirectional();
		((ExecutionState) es).setDirections(this.directions.eval());		
	}
		
}

