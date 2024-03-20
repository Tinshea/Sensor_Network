package AST;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import Interfaces.ICont;
import app.Models.ExecutionState;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class DCont implements ICont{

	
	private Set<Direction> directions;
	private int maxSauts;
	
	public DCont(Set<Direction> directions,int maxSauts) {
		this.directions = directions;
		this.maxSauts = maxSauts;
	}
	
	public Set<Direction> getDirections() {
		return this.directions;
	}
	public int getMaxSauts() {
		return this.maxSauts;
	}

	/*@Override
	public ArrayList<String> eval(ExecutionStateI es) {
		// TODO Auto-generated method stub
		ArrayList<String> res = new ArrayList<>();
		if(es.noMoreHops()) {
			return res;
		}
		Set<NodeInfoI> neighbors = es.getProcessingNode().getNeighbours();
		ArrayList<NodeInfoI> tmp = new ArrayList<>();
		tmp.addAll(neighbors);
		//Verifier la cond. de cont
		ArrayList<Direction> es_directions = new ArrayList<>();
		es_directions.addAll(es.getDirections());
		switch (es_directions.get(0)){
			case NE:
				res.add(tmp.get(0).nodeIdentifier());
				break;
			case NW:
				res.add(tmp.get(1).nodeIdentifier());
				break;
			case SE:
				res.add(tmp.get(2).nodeIdentifier());
				break;
			case SW:
				res.add(tmp.get(3).nodeIdentifier());
				break;
		}
		//Mettre à jour le executionState
		es.getDirections().remove(0);
		es.incrementHops();
		return res;
	}*/
	
	@Override
	public void eval(ExecutionStateI es) {
		// TODO Auto-generated method stub
		((ExecutionState) es).setDirections(this.directions);
		((ExecutionState) es).setDirectional();
		((ExecutionState) es).setMaxSauts(maxSauts);
		if(es.noMoreHops()) {
			return;
		}
		//Mettre à jour le executionState
		//faire l'incr a l'exec
		//es.incrementHops();
	}
		
}

