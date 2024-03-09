package app.Components;

import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

public class RequestContinuation extends Request implements RequestContinuationI {

	private static final long serialVersionUID = 1643394860402250861L;
	private ExecutionStateI es;

	public RequestContinuation(QueryI queryCode, ConnectionInfoI clientConnectionInfo, ExecutionStateI es) {
		super(queryCode, clientConnectionInfo);
		this.es = es;

	}
	
	@Override
	public ExecutionStateI getExecutionState() {
		// TODO Auto-generated method stub
		return this.es;
	}
}
