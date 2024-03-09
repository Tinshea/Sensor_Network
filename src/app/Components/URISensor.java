package app.Components;

import app.Ports.URISensorInboundPort;
import app.Ports.URISensorOutBoundPort;
import app.connectors.ConnectorRegistre;
import app.connectors.ConnectorSensor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import AST.BQuery;
import AST.GQuery;
import app.Components.URISensor;
import app.Interfaces.URISensorinCI;
import app.Interfaces.URISensoroutCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PImplI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

// -----------------------------------------------------------------------------
/**
 * The class <code>URISensor.java</code> implements a component that provides
 * URI creation services.
 *
 * <p><strong>Description</strong></p>
 * 
 * @author	<a>Malek Bouzarkouna, Younes Chetouani & Mohamed Amine Zemali </a>
 */

@OfferedInterfaces(offered = {RequestingCI.class, SensorNodeP2PCI.class, URISensorinCI.class})
@RequiredInterfaces(required = {SensorNodeP2PCI.class, RegistrationCI.class, URISensoroutCI.class})
public class URISensor extends AbstractComponent implements SensorNodeP2PImplI {
	
	protected final URISensorInboundPort inboundPort ;
	protected final String URI;
	
	protected URISensorOutBoundPort	outboundPort ;
	
	protected URISensorOutBoundPort	outboundPortNE ;
	protected URISensorOutBoundPort	outboundPortNW ;
	protected URISensorOutBoundPort	outboundPortSE ;
	protected URISensorOutBoundPort	outboundPortSW ;
	private Set<NodeInfoI> neighbor;
	protected NodeInfoI descriptor;
	private final String inboundPortRegister;
	private ExecutionState es;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	protected URISensor(NodeInfoI descriptor, String inboundPortRegister) throws Exception {
		
		super("Node : "+ descriptor.nodeIdentifier(), 1, 0) ;
		this.descriptor = descriptor;

		this.URI = ((BCM4JavaEndPointDescriptorI) descriptor.endPointInfo()).getInboundPortURI();
		this.inboundPort = new URISensorInboundPort(URI,this) ;
		this.inboundPort.localPublishPort() ;
		
		this.inboundPortRegister  = inboundPortRegister;
		
		this.outboundPort = new URISensorOutBoundPort(this) ; 
		this.outboundPort.publishPort() ;
		
		this.outboundPortNE = new URISensorOutBoundPort(this) ; 
		this.outboundPortNW = new URISensorOutBoundPort(this) ; 
		this.outboundPortSE = new URISensorOutBoundPort(this) ; 
		this.outboundPortSW = new URISensorOutBoundPort(this) ; 
		
		this.outboundPortNE.publishPort() ;
		this.outboundPortNW.publishPort() ;
		this.outboundPortSE.publishPort() ;
		this.outboundPortSW.publishPort() ;

	}

	// ------------------------------------------------------------------------
	// Component life-cycle
	// ------------------------------------------------------------------------
	
	@Override
	public void	start() throws ComponentStartException
	{
		this.logMessage("starting Node component : " + descriptor.nodeIdentifier() + "\nUri : " + URI )  ;
		// ---------------------------------------------------------------------
		// Connection phase
		// ---------------------------------------------------------------------
		
		// do the connection with the register
		try {
			this.doPortConnection(
							this.outboundPort.getPortURI(),
							inboundPortRegister,
							ConnectorRegistre.class.getCanonicalName()) ;
				} catch (Exception e) {
					e.printStackTrace();
				}
		super.start() ;
	}
	
