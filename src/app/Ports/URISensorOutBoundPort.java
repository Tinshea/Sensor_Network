package app.Ports;

import java.util.Set;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;


public class URISensorOutBoundPort extends AbstractOutboundPort implements	SensorNodeP2PCI, RegistrationCI
{
	private static final long serialVersionUID = 1L;
	
	 // On lui passe la référence au composant qui le détient
	public	URISensorOutBoundPort(String uri,ComponentI owner) throws Exception
	{
		super(uri, RequestingCI.class, owner) ;
		assert	uri != null && owner != null ;
	}

	public	URISensorOutBoundPort(ComponentI owner) throws Exception
	{
		super(RequestingCI.class, owner) ;

//		assert	owner instanceof RequestingCI ;
	}


	@Override
	public boolean registered(String nodeIdentifier) throws Exception {
		// TODO Auto-generated method stub
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
	
	
}
//-----------------------------------------------------------------------------
