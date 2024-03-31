package app.Ports;

import java.util.Set;

import app.Components.Register;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

public class URIRegisterInboundPortForNode extends AbstractInboundPort implements RegistrationCI
{
	private static final long serialVersionUID = 1L;

	public URIRegisterInboundPortForNode(String uri, ComponentI owner) throws Exception {
		super(uri, RegistrationCI.class, owner) ;

		assert	uri != null && owner instanceof Register;
	}

	public URIRegisterInboundPortForNode(ComponentI owner) throws Exception
	{
		super(RegistrationCI.class, owner) ;
		assert	owner instanceof RegistrationCI ;
	}

	@Override
	public boolean registered(String nodeIdentifier) throws Exception {
		return this.getOwner().handleRequest(owner -> ((Register)owner).registered(nodeIdentifier)) ;
	}

	@Override
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		return this.getOwner().handleRequest(owner -> ((Register)owner).register(nodeInfo)) ;
	}

	@Override
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
		return this.getOwner().handleRequest(owner -> ((Register)owner).findNewNeighbour(nodeInfo, d)) ;
	}

	@Override
	public void unregister(String nodeIdentifier) throws Exception {
		this.getOwner().handleRequest(owner -> {((Register)owner).unregister(nodeIdentifier) ; return null; });
	}
}
//-----------------------------------------------------------------------------
