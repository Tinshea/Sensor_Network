package app.connectors;

import java.util.Set;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

public class ConnectorRegistre extends AbstractConnector implements	LookupCI, RegistrationCI
{

	@Override
	public boolean registered(String nodeIdentifier) throws Exception {

		return ((RegistrationCI)this.offering).registered(nodeIdentifier);
	}

	@Override
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {

		return ((RegistrationCI)this.offering).register(nodeInfo);
	}

	@Override
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {

		return ((RegistrationCI)this.offering).findNewNeighbour(nodeInfo, d);
	}

	@Override
	public void unregister(String nodeIdentifier) throws Exception {
		((RegistrationCI)this.offering).unregister(nodeIdentifier);
	}

	@Override
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		return ((LookupCI)this.offering).findByIdentifier(sensorNodeId);
	}

	@Override
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
		
		return ((LookupCI)this.offering).findByZone(z);
	}
}
