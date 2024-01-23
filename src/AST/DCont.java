package AST;

import java.util.ArrayList;

import Enums.Dir;

public class DCont {

	
	private ArrayList<Dir> directions;
	private int maxSauts;
	
	public DCont(ArrayList<Dir> directions,int maxSauts) {
		this.directions = directions;
		this.maxSauts = maxSauts;
	}
	
	public ArrayList<Dir> getDirections() {
		return this.directions;
	}
	public int getMaxSauts() {
		return this.maxSauts;
	}
}
