package AST;

import java.util.Set;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class MyExecutionState implements ExecutionStateI {

	@Override
	public ProcessingNodeI getProcessingNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateProcessingNode(ProcessingNodeI pn) {
		// TODO Auto-generated method stub

	}

	@Override
	public QueryResultI getCurrentResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addToCurrentResult(QueryResultI result) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDirectional() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<Direction> getDirections() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean noMoreHops() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void incrementHops() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isFlooding() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean withinMaximalDistance(PositionI p) {
		// TODO Auto-generated method stub
		return false;
	}

}
