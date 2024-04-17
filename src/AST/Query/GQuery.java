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
		// init un arraylist pour les nodes collectés
		ArrayList<SensorDataI> nodes = (ArrayList<SensorDataI>) gather.eval(es);
		this.cont.eval(es);
		QueryResultI res = new QueryResult(nodes, new ArrayList<>());	
		((QueryResult) res).setGather();
		es.addToCurrentResult(res);
		return es.getCurrentResult();
	}
}
/* Ancienne version
	public QueryResultI eval(ExecutionStateI es) {
		//faire un setter dans QueryResult
		this.cont.eval(es);
		QueryResultI res = es.getCurrentResult();
		((QueryResult) res).setGather();
		res.gatheredSensorsValues().addAll(gather.eval(es));
		cont.eval(es);
		return es.getCurrentResult();
	}
	*/
