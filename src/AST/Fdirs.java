package AST;

import java.util.ArrayList;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;

public class Fdirs {
	private Direction direction;
	
	public Fdirs(Direction direction) {
		this.direction = direction;
	}
	
	public ArrayList<Direction> eval(){
		ArrayList<Direction> res =  new ArrayList<Direction>();
		res.add(this.direction);
		return res;
	}
}
