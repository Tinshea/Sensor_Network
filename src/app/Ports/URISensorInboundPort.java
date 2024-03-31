package app.Ports;

import app.Components.Sensor;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;


public class URISensorInboundPort extends AbstractInboundPort implements SensorNodeP2PCI
{
	private static final long serialVersionUID = 1L;

	public URISensorInboundPort( String uri, ComponentI owner) throws Exception{
		super(uri, SensorNodeP2PCI.class, owner) ;

		assert	uri != null && owner instanceof Sensor;
	}

	public URISensorInboundPort(ComponentI owner) throws Exception {
		super(SensorNodeP2PCI.class, owner) ;
		assert	owner instanceof SensorNodeP2PCI ;
	}

	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		this.owner.runTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((Sensor)this.getTaskOwner()).ask4Disconnection(neighbour) ;
						} catch (Exception e) {
							e.printStackTrace(); ;
						}
					}
				}) ;
	}

	@Override
	public void ask4Connection(NodeInfoI newNeighbour) throws Exception {
		this.owner.runTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((Sensor)this.getTaskOwner()).ask4Connection(newNeighbour) ;
						} catch (Exception e) {
							e.printStackTrace(); ;
						}
					}
				}) ;
		
	}

	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		return this.getOwner().handleRequest(owner -> ((Sensor)owner).execute(request)) ;
	}

	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		
	}

}
