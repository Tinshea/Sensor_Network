package app.Components;

import AST.ECont;
import AST.FGather;
import AST.GQuery;
import app.Ports.URIClientOutBoundPort;
import app.connectors.Connector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;


@RequiredInterfaces(required = {RequestingCI.class, LookupCI.class})
public class URIClient extends AbstractComponent {
	// ------------------------------------------------------------------------
	// Constructors and instance variables
	// ------------------------------------------------------------------------

	/** number of URIs that will be required by the consumer when calling
	 *  for several URIs at the same time.									*/
	protected final static int	N = 1 ;

	/**	the outbound port used to call the service.							*/
	protected URIClientOutBoundPort	uriGetterPort ; //todo
	/**	counting service invocations.										*/

	/**
	 * @param uri				URI of the component
	 * @param outboundPortURI	URI of the URI getter outbound port.
	 * @throws Exception		<i>todo.</i>
	 */
	protected URIClient(String uri, String outboundPortURI) throws Exception {
		super(uri, 0, 1) ;
		this.uriGetterPort = new URIClientOutBoundPort(this) ; //todo
		this.uriGetterPort.publishPort() ;

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
		
//		Le client appelle le registre soit par la méthode findByIdentifier, auqeul cas il va récupé-
//		rer les informations de connexion à ce nœud, ou encore par la méthode findByZone, auquel
//		cas il va récupérer un ensemble d’informations de connexion aux nœuds dans cette zone,
//		parmi lesquels il va en sélectionner un
//		
//		Avec les informations de connexion reçues du registre, le client se connecte au composant
//		nœud via l’interface de composants RequestingCI (figure 6) qui lui permettra d’envoyer sa
//		requête.
		
		// do the connection
		try {
			this.doPortConnection(
					this.uriGetterPort.getPortURI(),
					"mon-URI",
					Connector.class.getCanonicalName()) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.start() ;
	}

	@Override
	public void execute() throws Exception {
		this.logMessage("executing client component.") ;
		this.runTask(
			new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						((URIClient)this.getTaskOwner()).executeAndPrintNode() ;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}) ;
	}

	@Override
	public void finalise() throws Exception {
		this.logMessage("stopping client component.") ;
		this.printExecutionLogOnFile("client");
		this.doPortDisconnection(this.uriGetterPort.getPortURI());
		this.uriGetterPort.unpublishPort() ;
		super.finalise();
	}
	
	// TODO : pas de shutdown ?
}
//-----------------------------------------------------------------------------
