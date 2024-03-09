package app.Interfaces;

import java.util.Set;


import fr.sorbonne_u.components.interfaces.OfferedCI;

import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

public interface RegisterCI extends OfferedCI, LookupCI, RegistrationCI {


	public boolean registered(String nodeIdentifier) throws Exception ;
	
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception ;
	
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception ;
	
	public void unregister(String nodeIdentifier) throws Exception ;
	
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception ;

	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception ;
}
//-----------------------------------------------------------------------------
