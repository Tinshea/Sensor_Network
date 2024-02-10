package AST;

import java.util.ArrayList;

import Enums.Dir;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;

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
		
}