	public void	 execute() throws Exception {
		
		this.logMessage("executing node component.") ;
		
		this.neighbor = this.outboundPort.register(descriptor) ;
		
		
		//faire un  ask4connection sur les 4 voisons ici
		
		for (NodeInfoI node : neighbor) {
			
			BCM4JavaEndPointDescriptorI EndPointDescriptorNode = (BCM4JavaEndPointDescriptorI) node.endPointInfo();
			
			String inboundPortSensor = EndPointDescriptorNode.getInboundPortURI();
			Direction d = descriptor.nodePosition().directionFrom(node.nodePosition());
			

		    try {
		        if (d.equals(Direction.NE)) {
		            this.doPortConnection(
		                this.outboundPortNE.getPortURI(),
		                inboundPortSensor,
		                ConnectorSensor.class.getCanonicalName());
		            this.logMessage("connexion uni directionnel vers un voisin au NE") ;
		            this.outboundPortNE.ask4Connection(descriptor);
		        } else if (d.equals(Direction.NW)) {
		            this.doPortConnection(
		                this.outboundPortNW.getPortURI(),
		                inboundPortSensor,
		                ConnectorSensor.class.getCanonicalName());
		            this.logMessage("connexion uni directionnel vers un voisin au NW") ;
		            this.outboundPortNW.ask4Connection(descriptor);
		        } else if (d.equals(Direction.SE)) {
		            this.doPortConnection(
		                this.outboundPortSE.getPortURI(),
		                inboundPortSensor,
		                ConnectorSensor.class.getCanonicalName());
		            this.logMessage("connexion uni directionnel vers un voisin au SE") ;
		            this.outboundPortSE.ask4Connection(descriptor);
		        } else if (d.equals(Direction.SW)) {
		            this.doPortConnection(
		                this.outboundPortSW.getPortURI(),
		                inboundPortSensor,
		                ConnectorSensor.class.getCanonicalName());
		            this.logMessage("connexion uni directionnel vers un voisin au SW") ;
		            this.outboundPortSW.ask4Connection(descriptor);
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
	}	
	@Override
	public void finalise() throws Exception {
//		Lorsque le nœud quitte le réseau de capteur, il doit d’abord se déconnecter de ses voisins puis
//		se désenregistrer auprès du registre en appelant la méthode unregister
	    this.logMessage("stopping node component.");
	    this.printExecutionLogOnFile("node");
	      
	    // Vérifie si le port est connecté avant de tenter de le déconnecter
	    if (this.outboundPort.connected()) {
	        this.doPortDisconnection(this.outboundPort.getPortURI());
	    }

	    if (this.outboundPortNE.connected()) {
	        this.doPortDisconnection(this.outboundPortNE.getPortURI()); 
	    }

	    if (this.outboundPortNW.connected()) {
	        this.doPortDisconnection(this.outboundPortNW.getPortURI());
	    }

	    if (this.outboundPortSE.connected()) {
	        this.doPortDisconnection(this.outboundPortSE.getPortURI());
	    }   

	    if (this.outboundPortSW.connected()) {
	        this.doPortDisconnection(this.outboundPortSW.getPortURI());
	    }

	    super.finalise();
	}


	@Override
	public void shutdown() throws ComponentShutdownException {
	      try {
			this.inboundPort.unpublishPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      try {
			this.outboundPort.unpublishPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      try {
			this.outboundPortNE.unpublishPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      try {
			this.outboundPortNW.unpublishPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      try {
			this.outboundPortSE.unpublishPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      try {
			this.outboundPortSW.unpublishPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.shutdown();
	}
	
	@Override
	public void shutdownNow() throws ComponentShutdownException {
		 try {
				this.inboundPort.unpublishPort();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      try {
				this.outboundPort.unpublishPort();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      try {
				this.outboundPortNE.unpublishPort();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      try {
				this.outboundPortNW.unpublishPort();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      try {
				this.outboundPortSE.unpublishPort();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      try {
				this.outboundPortSW.unpublishPort();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		super.shutdownNow();
	}

	//--------------------------------------------------------------------------
	// Component internal services
	//--------------------------------------------------------------------------
	 
	
	public QueryResultI execute(RequestI request) throws Exception{
		this.logMessage("C'est bien arriver jusque ici :"+request.toString()) ;
		// TODO
		SensorDataI sensor = new SensorData(this.descriptor.nodeIdentifier(),"Thermomètre" , 0.0);
		ProcessingNode node = new ProcessingNode(this.descriptor.nodeIdentifier(),
				this.descriptor.nodePosition(),this.neighbor,sensor);
		this.logMessage("La position est la : " + this.descriptor.nodePosition().toString()) ;
		QueryResult queryR = new QueryResult(new ArrayList<>(),false , new ArrayList<>());
		this.es = new ExecutionState(node,queryR);
		RequestContinuationI clientRequest = new RequestContinuation(request.getQueryCode(),request.clientConnectionInfo(), this.es);
//		this.runTask(
//				new AbstractComponent.AbstractTask() {
//					@Override
//					public void run() {
//						try {
//							((URISensor) this.getTaskOwner()).execute((RequestContinuationI)clientRequest) ;
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				});
		execute((RequestContinuationI)clientRequest);
		return es.getCurrentResult();
	}

	@Override
	public void ask4Connection(NodeInfoI neighbour) throws Exception {
		this.logMessage("j'essaye de me connecter a :" + neighbour.nodeIdentifier()) ;
	    Direction d = this.descriptor.nodePosition().directionFrom(neighbour.nodePosition());
	    BCM4JavaEndPointDescriptorI endPointDescriptorNode = (BCM4JavaEndPointDescriptorI) neighbour.endPointInfo();
	    
	    String inboundPortSensor = endPointDescriptorNode.getInboundPortURI();

	    try {
	        if (d.equals(Direction.NE)) {
	            if (this.outboundPortNE.connected()) {
	                this.outboundPortNE.ask4Disconnection(descriptor);
	                this.doPortDisconnection(this.outboundPortNE.getPortURI());
	            }
	            this.doPortConnection(
	                this.outboundPortNE.getPortURI(),
	                inboundPortSensor,
	                ConnectorSensor.class.getCanonicalName());
	            this.logMessage("connexion bidirectionnel vers un voisin au NE") ;

	        } else if (d.equals(Direction.NW)) {
	            if (this.outboundPortNW.connected()) {
	                this.outboundPortNW.ask4Disconnection(descriptor);
	                this.doPortDisconnection(this.outboundPortNW.getPortURI());
	            }
	            this.doPortConnection(
	                this.outboundPortNW.getPortURI(),
	                inboundPortSensor,
	                ConnectorSensor.class.getCanonicalName());
	            this.logMessage("connexion bidirectionnel vers un voisin au NW") ;

	        } else if (d.equals(Direction.SE)) {
	            if (this.outboundPortSE.connected()) {
	                this.outboundPortSE.ask4Disconnection(descriptor);
	                this.doPortDisconnection(this.outboundPortSE.getPortURI());
	            }
	            this.doPortConnection(
	                this.outboundPortSE.getPortURI(),
	                inboundPortSensor,
	                ConnectorSensor.class.getCanonicalName());
	            this.logMessage("connexion bidirectionnel vers un voisin au SE") ;

	        } else if (d.equals(Direction.SW)) {
	            if (this.outboundPortSW.connected()) {
	                this.outboundPortSW.ask4Disconnection(descriptor);
	                this.doPortDisconnection(this.outboundPortSW.getPortURI());
	            }
	            this.doPortConnection(
	                this.outboundPortSW.getPortURI(),
	                inboundPortSensor,
	                ConnectorSensor.class.getCanonicalName());
	            this.logMessage("connexion bidirectionnel vers un voisin au SW") ;

	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	
	private void handleDisconnectionAndReconnection(URISensorOutBoundPort outboundPort, Direction d) throws Exception {
	    if(outboundPort.connected()) {
	        this.doPortDisconnection(outboundPort.getPortURI());
	    }
	    NodeInfoI node = outboundPort.findNewNeighbour(this.descriptor, d);
	    if (node != null) {
	        BCM4JavaEndPointDescriptorI endPointDescriptorNode = (BCM4JavaEndPointDescriptorI) node.endPointInfo();
	        String inboundPortSensor = endPointDescriptorNode.getInboundPortURI();
	        this.doPortConnection(
	            outboundPort.getPortURI(),
	            inboundPortSensor,
	            ConnectorSensor.class.getCanonicalName());
	        outboundPort.ask4Connection(node);
	    }
	}

	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
	    Direction d = this.descriptor.nodePosition().directionFrom(neighbour.nodePosition());
	    
	    try {
	        if (d.equals(Direction.NE)) {
	            handleDisconnectionAndReconnection(this.outboundPortNE, d);
	        } else if (d.equals(Direction.NW)) {
	            handleDisconnectionAndReconnection(this.outboundPortNW, d);
	        } else if (d.equals(Direction.SE)) {
	            handleDisconnectionAndReconnection(this.outboundPortSE, d);
	        } else if (d.equals(Direction.SW)) {
	            handleDisconnectionAndReconnection(this.outboundPortSW, d);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }       
	}


	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		this.logMessage("zizi");
		this.logMessage(request.getQueryCode().toString());
		ExecutionStateI es = request.getExecutionState();
		if(request.getQueryCode() instanceof GQuery) {
			((GQuery) request.getQueryCode()).eval(es);
		}else {
			((BQuery) request.getQueryCode()).eval(es);
		}
	
		SensorData sensor = new SensorData(this.descriptor.nodeIdentifier(),"radar" , 0.0);
		ProcessingNode node = new ProcessingNode(this.descriptor.nodeIdentifier(),
				this.descriptor.nodePosition(),this.neighbor,sensor);
		es.updateProcessingNode(node);
		if(es.isFlooding()) {
			this.logMessage("caca");
			for(NodeInfoI n : es.getProcessingNode().getNeighbours()) {
				Direction d = es.getProcessingNode().getPosition().directionFrom(n.nodePosition());
				this.logMessage(Double.toString(es.getProcessingNode().getPosition().distance(n.nodePosition())));
				if(es.withinMaximalDistance(n.nodePosition())) {
					this.logMessage("cafca");
					try {
				        if (d.equals(Direction.NE)) {
				    		this.logMessage("oco");
				    		if(outboundPortNE.connected()) {
				    			 this.outboundPortNE.execute(request);
				    		}
				        } else if (d.equals(Direction.NW)) {
				        	this.logMessage("ossco");
				            this.outboundPortNW.execute(request);
				        } else if (d.equals(Direction.SE)) {
				        	this.logMessage("os");
				            this.outboundPortSE.execute(request);
				        } else if (d.equals(Direction.SW)) {
				        	this.logMessage("Penis");
				            this.outboundPortSW.execute(request);
				        }
				    } catch (Exception e) {
				        e.printStackTrace();
				    }
				}
			}
			return es.getCurrentResult();
		}
		if(es.isDirectional()) {
			if(es.noMoreHops()) {
				return es.getCurrentResult();
			}else {
				es.incrementHops();
				ArrayList<Direction> tmp = new ArrayList<>();
				tmp.addAll(es.getDirections());
				if (tmp.isEmpty()){ return es.getCurrentResult();}
				Direction d = tmp.get(0);
				 if (d.equals(Direction.NE)) {
			    		this.logMessage("oco");
			    		if(outboundPortNE.connected()) {
			    			this.logMessage("Je suis bien connecté ! ");
			    		}
			            this.outboundPortNE.execute(request);
			        } else if (d.equals(Direction.NW)) {
			        	this.logMessage("ossco");
			            this.outboundPortNW.execute(request);
			        } else if (d.equals(Direction.SE)) {
			        	this.logMessage("os");
			            this.outboundPortSE.execute(request);
			        } else if (d.equals(Direction.SW)) {
			        	this.logMessage("Penis");
			            this.outboundPortSW.execute(request);
			        }
				 
				
				 return es.getCurrentResult();
			}
		}
		 return null; 
	}

	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		// TODO Auto-generated method stub
		
	}
	// -----------------------------------------------------------------------------
	
}