package AST;


import Interfaces.ICont;
import Interfaces.Ibexp;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

public class BQuery implements QueryI{
	private Ibexp bexp;
	private ICont cont;
	
	public QueryResultI eval(ExecutionStateI es) {
		QueryResultI res = es.getCurrentResult();
		for(ProcessingNodeI node : cont.eval(es)) {
			es.getCurrentResult().positiveSensorNodes().add(node.getNodeIdentifier());
		}
		if(es.getCurrentResult().positiveSensorNodes().size() == 0) {
			return res;
		}
		if(bexp.eval(es.getProcessingNode())) {
			es.getCurrentResult().positiveSensorNodes().add(es.getProcessingNode().getNodeIdentifier());
		}
		return res;
	}
	
}
