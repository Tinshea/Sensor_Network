package AST;

import java.util.ArrayList;
import java.util.List;

import Interfaces.ICont;
import Interfaces.Ibexp;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

public class BQuery implements QueryI{
	private Ibexp bexp;
	private ICont cont;
	
	public QueryResultI eval(ExecutionStateI es) {
		List<String> i = new ArrayList<>();
		i.add(es.getProcessingNode().getNodeIdentifier());
		i.addAll(cont.eval());
		
	}
	
}
