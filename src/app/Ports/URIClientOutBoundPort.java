package app.Ports;

import java.time.Instant;
import java.util.Set;

import app.Interfaces.URIClientCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;


public class URIClientOutBoundPort extends AbstractOutboundPort implements	URIClientCI
{
	private static final long serialVersionUID = 1L;
	
	public	URIClientOutBoundPort(String uri,ComponentI owner) throws Exception
	{
		super(uri, URIClientCI.class, owner) ;
		assert	uri != null && owner != null ;
	}

	public	URIClientOutBoundPort(ComponentI owner) throws Exception
	{
		super(URIClientCI.class, owner) ;

//		assert	owner instanceof RequestingCI ;
	}

	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		return ((RequestingCI )this.getConnector()).execute(request) ;
	}

	@Override
	public void executeAsync(RequestI request) throws Exception {
		
	}

	@Override
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		return ((LookupCI)this.getConnector()).findByIdentifier(sensorNodeId) ;
	}

	@Override
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
		return ((LookupCI)this.getConnector()).findByZone(z) ;
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
