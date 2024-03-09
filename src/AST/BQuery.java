package AST;

import java.util.ArrayList;
import java.util.List;

import Interfaces.ICont;
import Interfaces.Ibexp;
import app.Components.QueryResult;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

public class BQuery implements QueryI{
	private Ibexp bexp;
	private ICont cont;
	
	public QueryResultI eval(ExecutionStateI es) {
		//Faire un setter boolean
		QueryResultI res = es.getCurrentResult();
		((QueryResult) res).setBoolean();
		if(bexp.eval(es.getProcessingNode())) {
			res.positiveSensorNodes().add(es.getProcessingNode()
					.getNodeIdentifier());
		}
		cont.eval(es);
		return res;
	}
	
}
