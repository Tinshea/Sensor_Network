package app.Interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;

public interface URISensorinCI extends OfferedCI {	
	
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception ;

	public void ask4Connection(NodeInfoI newNeighbour) throws Exception ;

	public QueryResultI execute(RequestContinuationI request) throws Exception ;

	public void executeAsync(RequestContinuationI requestContinuation) throws Exception ;
	
}
//-----------------------------------------------------------------------------
