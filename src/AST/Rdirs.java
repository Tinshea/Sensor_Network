package AST;

import java.util.ArrayList;

import Enums.Dir;

public class Rdirs {

	private Dir direction;
	private ArrayList<Dir> directions;
	
	public Rdirs(Dir direction, ArrayList<Dir> directions) {
		this.direction = direction;
		this.directions = directions;
	}
	
	public Dir getDirection() {
		return this.direction;
	}
	public ArrayList<Dir> getDirections(){
		return this.directions;
	}
}
