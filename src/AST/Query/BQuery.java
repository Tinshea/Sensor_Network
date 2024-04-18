package AST.Query;

import java.io.Serializable;
import java.util.ArrayList;

import AST.Cont.ICont;
import AST.BEXP.Ibexp;
import app.Models.QueryResult;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

public class BQuery implements QueryI, Serializable {
	private static final long serialVersionUID = 20L;
	private Ibexp bexp;
	private ICont cont;


	public BQuery(Ibexp bexp,ICont cont){
		this.bexp = bexp;
		this.cont = cont;
	}
	public QueryResultI eval(ExecutionStateI es) {
		ArrayList<String> nodes = new ArrayList<String>();
		boolean bool = bexp.eval(es);
		if(bool) {
			nodes.add(es.getProcessingNode().getNodeIdentifier());
		}
		this.cont.eval(es);
		QueryResult res = new QueryResult(new ArrayList<>(),nodes);
		es.addToCurrentResult(res);
		((QueryResult) es.getCurrentResult()).setBoolean();
		return es.getCurrentResult();
	}

}