package AST.Dirs;

import java.util.HashSet;
import java.util.Set;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;

public class Fdirs implements IDirs {
	private Direction direction;
	
	public Fdirs(Direction direction) {
		this.direction = direction;
	}
	
	public Set<Direction> eval(){
		Set<Direction> res =  new HashSet<>();
		res.add(this.direction);
		return res;
	}
}
