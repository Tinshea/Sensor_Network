package app.Ports;

import java.time.Instant;
import java.util.Set;

import app.Interfaces.URISensoroutCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;


public class URISensorOutBoundPort extends AbstractOutboundPort implements URISensoroutCI
{
	private static final long serialVersionUID = 1L;
	
	public	URISensorOutBoundPort(String uri,ComponentI owner) throws Exception {
		super(uri, URISensoroutCI.class, owner) ;
		assert	uri != null && owner != null ;
	}

	public	URISensorOutBoundPort(ComponentI owner) throws Exception {
		super(URISensoroutCI.class, owner) ;

	}

	@Override
	public boolean registered(String nodeIdentifier) throws Exception {
		return ((RegistrationCI)this.getConnector()).registered(nodeIdentifier) ;
	}

	@Override
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		return ((RegistrationCI)this.getConnector()).register(nodeInfo) ;
	}

	@Override
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
		return ((RegistrationCI)this.getConnector()).findNewNeighbour(nodeInfo, d) ;
	}

	@Override
	public void unregister(String nodeIdentifier) throws Exception {
		((RegistrationCI)this.getConnector()).unregister(nodeIdentifier) ;		
	}

	@Override
	public void ask4Connection(NodeInfoI newNeighbour) throws Exception {
		((SensorNodeP2PCI)this.getConnector()).ask4Connection(newNeighbour) ;		
	}

	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		((SensorNodeP2PCI)this.getConnector()).ask4Disconnection(neighbour) ;
		
	}

	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		return ((SensorNodeP2PCI)this.getConnector()).execute(request) ;
	}

	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		((SensorNodeP2PCI)this.getConnector()).executeAsync(requestContinuation) ;
	}
	
	@Override
	public AcceleratedClock createClock(String clockURI, long unixEpochStartTimeInNanos, Instant startInstant,
			double accelerationFactor) throws Exception {
		return ((ClocksServerCI)this.getConnector()).createClock(clockURI, unixEpochStartTimeInNanos, startInstant, accelerationFactor);
	}

	@Override
	public AcceleratedClock getClock(String clockURI) throws Exception {
		return ((ClocksServerCI)this.getConnector()).getClock(clockURI);
	}
	
}