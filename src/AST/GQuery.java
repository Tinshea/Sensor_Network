package AST;

import java.util.List;

import Interfaces.ICont;
import Interfaces.IGather;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

public class GQuery implements QueryI {
	private IGather gather;
	private ICont cont;
	
	public GQuery(IGather gather, ICont cont) {
		this.gather = gather;
		this.cont = cont;
	}
	
	public QueryResultI eval(ExecutionStateI es) {
		QueryResultI res = es.getCurrentResult();
		List<String> i = cont.eval(es);
		i.add(0,es.getProcessingNode().getNodeIdentifier());
		if(i.size() == 1) {
			res.gatheredSensorsValues().addAll(gather.eval(es));
			i.remove(0);
		}
		else {
			res.gatheredSensorsValues().addAll(gather.eval(es));
			i.remove(0);
			es.
			this.eval(es);
		}
		return res;
	}
	
}
