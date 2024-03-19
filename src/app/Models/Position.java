package app.Models;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;

public class Position implements PositionI {

	private static final long serialVersionUID = -4235766298940273454L;
	
	private double x;
	private double y;

	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getx() {
		return x;
	}
	public double gety() {
		return y;
	}
	@Override
	public double distance(PositionI p) {
		if (p instanceof Position) {
			Position other = (Position) p;
			return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
		}
		return 0;
	}

	@Override
	public Direction directionFrom(PositionI p) {
	    if (p instanceof Position) {
	        Position other = (Position) p;
	        boolean north = this.northOf(other);
	        boolean south = this.southOf(other);
	        boolean east = this.eastOf(other);
	        boolean west = this.westOf(other);

	        if (north && east) return Direction.NE;
	        if (north && west) return Direction.NW;
	        if (south && east) return Direction.SE;
	        if (south && west) return Direction.SW;
	    }
	    return null; 
	}


	@Override
	public boolean northOf(PositionI p) {
		if (p instanceof Position) {
			return this.y < ((Position) p).y;
		}
		return false;
	}

	@Override
	public boolean southOf(PositionI p) {
		if (p instanceof Position) {
			return this.y > ((Position) p).y;
		}
		return false;
	}

	@Override
	public boolean eastOf(PositionI p) {
		if (p instanceof Position) {
			return this.x < ((Position) p).x;
		}
		return false;
	}

	@Override
	public boolean westOf(PositionI p) {
		if (p instanceof Position) {
			return this.x > ((Position) p).x;
		}
		return false;
	}
}
