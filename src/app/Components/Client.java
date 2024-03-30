package app.Components;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import AST.ABase;
import AST.DCont;
import AST.ECont;
import AST.FCont;
import AST.FGather;
import AST.GQuery;
import app.Models.ClientConfig;
import app.Models.Request;
import app.Ports.URIClientOutBoundPortToNode;
import app.Ports.URIClientOutBoundPortToRegister;
import app.connectors.ConnectorRegistreClient;
import app.connectors.ConnectorSensorToClient;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.helpers.TracerI;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;


@RequiredInterfaces(required = {RequestingCI.class, LookupCI.class, ClocksServerCI.class})
public class Client extends AbstractComponent {
	
	// ------------------------------------------------------------------------
	// Instance variables
	// ------------------------------------------------------------------------
	
	protected URIClientOutBoundPortToRegister	urioutPortregister ; 
	protected URIClientOutBoundPortToNode	urioutPortnode ; 
	private final String inboundPortRegister;
	
	private String TEST_CLOCK_URI;
	private ClocksServerOutboundPort outboundPortClock;

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------

	protected Client(ClientConfig configClient) throws Exception {
		
		super("Client " + configClient.getName(), 0, 1) ;
		
		this.TEST_CLOCK_URI = configClient.getUriClock();
		this.outboundPortClock = new ClocksServerOutboundPort(this);
		outboundPortClock.publishPort();
		
		this.urioutPortregister = new URIClientOutBoundPortToRegister(this) ;
		this.urioutPortregister.publishPort() ;
		
		this.urioutPortnode = new URIClientOutBoundPortToNode(this) ;
		this.urioutPortnode.publishPort() ;
		
		this.inboundPortRegister  = configClient.getInboundPortRegister();
		
		TracerI tracer = this.getTracer() ;
	
		tracer.setRelativePosition(configClient.getName() % 3, configClient.getName() / 3);
		AbstractComponent.checkImplementationInvariant(this);
		AbstractComponent.checkInvariant(this);
	}

	//-------------------------------------------------------------------------
	// Component life-cycle
	//-------------------------------------------------------------------------
		
	@Override
	public void start() throws ComponentStartException {
		this.logMessage("starting client component.") ;
		
		// ---------------------------------------------------------------------
		// Connection phase
		// ---------------------------------------------------------------------
		
		// do the connection with the clock
		try {
			this.doPortConnection(
					outboundPortClock.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// do the connection with the register
		try {
			this.doPortConnection(
							this.urioutPortregister.getPortURI(),
							inboundPortRegister,
							ConnectorRegistreClient.class.getCanonicalName()) ;
			this.logMessage("Connected to Register");
				} catch (Exception e) {
					e.printStackTrace();
				}
		
		super.start() ;
	}

	@Override
	public void execute() throws Exception {
		
		AcceleratedClock ac = outboundPortClock.getClock(TEST_CLOCK_URI);
		
		ac.waitUntilStart();

		Instant i0 = ac.getStartInstant();
		Instant i2 = i0.plusSeconds(120);
		
		long delay = ac.nanoDelayUntilInstant(i2);
		
		this.scheduleTask(
				o -> { try {
					this.requestNodeAndconnectByName("n9");
				} catch (Exception e) {
					e.printStackTrace();
				}
				},
				delay, TimeUnit.NANOSECONDS);
		
		Instant i3 = i2.plusSeconds(120);
		long delay2 = ac.nanoDelayUntilInstant(i3); 
		
		this.scheduleTask(
				o -> { try {
					this.executeAndPrintRequest();
				} catch (Exception e) {
					e.printStackTrace();
				}
				},
				delay2, TimeUnit.NANOSECONDS);
		}
			

	@Override
	public void finalise() throws Exception {
		this.logMessage("stopping client component.") ;
		this.printExecutionLogOnFile("client");
	    if (this.urioutPortregister.connected()) {
	    	this.doPortDisconnection(this.urioutPortregister.getPortURI());
	    }
	    if (this.urioutPortnode.connected()) {
	    	this.doPortDisconnection(this.urioutPortnode.getPortURI());
	    }
	    if (this.outboundPortClock.connected()) {
	    	this.doPortDisconnection(this.outboundPortClock.getPortURI());
	    }
		
		super.finalise();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			this.urioutPortregister.unpublishPort() ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.urioutPortnode.unpublishPort() ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			this.outboundPortClock.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}
	
	@Override
	public void shutdownNow() throws ComponentShutdownException {
		try {
			this.urioutPortregister.unpublishPort() ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.urioutPortnode.unpublishPort() ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			this.outboundPortClock.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdownNow();
	}


//-------------------------------------------------------------------------
// Component internal services
//-------------------------------------------------------------------------

	
	public void executeAndPrintRequest() throws Exception {
		Set<Direction> dirs = new HashSet<>();
		dirs.add(Direction.SE);
//		RequestI clientRequest = new Request((QueryI) new GQuery (new FGather("Heat"), new ECont()), null);
		RequestI clientRequest = new Request((QueryI) new GQuery (new FGather("Heat"), new FCont(new ABase(),2)), null);
		//RequestI clientRequest = new Request((QueryI) new GQuery (new FGather("Heat"), new DCont(dirs, 5)), null);
	    QueryResultI queryR = this.urioutPortnode.execute( (RequestI) clientRequest);
	    this.logMessage("request result : ");
	    if(queryR.isBooleanRequest()) {
	    	this.logMessage(queryR.positiveSensorNodes().toString());
	    }
	    else {
	    	this.logMessage(queryR.gatheredSensorsValues().toString());
	    }
	}
	
	public void requestNodeAndconnectByName (String noderequest) throws Exception {
		ConnectionInfoI node = null;
		try {
			this.logMessage("requesting node :"+ noderequest);
			node = this.urioutPortregister.findByIdentifier(noderequest) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
						
		if(node != null) {
		BCM4JavaEndPointDescriptorI EndPointDescriptorNode = (BCM4JavaEndPointDescriptorI) node.endPointInfo();
		
		String inboundPortSensor = EndPointDescriptorNode.getInboundPortURI();
		this.logMessage("found node : "+ node.nodeIdentifier());
		
		// do the connection
		try {
			this.doPortConnection(
					this.urioutPortnode.getPortURI(),
					inboundPortSensor,
					ConnectorSensorToClient.class.getCanonicalName()) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		}else {
			this.logMessage("No node found.") ;
		}
	}
}
	