package AST;

import java.util.ArrayList;

import Enums.Dir;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;

public class Rdirs {

	private Direction direction;
	private ArrayList<Direction> directions;
	
	public Rdirs(Direction direction, ArrayList<Direction> directions) {
		this.direction = direction;
		this.directions = directions;
	}
	
	public Direction getDirection() {
		return this.direction;
	}
	public ArrayList<Direction> getDirections(){
		return this.directions;
	}
}
