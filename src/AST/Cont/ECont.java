package AST.Cont;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

import java.io.Serializable;

import app.Models.ExecutionState;

public class ECont implements ICont , Serializable {
	private static final long serialVersionUID = 14L;

	@Override
	public void eval(ExecutionStateI es) {
		((ExecutionState) es).setContinuation(false);;
	}
	
	
	

	
}
