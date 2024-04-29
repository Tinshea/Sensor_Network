package AST;

import java.util.List;

import Interfaces.ICont;
import Interfaces.IGather;
import app.Models.QueryResult;
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
	
	/*public QueryResultI eval2(ExecutionStateI es) {
		QueryResultI res = es.getCurrentResult();
		List<ProcessingNodeI> i = cont.eval(es);
		i.add(0,es.getProcessingNode());
		if(i.size() == 1) {
			res.gatheredSensorsValues().addAll(gather.eval(es));
			i.remove(0);
		}
		else {
			res.gatheredSensorsValues().addAll(gather.eval(es));
			i.remove(0);
			es.updateProcessingNode(i.get(0));
			this.eval(es);
		}
		return res;
	}*/
	public QueryResultI eval(ExecutionStateI es) {
		//faire un setter dans QueryResult
		QueryResultI res = es.getCurrentResult();
		((QueryResult) res).setGather();
		res.gatheredSensorsValues().addAll(gather.eval(es));
		cont.eval(es);
		return es.getCurrentResult();
	}
}
