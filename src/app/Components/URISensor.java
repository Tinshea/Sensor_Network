package app.Components;
import app.Interfaces.URISensorCI;
import app.Ports.URISensorInboundPort;
import app.Components.URISensor;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.examples.cps.interfaces.ports.ValueProvidingInboundPort;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>URISensor.java</code> implements a component that provides
 * URI creation services.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 
 * </pre>
 * 
 * <p>Created on : 2024-02-17</p>
 * 
 * @author	<a>Malek Bouzarkouna, Younes Chetouani & Mohamed Amine Zemali </a>
 */
@OfferedInterfaces(offered = {RequestingCI.class})
public class URISensor extends AbstractComponent
{
	// todo : pour la continuation : implements SensorNodeP2PImplI
	protected final URISensorInboundPort inboundPort ;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * create a value provider with the given URI for its value providing
	 * inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	valueProvidingInboundPortURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param valueProvidingInboundPortURI	URI to be used to create the inbound port.
	 * @throws Exception						<i>todo.</i>
	 */
	protected URISensor(String valueProvidingInboundPortURI) throws Exception
	{
		// only one thread to ensure the serialised execution of services
		// inside the component.
		super(1, 0) ;
		assert	valueProvidingInboundPortURI != null ;

		this.inboundPort = new URISensorInboundPort("mon-URI" ,this) ;
		this.inboundPort.localPublishPort() ;

		this.getTracer().setTitle("RandomValueProvider") ;
		this.getTracer().setRelativePosition(1, 1) ;

		AbstractComponent.checkImplementationInvariant(this);
		AbstractComponent.checkInvariant(this);
	}

	/**
	 * create a value provider with the given URI for its value providing
	 * inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	valueProvidingInboundPortURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI		URI of the component reflection inbound port.
	 * @param valueProvidingInboundPortURI	URI to be used to create the inbound port.
	 * @throws Exception						<i>todo.</i>
	 */
	
	// POUR LE MOMENT PAS UTILE
	protected	URISensor(
		String reflectionInboundPortURI,
		String valueProvidingInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);

		this.inboundPort = new URISensorInboundPort("mon-URI", this) ;
		this.inboundPort.publishPort();

		this.getTracer().setTitle("RandomValueProvider") ;
		this.getTracer().setRelativePosition(1, 1) ;
	}

	// ------------------------------------------------------------------------
	// Component life-cycle
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void shutdown() throws ComponentShutdownException
	{
		// the shutdown is a good place to unpublish inbound ports.
		try {
			this.inboundPort.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		};
		super.shutdown();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdownNow()
	 */
	@Override
	public void shutdownNow() throws ComponentShutdownException
	{
		// the shutdown is a good place to unpublish inbound ports.
		try {
			this.inboundPort.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		};
		super.shutdownNow();
	}

	//--------------------------------------------------------------------------
	// Component internal services
	//--------------------------------------------------------------------------
	
	public QueryResultI execute(RequestI request) throws Exception{
		ExecutionStateI es = new ExecutionState();
		return null; /*request.eval(es); */
	}
	// -----------------------------------------------------------------------------

	
}