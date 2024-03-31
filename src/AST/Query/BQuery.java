package AST.Query;

import java.util.ArrayList;

import AST.Cont.ICont;
import AST.BEXP.Ibexp;
import app.Models.QueryResult;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

public class BQuery implements QueryI{
	private Ibexp bexp;
	private ICont cont;


	public BQuery(Ibexp bexp,ICont cont){
		this.bexp = bexp;
		this.cont = cont;
	}
	public QueryResultI eval(ExecutionStateI es) {
		// get the current node
		ProcessingNodeI node = es.getProcessingNode();
		// init un arraylist pour les positive nodes
		ArrayList<String> nodes = new ArrayList<String>();
		// eval ton expression
		boolean bool = bexp.eval(es);

			nodes.add(es.getProcessingNode().getNodeIdentifier());

		// si ça c'est true
		// tu ajoutes l'identifiant du node courant dans nodes
		// tu init un QueryResult
		//QueryResult res = new QueryResult(isBR = true, isGR = false, tes nodes positives ici, null);
		this.cont.eval(es);
		QueryResult res = new QueryResult(new ArrayList<>(),nodes);
		((QueryResult) res).setBoolean();
		es.addToCurrentResult(res);
		// tu retournes tous tes résultats jusqu'à ici
		return es.getCurrentResult();
	}

}