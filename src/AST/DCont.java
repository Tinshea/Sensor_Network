package AST;

import java.util.ArrayList;

import Enums.Dir;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class DCont {

	
	private ArrayList<Direction> directions;
	private int maxSauts;
	
	public DCont(ArrayList<Direction> directions,int maxSauts) {
		this.directions = directions;
		this.maxSauts = maxSauts;
	}
	
	public ArrayList<Direction> getDirections() {
		return this.directions;
	}
	public int getMaxSauts() {
		return this.maxSauts;
	}
	
	public ArrayList<SensorDataI> eval(ExecutionStateI es){
		ArrayList<SensorDataI> res = new ArrayList<>();
		// mettre directions et nombre de sauts
		int k = maxSauts;
		if(es.noMoreHops() || k==0) {
			return res;
		}
		for(int i = 0;i<k;i++) {
			es.incrementHops();
		}
		ArrayList<NodeInfoI> voisins = (ArrayList<NodeInfoI>) es.getProcessingNode().getNeighbours();
		NodeInfoI tmp = voisins.get(0);
		switch(directions.get(0)) {
			case NE:
				 tmp = voisins.get(0);
				break;
			case NW:
				 tmp =voisins.get(1);
				break;
			case SE:
				 tmp =voisins.get(2);
				break;
			case SW:
				 tmp =voisins.get(3);
				break;
		}
		try {
			es.getProcessingNode().propagateRequest(tmp.nodeIdentifier(), null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ProcessingNodeI pn = es.getProcessingNode();
		return res;
	}
		
}

