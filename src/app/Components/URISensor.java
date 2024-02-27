package app.Components;

import app.Interfaces.URISensorCI;
import app.Ports.URISensorInboundPort;
import app.connectors.Connector;

import java.util.Set;

import app.Components.URISensor;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.examples.cps.interfaces.ports.ValueProvidingInboundPort;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PImplI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;
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
 * @author	<a>Malek Bouzarkouna, Younes Chetouani & Mohamed Amine Zemali </a>
 */

@OfferedInterfaces(offered = {RequestingCI.class, SensorNodeP2PCI.class})
@RequiredInterfaces(required = {SensorNodeP2PCI.class, RegistrationCI.class})
public class URISensor extends AbstractComponent implements SensorNodeP2PImplI {
	
	protected final URISensorInboundPort inboundPort ;
	protected NodeInfoI descriptor;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	protected URISensor(NodeInfoI descriptor) throws Exception {
		
		super(1, 0) ;
		this.descriptor = descriptor;
		this.inboundPort = new URISensorInboundPort("mon-URI" ,this) ;
		this.inboundPort.localPublishPort() ;

	}

	// ------------------------------------------------------------------------
	// Component life-cycle
	// ------------------------------------------------------------------------
	
	@Override
	public void	start() throws ComponentStartException
	{
		this.logMessage("starting Node component.") ;
		// ---------------------------------------------------------------------
		// Connection phase
		// ---------------------------------------------------------------------

		// do the connection
		try {
			this.doPortConnection(
					//connecter le noeud au register  via l’interface de composants RegistrationCI
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.start() ;
	}
	
	public void	 execute() throws Exception {
		
		this.logMessage("executing client component.") ;
		
		Set<NodeInfoI> neighbor =   this.handleRequest( 
					new AbstractComponent.AbstractService<Set<NodeInfoI>>() {
				public Set<NodeInfoI> call() {
				return this.register(descriptor);;
				});
		
		
		//faire un  ask4connection sur les 4 voisons ici
		
//		Si un nœud Y reçoit une demande de connexion d’un nouveau nœud X dans une direction où
//		Y avait déjà un voisin Z, Y doit d’abord se déconnecter de Z. Pour cela, Y appelle d’abord la
//		méthode ask4Disconnection sur Z pour que celui-ci se déconnecte de lui, puis Y se déconnecte
//		effectivement de Z avant de se connecter avec X. Lorsqu’un nœud se retrouve sans voisin dans
//		une certaine direction, il appelle le registre avec la méthode findNewNeighbour en fournissant en
//		paramètre ses informations de nœud et la direction dans laquelle il n’a plus de voisin ; le registre
//		retourne les informations de connexion d’un nouveau voisin ou null s’il n’existe plus de voisin
//		possible dans cette direction.
//		this.runTask(
//			new AbstractComponent.AbstractTask() {
//				@Override
//				public void run() {
//					try {
//						((URIClient)this.getTaskOwner()).executeAndPrintNode() ;
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}) ;
	}	

	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
//			Lorsque le nœud quitte le réseau de capteur, il doit d’abord se déconnecter de ses voisins puis
//			se désenregistrer auprès du registre en appelant la méthode unregister.
			this.inboundPort.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		};
		super.shutdown();
	}
	
	@Override
	public void shutdownNow() throws ComponentShutdownException {
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
		 QueryResultI result = request.eval(es);
		return result ;
	}

	@Override
	public void ask4Connection(NodeInfoI neighbour) throws Exception {
		//ici on se connecte et on check les voisins etc je pense
		
	}

	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		// TODO Auto-generated method stub
		
	}
	// -----------------------------------------------------------------------------
	
}