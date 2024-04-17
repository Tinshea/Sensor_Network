package AST.Base;

import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import java.io.Serializable;

/**
 * Class ABase - represents a base node in an abstract syntax tree (AST).
 *
 * This class implements serializable and can be used within sensor network
 * simulations to represent base node with a position.
 */
public class ABase implements IBase, Serializable {

	private static final long serialVersionUID = 1L;

	private PositionI position;

	/**
	 * Constructs an ABase instance with a given position.
	 *
	 * @param position the position of this base node in a sensor network.
	 */
	public ABase(PositionI position) {
		this.position = position;
	}

	/**
	 * Evaluates the current state of the node.

	 * @param es the execution state against which the node is evaluated.
	 * @return the position of the node.
	 */
	public PositionI eval(ExecutionStateI es) {
		return this.position;
	}



}
