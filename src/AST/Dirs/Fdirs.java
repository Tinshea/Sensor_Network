package AST.Dirs;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;

public class Fdirs implements IDirs , Serializable {
	private static final long serialVersionUID = 16L;
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
