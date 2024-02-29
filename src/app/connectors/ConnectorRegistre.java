package app.connectors;

import java.util.Set;

import app.Interfaces.RegisterCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

public class ConnectorRegistre extends AbstractConnector implements	RegisterCI
{

	@Override
	public boolean registered(String nodeIdentifier) throws Exception {

		return ((RegisterCI)this.offering).registered(nodeIdentifier);
	}

	@Override
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {

		return ((RegisterCI)this.offering).register(nodeInfo);
	}

	@Override
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {

		return ((RegisterCI)this.offering).findNewNeighbour(nodeInfo, d);
	}

	@Override
	public void unregister(String nodeIdentifier) throws Exception {
		((RegisterCI)this.offering).unregister(nodeIdentifier);
	}

	@Override
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		return ((RegisterCI)this.offering).findByIdentifier(sensorNodeId);
	}

	@Override
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
		
		return ((RegisterCI)this.offering).findByZone(z);
	}
}
