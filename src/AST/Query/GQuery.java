package AST.Query;

import java.io.Serializable;
import java.util.ArrayList;

import AST.Cont.ICont;
import AST.Gather.IGather;
import app.Models.QueryResult;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

public class GQuery implements QueryI, Serializable {
	private static final long serialVersionUID = 21L;
	private IGather gather;
	private ICont cont;

	public GQuery(IGather gather, ICont cont) {
		this.gather = gather;
		this.cont = cont;
	}
	public QueryResultI eval(ExecutionStateI es) {
		((QueryResult) es.getCurrentResult()).setGather() ;
		ArrayList<SensorDataI> nodes = (ArrayList<SensorDataI>) gather.eval(es);
		this.cont.eval(es);
		QueryResultI res = new QueryResult(nodes, new ArrayList<>());	
		es.addToCurrentResult(res);
		return es.getCurrentResult();
	}
}
