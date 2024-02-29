package app.Components;

import AST.ECont;
import AST.FGather;
import AST.GQuery;
import app.Interfaces.URIClientCI;
import app.Ports.URIClientOutBoundPort;
import app.connectors.ConnectorRegistre;
import app.connectors.ConnectorSensor;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;


@RequiredInterfaces(required = {RequestingCI.class, LookupCI.class, URIClientCI.class})
public class URIClient extends AbstractComponent {
	
	// ------------------------------------------------------------------------
	// Constructors and instance variables
	// ------------------------------------------------------------------------

	/**	the outbound port usedy to call the service.							*/
	protected URIClientOutBoundPort	uriGetterPort ; //todo
	protected URIClientOutBoundPort	urioutPortnode ; //todo
	private final String inboundPortRegister;
	/**	counting service invocations.										*/

	/**
	 * @param uri				URI of the component
	 * @param outboundPortURI	URI of the URI getter outbound port.
	 * @throws Exception		<i>todo.</i>
	 */
	protected URIClient(String inboundPortRegister) throws Exception {
		super("Client", 0, 1) ;
		this.uriGetterPort = new URIClientOutBoundPort(this) ;
		this.uriGetterPort.publishPort() ;
		
		this.urioutPortnode = new URIClientOutBoundPort(this) ;
		this.urioutPortnode.publishPort() ;
		
		this.inboundPortRegister  = inboundPortRegister;

		AbstractComponent.checkImplementationInvariant(this);
		AbstractComponent.checkInvariant(this);
	}

	//-------------------------------------------------------------------------
	// Component internal services
	//-------------------------------------------------------------------------
	
		public void executeAndPrintNode() throws Exception {
	
			MyRequest clientRequest = new MyRequest((QueryI) new GQuery (new FGather("temperature"), new ECont()));
		    QueryResultI queryR = this.uriGetterPort.execute((RequestI) clientRequest);
		    System.out.println(queryR);
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
		
		
//		Le composant client se connecte au composant registre via l’interface de composant LookupCI
//		(figure 5).
		
		// do the connection with the register
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.doPortConnection(
							this.uriGetterPort.getPortURI(),
							inboundPortRegister,
							ConnectorRegistre.class.getCanonicalName()) ;
			this.logMessage("Connected to Register");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
//		Le client appelle le registre soit par la méthode findByIdentifier, auqeul cas il va récupé-
//		rer les informations de connexion à ce nœud, ou encore par la méthode findByZone, auquel
//		cas il va récupérer un ensemble d’informations de connexion aux nœuds dans cette zone,
//		parmi lesquels il va en sélectionner un
		
		super.start() ;
	}

	@Override
	public void execute() throws Exception {
		this.logMessage("executing client component.") ;
		ConnectionInfoI node = null;
		try {
			this.logMessage("requesting node ");
			node = this.uriGetterPort.findByIdentifier("n1") ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
						
								
//		Avec les informations de connexion reçues du registre, le client se connecte au composant
//		nœud via l’interface de composants RequestingCI (figure 6) qui lui permettra d’envoyer sa
//		requête.
		
		if(node != null) {
		BCM4JavaEndPointDescriptorI EndPointDescriptorNode = (BCM4JavaEndPointDescriptorI) node.endPointInfo();
		
		
		String inboundPortSensor = EndPointDescriptorNode.getInboundPortURI();
		this.logMessage("found node : "+node.nodeIdentifier() +"\nUri :"+ inboundPortSensor);
		// do the connection
		try {
			this.doPortConnection(
					this.urioutPortnode.getPortURI(),
					inboundPortSensor,
					ConnectorSensor.class.getCanonicalName()) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		MyRequest clientRequest = new MyRequest((QueryI) new GQuery (new FGather("temperature"), new ECont()));
		this.urioutPortnode.execute(clientRequest);
		}
		else {
			this.logMessage("No node found.") ;
		}
		
	
//		this.runTask(
//			new AbstractComponent.AbstractTask() {
//				@Override
//				public void run() {
//					try {
//						((URIClient)this.()).executeAndPrintNode() ;
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}) ;
	}	

	@Override
	public void finalise() throws Exception {
		this.logMessage("stopping client component.") ;
		this.printExecutionLogOnFile("client");
		this.doPortDisconnection(this.uriGetterPort.getPortURI());
		this.doPortDisconnection(this.urioutPortnode.getPortURI());
		super.finalise();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			this.uriGetterPort.unpublishPort() ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.urioutPortnode.unpublishPort() ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.shutdown();
	}
	
	@Override
	public void shutdownNow() throws ComponentShutdownException {
		try {
			this.uriGetterPort.unpublishPort() ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.urioutPortnode.unpublishPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.shutdownNow();
	}
}
//-----------------------------------------------------------------------------
