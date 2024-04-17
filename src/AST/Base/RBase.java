package AST.Base;

import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import java.io.Serializable;

/**
 * The class RBase represents a "runtime base" in an abstract syntax tree (AST).
 * This node is typically used to evaluate runtime properties or states.
 *
 * It implements Serializable to allow for state to be persisted or transferred
 * over the network or between components in a sensor network simulation.
 */
public class RBase implements IBase, Serializable {
	private static final long serialVersionUID = 2L;

	/**
	 * Default constructor for RBase.
	 */
	public RBase() {
		// Default constructor
	}

	/**
	 * Evaluates the runtime state of this node.
	 *
	 * This method will return the position of the processing node
	 * within the execution state provided.
	 *
	 * @param es The execution state that contains the context needed for evaluation.
	 * @return The position of the processing node.
	 */
	public PositionI eval(ExecutionStateI es) {
		return es.getProcessingNode().getPosition();
	}

}
