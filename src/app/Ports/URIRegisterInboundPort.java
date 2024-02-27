package app.Ports;

import java.util.Set;

import app.Components.Register;
import app.Components.URISensor;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

public class URIRegisterInboundPort extends AbstractInboundPort implements LookupCI, RegistrationCI
{
	private static final long serialVersionUID = 1L;

	public URIRegisterInboundPort( String uri, ComponentI owner) throws Exception
	{
		// the implemented interface is statically known
		super(uri, RegistrationCI.class, owner) ;

		assert	uri != null && owner instanceof Register;
	}

	public URIRegisterInboundPort(ComponentI owner) throws Exception
	{
		// the implemented interface is statically known
		super(RegistrationCI.class, owner) ;
//		assert	owner instanceof RequestingCI ;
	}

	@Override
	public boolean registered(String nodeIdentifier) throws Exception {
		// TODO Auto-generated method stub
		return this.getOwner().handleRequest(owner -> ((Register)owner).registered(nodeIdentifier)) ;
	}

	@Override
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		// TODO Auto-generated method stub
		return this.getOwner().handleRequest(owner -> ((Register)owner).register(nodeInfo)) ;
	}

	@Override
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
		// TODO Auto-generated method stub
		return this.getOwner().handleRequest(owner -> ((Register)owner).findNewNeighbour(nodeInfo, d)) ;
	}

	@Override
	public void unregister(String nodeIdentifier) throws Exception {
		this.getOwner().handleRequest(owner -> ((Register)owner).unregister(nodeIdentifier)) ;
	}

	@Override
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		// TODO Auto-generated method stub
		return this.getOwner().handleRequest(owner -> ((Register)owner).findByIdentifier(sensorNodeId)) ;
;
	}

	@Override
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
		// TODO Auto-generated method stub
		return this.getOwner().handleRequest(owner -> ((Register)owner).findByZone(z)) ;
	}
}
//-----------------------------------------------------------------------------
