package app.Interfaces;

import java.util.Set;

import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;


public interface URISensoroutCI extends RequiredCI {	
	
	public boolean registered(String nodeIdentifier) throws Exception ;

	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception ;

	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception ;

	public void unregister(String nodeIdentifier) throws Exception ;

	public void ask4Connection(NodeInfoI newNeighbour) throws Exception ;
	
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception ;
	
	public QueryResultI execute(RequestContinuationI request) throws Exception ;

	public void executeAsync(RequestContinuationI requestContinuation) throws Exception ;
}
	
//-----------------------------------------------------------------------------
