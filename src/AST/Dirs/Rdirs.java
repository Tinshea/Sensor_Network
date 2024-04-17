package AST.Dirs;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;

public class Rdirs implements IDirs , Serializable {
	private static final long serialVersionUID = 17L;

	private Direction direction;

	private  IDirs dirs;
	public Rdirs(Direction direction, IDirs directions) {
		this.direction = direction;
		this.dirs = directions;
	}
	public Set<Direction> eval()
	{
		Set<Direction> res = new HashSet<>();
		res.add(direction);
		res.addAll(dirs.eval());

		return res;
	}
}
