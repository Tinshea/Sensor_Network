package app.Ports;

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


public class URIClientOutBoundPort extends AbstractOutboundPort implements	URIClientCI
{
	private static final long serialVersionUID = 1L;
	
	 // On lui passe la référence au composant qui le détient
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		// TODO Auto-generated method stub
		return ((LookupCI)this.getConnector()).findByIdentifier(sensorNodeId) ;
	}

	@Override
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
		// TODO Auto-generated method stub
		return ((LookupCI)this.getConnector()).findByZone(z) ;
	}
	
	
}
//-----------------------------------------------------------------------------
