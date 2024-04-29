package app.Interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;

public interface URISensorinCI extends OfferedCI, RequestingCI, SensorNodeP2PCI{	
	
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception ;

	public void ask4Connection(NodeInfoI newNeighbour) throws Exception ;

	public QueryResultI execute(RequestContinuationI request) throws Exception ;

	public void executeAsync(RequestContinuationI requestContinuation) throws Exception ;
	
	public QueryResultI execute(RequestI request) throws Exception;

	public void executeAsync(RequestI request) throws Exception ;
	
}
//-----------------------------------------------------------------------------
