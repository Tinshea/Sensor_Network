package app.Ports;

import app.Components.URISensor;
import app.Interfaces.URISensorinCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;


public class URISensorInboundPort extends AbstractInboundPort implements URISensorinCI
{
	private static final long serialVersionUID = 1L;

	public URISensorInboundPort( String uri, ComponentI owner) throws Exception
	{
		// the implemented interface is statically known
		super(uri, URISensorinCI.class, owner) ;

		assert	uri != null && owner instanceof URISensor;
	}

	public URISensorInboundPort(ComponentI owner) throws Exception
	{
		// the implemented interface is statically known
		super(URISensorinCI.class, owner) ;
//		assert	owner instanceof RequestingCI ;
	}

	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		this.owner.runTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((URISensor)this.getTaskOwner()).ask4Disconnection(neighbour) ;
						} catch (Exception e) {
							e.printStackTrace(); ;
						}
					}
				}) ;
	}

	@Override
	public void ask4Connection(NodeInfoI newNeighbour) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		// TODO Auto-generated method stub
		return this.getOwner().handleRequest(owner -> ((URISensor)owner).execute(request)) ;
	}

	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
//-----------------------------------------------------------------------------
