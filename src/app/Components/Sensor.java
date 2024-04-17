package app.Components;
	
	import app.Ports.URISensorInboundPort;
	import app.Ports.URISensorInboundPortClient;
	import app.Ports.URISensorOutBoundPortToNode;
	import app.Ports.URISensorOutBoundPortToRegister;
	import app.connectors.ConnectorRegistreNode;
	import app.connectors.ConnectorSensorToSensor;
	import java.time.Instant;
	import java.util.ArrayList;
	import java.util.Collections;
	import java.util.HashMap;
	import java.util.HashSet;
	import java.util.Iterator;
	import java.util.Map;
	import java.util.Set;
	import java.util.concurrent.TimeUnit;
	import AST.Query.BQuery;
	import AST.Query.GQuery;
	import app.gui.GraphicalNetworkInterface;
	import app.Components.Sensor;
	import app.Models.Bcm4javaEndPointDescriptor;
	import app.Models.Descriptor;
	import app.Models.ExecutionState;
	import app.Models.ProcessingNode;
	import app.Models.QueryResult;
	import app.Models.RequestContinuation;
	import app.Models.SensorConfig;
	import fr.sorbonne_u.components.AbstractComponent;
	import fr.sorbonne_u.components.annotations.OfferedInterfaces;
	import fr.sorbonne_u.components.annotations.RequiredInterfaces;
	import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
	import fr.sorbonne_u.components.exceptions.ComponentStartException;
	import fr.sorbonne_u.components.helpers.TracerI;
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
	import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
	import fr.sorbonne_u.utils.aclocks.ClocksServer;
	import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
	import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
	import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
	
	// -----------------------------------------------------------------------------
	/**
	 * The {@code Sensor} class represents a network sensor node within a distributed
	 * sensor network. It handles tasks such as registering with a registry, managing
	 * connections to other nodes, and processing sensor data queries.
	 * <p>
	 * This class offers interfaces to handle sensor data requests and node connections,
	 * and requires interfaces to connect to other nodes, the registration service, and
	 * the clock server. The sensor node operates within an accelerated time context
	 * provided by a clock server and interacts with other nodes in the network to
	 * perform distributed queries and data aggregation.
	 * </p>
	 *
	 * @author Malek Bouzarkouna, Younes Chetouani, Mohamed Amine Zemali
	 * @version 1.0
	 * @see fr.sorbonne_u.components.AbstractComponent
	 * @see fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PImplI
	 */
	
	@OfferedInterfaces(offered = {RequestingCI.class, SensorNodeP2PCI.class})
	@RequiredInterfaces(required = {SensorNodeP2PCI.class, RegistrationCI.class, ClocksServerCI.class})
	public class Sensor extends AbstractComponent implements SensorNodeP2PImplI {
		
		// ------------------------------------------------------------------------
		// Instance variables
		// ------------------------------------------------------------------------
	
		private final URISensorInboundPort inboundPortSensor ;
		private final URISensorInboundPortClient inboundPortClient ;
		
		private URISensorOutBoundPortToRegister	outboundPortRegistre ;
			
		private URISensorOutBoundPortToNode	outboundPortNE ;
		private URISensorOutBoundPortToNode	outboundPortNW ;
		private URISensorOutBoundPortToNode	outboundPortSE ;
		private URISensorOutBoundPortToNode	outboundPortSW ;
		private Set<NodeInfoI> neighbor;
		private Set<SensorDataI> sensors;
		private Map<URISensorOutBoundPortToNode, NodeInfoI> nodeOutboundPorts;
		private HashSet<String> processedRequests;
		private NodeInfoI descriptor;
		
		
		private String TEST_CLOCK_URI;
		private ClocksServerOutboundPort outboundPortClock;
		
		private final String inboundPortRegister;
		
		private ExecutionState es;
		
		private GraphicalNetworkInterface gui;
	
		// ------------------------------------------------------------------------
		// Constructor
		// ------------------------------------------------------------------------
	
		protected Sensor(SensorConfig config) throws Exception {
	        super("Node : " + config.getName() , 0, 5);
	        this.gui = config.getGui();
	        this.TEST_CLOCK_URI = config.getUriClock();
	        this.sensors = config.getSensors();

	        this.inboundPortSensor = new URISensorInboundPort(this);
	        this.inboundPortSensor.publishPort();
	        
	        this.inboundPortClient = new URISensorInboundPortClient(this);
	        this.inboundPortClient.publishPort();

	        BCM4JavaEndPointDescriptorI urinodeSensor = new Bcm4javaEndPointDescriptor(inboundPortSensor.getPortURI());
	        BCM4JavaEndPointDescriptorI urinodeclient = new Bcm4javaEndPointDescriptor(inboundPortClient.getPortURI());
	        this.descriptor = new Descriptor("n" + config.getName(), urinodeclient, config.getPosition(), 1.5, urinodeSensor);

	        this.inboundPortRegister = config.getInboundPortRegister();
	        initializeOutboundPorts();
	        
	        this.processedRequests = new HashSet<>();
	        
	        gui.addGraphicalNode("n" + config.getName(), config.getPosition().getx(), config.getPosition().gety());
	        // Ajustement de la position du tracer
	        TracerI tracer = this.getTracer();
	        int index = config.getName() - 1;
	        tracer.setRelativePosition(index % 3, (index / 3) + 1);
	    }
	
		// ------------------------------------------------------------------------
		// Component life-cycle
		// ------------------------------------------------------------------------
		
		@Override
		public void	start() throws ComponentStartException{
			
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
								this.outboundPortRegistre.getPortURI(),
								inboundPortRegister,
								ConnectorRegistreNode.class.getCanonicalName()) ;
					} catch (Exception e) {
						e.printStackTrace();
					}
			
			 StringBuilder logMessageBuilder = new StringBuilder();
		        logMessageBuilder.append("Sensor values").append(this.descriptor.nodeIdentifier()).append(": [");

		        for (SensorDataI sensorData : this.sensors) {
		            logMessageBuilder.append(sensorData.getSensorIdentifier())
		                             .append(": ").append(sensorData.getValue()).append(", ");
		        }
		        if (!this.sensors.isEmpty()) {
		            logMessageBuilder.setLength(logMessageBuilder.length() - 2); // Retire la dernière virgule et l'espace
		        }

		        logMessageBuilder.append("]");
		        this.logMessage(logMessageBuilder.toString());
		        
			super.start() ;
		}
		
		public void	 execute() throws Exception {
			
			this.logMessage("executing node component.") ;
			
			AcceleratedClock ac = outboundPortClock.getClock(TEST_CLOCK_URI);
			ac.waitUntilStart();
			Instant i0 = ac.getStartInstant();
			Instant i1= i0.plusSeconds(60);
			
			long delay = ac.nanoDelayUntilInstant(i1); // délai en nanosecondes
			
			this.scheduleTask(
					o -> { try {
						if(!this.outboundPortRegistre.registered(this.descriptor.nodeIdentifier()))
		                this.registerAndConnectToNeighbor();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					},
					delay, TimeUnit.NANOSECONDS);
			
		    if (this.outboundPortNE.connected()) {
		    	this.outboundPortNE.ask4Disconnection(descriptor);  
		    }
		    if (this.outboundPortNW.connected()) {
		    	this.outboundPortNW.ask4Disconnection(descriptor);  
		    }
		    if (this.outboundPortSE.connected()) {
		    	this.outboundPortSE.ask4Disconnection(descriptor);
		    }   
		    if (this.outboundPortSW.connected()) {
		    	this.outboundPortSW.ask4Disconnection(descriptor); 
		    }
		
		}	
		
		@Override
		public void finalise() throws Exception {
	//		Lorsque le nœud quitte le réseau de capteur, il doit d’abord se déconnecter de ses voisins puis
	//		se désenregistrer auprès du registre en appelant la méthode unregister
		    this.logMessage("stopping node component.");
		    this.printExecutionLogOnFile("node");
		      
		    // Vérifie si le port est connecté avant de tenter de le déconnecter
		    
		    if (this.outboundPortClock.connected()) {
		        this.doPortDisconnection(this.outboundPortClock.getPortURI());
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
		    
	        if (this.outboundPortRegistre.connected()) {
		    	this.outboundPortRegistre.unregister(descriptor.nodeIdentifier());
		        this.doPortDisconnection(this.outboundPortRegistre.getPortURI());
		    }   
	
		    super.finalise();
		}
	
		@Override
		public void shutdown() throws ComponentShutdownException {
			try {
				   outboundPortClock.unpublishPort();
				} catch (Exception e) {
					e.printStackTrace();
				}
		     try {
				this.inboundPortSensor.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortRegistre.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortNE.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortNW.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortSE.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortSW.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.shutdown();
		}
		
		@Override
		public void shutdownNow() throws ComponentShutdownException {
			try {
				   outboundPortClock.unpublishPort();
				} catch (Exception e) {
					e.printStackTrace();
				}
		     try {
				this.inboundPortSensor.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortRegistre.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortNE.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortNW.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortSE.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortSW.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.shutdownNow();
		}
	
		//--------------------------------------------------------------------------
		// Component internal services
		//--------------------------------------------------------------------------
		
		/**
		 * Processes a request by wrapping it into a {@link RequestContinuationI} instance and executing it.
		 * This method prepares the node's state and forwards the request for detailed processing.
		 * 
		 * @param request The {@link RequestI} instance containing the initial request details.
		 * @return A {@link QueryResultI} object representing the result of the query execution.
		 * @throws Exception If any errors occur during the processing of the request.
		 */
		public QueryResultI execute(RequestI request) throws Exception{
			gui.resetNodesBlink();
			ProcessingNode node = new ProcessingNode(this.descriptor.nodeIdentifier(), this.descriptor.nodePosition(), this.neighbor,sensors);
            QueryResult queryR = new QueryResult(new ArrayList<>(),new ArrayList<>());
			ExecutionState executionState = new ExecutionState(node,queryR);
			this.es = executionState;
			RequestContinuationI clientRequest = new RequestContinuation(request.getQueryCode(),request.clientConnectionInfo(), this.es);
			QueryResultI qr = this.execute((RequestContinuationI)clientRequest);
			return qr;
		}
		
		/**
		 * Executes a sensor network query based on a continuation request. This method handles both
		 * geographic and broadcast queries by updating and evaluating the state of execution based
		 * on sensor data and node information. It also manages network communication with neighboring
		 * nodes to fulfill the query requirements.
		 * 
		 * <p>This method checks if a request has been processed before to avoid redundant processing
		 * and potential infinite loops in the network.</p>
		 * 
		 * @param request The {@link RequestContinuationI} instance containing the continuation details.
		 * @return A {@link QueryResultI} object representing the aggregated or evaluated result after executing the query.
		 * @throws Exception If there are issues in query processing or during network communication.
		 */
		@Override
		public QueryResultI execute(RequestContinuationI request) throws Exception {
		    if (this.processedRequests.contains(request.requestURI())) {
		        return new QueryResult(new ArrayList<>(), new ArrayList<>());
		    }
		    return processQuery(request);
		}
		
		/**
		 * Asynchronously executes a request based on the provided {@link RequestContinuationI}.
		 * This method is intended for asynchronous handling of requests where the results are not
		 * immediately required to be returned but can be processed in a non-blocking manner.
		 *
		 * <p>This method is currently a stub and needs to be implemented.</p>
		 * 
		 * @param requestContinuation The {@link RequestContinuationI} object representing the request continuation.
		 * @throws Exception If there are issues in processing the request asynchronously.
		 */
		@Override
		public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
			// TODO Auto-generated method stub
			
		}
		
		/**
		 * Attempts to establish a connection with a specified neighbouring node. This method updates
		 * the graphical interface to reflect the connection and adds the neighbour to the known set.
		 * It determines the appropriate direction and manages the connection based on the determined direction.
		 *
		 * @param neighbour The {@link NodeInfoI} instance representing the neighbouring node to connect with.
		 * @throws Exception If managing the connection based on direction fails.
		 */
		@Override
		public void ask4Connection(NodeInfoI neighbour) throws Exception {
		    this.neighbor.add(neighbour);

		    try {
		    	connectToNeighbor(neighbour, false);
		    } catch (Exception e) {
//		        logger.error("Failed to manage connection for direction: " + direction, e);
		    }
		}
		
		/**
		 * Disconnects from a specified neighbouring node. This method updates the graphical interface
		 * to reflect the disconnection and uses the directional information to manage the disconnection process.
		 *
		 * @param neighbour The {@link NodeInfoI} instance representing the neighbouring node to disconnect from.
		 * @throws Exception If there is an issue in managing the disconnection.
		 */
		@Override
		public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		    gui.removeGraphicalConnection(neighbour.nodeIdentifier(), this.descriptor.nodeIdentifier());
		    Direction direction = this.descriptor.nodePosition().directionFrom(neighbour.nodePosition());
		    URISensorOutBoundPortToNode outboundPort = getPortByDirection(direction);
		    if (outboundPort != null) {
		        try {
		            if (outboundPort.connected()) {
				        this.doPortDisconnection(outboundPort.getPortURI());
				    }
				    NodeInfoI node = outboundPortRegistre.findNewNeighbour(this.descriptor, direction);
				    if (node != null) {
				    	connectToNeighbor(node, true);
				    }
		        } catch (Exception e) {
//		            logger.error("Error handling disconnection and reconnection", e);
		        }
		    } else {
//		        this.logMessage("Invalid direction: {}", direction);
		    }
		}
		
		/**
		 * Retrieves the outbound port corresponding to a specific direction.
		 *
		 * @param direction The direction for which the outbound port is needed.
		 * @return The outbound port corresponding to the given direction or {@code null} if the direction is invalid.
		 */
		private URISensorOutBoundPortToNode getPortByDirection(Direction direction) {
		    switch (direction) {
		        case NE: return outboundPortNE;
		        case NW: return outboundPortNW;
		        case SE: return outboundPortSE;
		        case SW: return outboundPortSW;
		        default:
		        	this.logMessage("Unknown direction: " + direction);
		        	return null; 
		    }
		}
		
		/**
		 * Registers the current node and establishes connections with all known neighbours. This method
		 * logs the registration process and proceeds to connect to each neighbour individually.
		 */
		private void registerAndConnectToNeighbor() {
		    this.logMessage("Registering node and connecting to neighbors");
		    try {
		        this.neighbor = Collections.synchronizedSet(this.outboundPortRegistre.register(descriptor));
		        synchronized (neighbor) {
		            for (NodeInfoI node : neighbor) {
		                connectToNeighbor(node, true);
		            }
		        }

		    } catch (Exception e) {
		    }
		}
		
		/**
		 * Connects to a specified neighboring node based on the calculated directional position. This method
		 * manages the establishment of the connection by first disconnecting any existing connections on the
		 * relevant port before making a new connection. It also optionally initiates a connection request to the
		 * descriptor depending on the {@code ask} parameter.
		 *
		 * @param node The {@link NodeInfoI} representing the neighbor node to connect to.
		 * @param ask A boolean flag that determines whether to initiate a connection request after establishing the
		 *            connection. If true, the method will initiate a connection request; otherwise, it will only establish
		 *            the connection.
		 * @throws Exception if there is an issue retrieving the direction, handling port connections/disconnections,
		 *                   or any other operation-related exceptions.
		 */
		private void connectToNeighbor(NodeInfoI node, boolean ask) throws Exception {
		    Direction direction = descriptor.nodePosition().directionFrom(node.nodePosition());
		    URISensorOutBoundPortToNode portToUse = getPortByDirection(direction);

		    if (portToUse == null) {
		        this.logMessage("Invalid direction specified: " + direction);
		        return;
		    }

		    String inboundPortSensor = ((BCM4JavaEndPointDescriptorI) node.p2pEndPointInfo()).getInboundPortURI();
		    if (portToUse.connected()) {
		        portToUse.ask4Disconnection(descriptor);
		        doPortDisconnection(portToUse.getPortURI());
		    }

		    doPortConnection(portToUse.getPortURI(), inboundPortSensor, ConnectorSensorToSensor.class.getCanonicalName());
		    nodeOutboundPorts.put(portToUse, node);
		    this.logMessage("Connection established towards neighbor at " + direction + ": " + node.nodeIdentifier());
		    gui.addGraphicalConnection(this.descriptor.nodeIdentifier(), node.nodeIdentifier());
		    if (ask) {
		        portToUse.ask4Connection(descriptor);
		    }
		}

		/**
		 * Processes a request by orchestrating various steps including state preparation, query evaluation,
		 * UI updates, and query propagation based on the execution state.
		 *
		 * @param request The {@link RequestContinuationI} instance containing continuation details of the query.
		 * @return A {@link QueryResultI} object representing the aggregated or evaluated result after executing the query.
		 * @throws Exception if there are issues during query processing or propagation.
		 */
		private QueryResultI processQuery(RequestContinuationI request) throws Exception {
		    ExecutionState es = prepareExecutionState(request);
		    evaluateQuery(request, es);
		    gui.toggleNodeBlinking(this.descriptor.nodeIdentifier());
		    return handleQueryPropagation(request, es);
		}
		
		/**
		 * Prepares the execution state for processing a query by cloning the existing state and updating it
		 * with the current node information.
		 *
		 * @param request The {@link RequestContinuationI} with the existing execution state to be cloned and updated.
		 * @return An updated {@link ExecutionState} ready for query evaluation.
		 * @throws CloneNotSupportedException if the existing execution state cannot be cloned.
		 */
		private ExecutionState prepareExecutionState(RequestContinuationI request) throws CloneNotSupportedException {
		    ProcessingNode node = new ProcessingNode(this.descriptor.nodeIdentifier(),
		        this.descriptor.nodePosition(), this.neighbor, sensors);
		    ExecutionState es = ((ExecutionState) request.getExecutionState()).clone();
		    es.updateProcessingNode(node);
		    return es;
		}
		
		/**
		 * Evaluates the given query based on the type (GQuery or BQuery) and updates the execution state accordingly.
		 *
		 * @param request The {@link RequestContinuationI} instance containing the query and related information.
		 * @param es The {@link ExecutionState} which is to be used and modified during query evaluation.
		 */
		private void evaluateQuery(RequestContinuationI request, ExecutionState es) {
		    if (request.getQueryCode() instanceof GQuery) {
		        ((GQuery) request.getQueryCode()).eval(es);
		    } else {
		        ((BQuery) request.getQueryCode()).eval(es);
		    }
		}
		
		/**
		 * Determines the mode of query propagation (flooding or directional) based on the execution state
		 * and executes the propagation accordingly.
		 *
		 * @param request The {@link RequestContinuationI} instance containing the query and its continuation data.
		 * @param es The {@link ExecutionState} used to determine the propagation strategy.
		 * @return The result of the query after attempting propagation, if applicable.
		 * @throws Exception if there is an issue during query propagation.
		 */
		private QueryResultI handleQueryPropagation(RequestContinuationI request, ExecutionState es) throws Exception {
		    if (es.isFlooding()) {
		        return propagateFlooding(request, es);
		    } else if (es.isDirectional()) {
		        return handleDirectionalPropagation(request, es);
		    }
		    return es.getCurrentResult();
		}
		
		/**
		 * Handles query propagation using a flooding approach where the query is sent to all reachable neighbors
		 * within a maximal distance.
		 *
		 * @param request The request continuation instance.
		 * @param es The current execution state holding network topology and distance metrics.
		 * @return The current result of the query after flooding to neighbors.
		 * @throws Exception if there is an issue during the flooding propagation.
		 */
		private QueryResultI propagateFlooding(RequestContinuationI request, ExecutionState es) throws Exception {
		    this.processedRequests.add(request.requestURI());
		      synchronized (neighbor) {
		    for (NodeInfoI n : neighbor) {
		        Direction d = this.descriptor.nodePosition().directionFrom(n.nodePosition());
		        if (es.withinMaximalDistance(n.nodePosition())) {
		            executeNeighborQuery(d, request);
		        } else {
		            return es.getCurrentResult();
		        }
		    }
		      }
		    return es.getCurrentResult();
		}
		
		/**
		 * Manages directional query propagation based on the specified directions within the execution state.
		 * This method determines the next direction for query propagation by examining available directions
		 * and the state of connection to those directions. It attempts to propagate the query to the first
		 * available and connected direction. If no directions are available or if there are no more hops
		 * allowed, it simply returns the current result.
		 *
		 * @param request The request continuation detailing the query continuation. It is used to pass along
		 *                the query specifics as the propagation proceeds to different directions.
		 * @param es The execution state which includes directional information and propagation logic.
		 *           It provides context such as available directions, whether more hops are possible, and
		 *           the current result of the query propagation.
		 * @return The query result after attempting directional propagation. If no propagation occurs,
		 *         the current result of the execution state is returned.
		 * @throws Exception if there is an issue during directional propagation, such as a failure in
		 *                   executing the query on the neighbor or issues with connection handling.
		 */
		private QueryResultI handleDirectionalPropagation(RequestContinuationI request, ExecutionState es) throws Exception {
			ArrayList<Direction> directions = new ArrayList<>(es.getDirections());
		    if (directions.isEmpty() || es.noMoreHops()) {
		        return es.getCurrentResult();
		    }

		    Iterator<Direction> directionIterator = directions.iterator();
		    while (directionIterator.hasNext()) {
		        Direction d = directionIterator.next();
		        URISensorOutBoundPortToNode port = getPortByDirection(d);
		        if (port != null && port.connected()) {
		            executeNeighborQuery(d, request);
		            break; 
		        }

		    }
		    return es.getCurrentResult();
		}
		
		/**
		 * Executes the query on a neighboring node based on the specified direction.
		 *
		 * @param direction The direction in which the neighbor node is located.
		 * @param request The {@link RequestContinuationI} instance detailing the query continuation.
		 * @throws Exception if there is an issue executing the query on the neighbor node.
		 */
		private void executeNeighborQuery(Direction direction, RequestContinuationI request) throws Exception {
		    URISensorOutBoundPortToNode port = getPortByDirection(direction);
		    if (port != null && port.connected()) {
		        port.execute(request);
		        gui.startGraphicalLightAnimation(this.descriptor.nodeIdentifier(), this.nodeOutboundPorts.get(port).nodeIdentifier());
		    }
		}
	
		/**
		 * Initializes and publishes all outbound ports necessary for the sensor node's operation.
		 * This includes ports for registering with the network, connecting to the clock server, and
		 * interacting with other nodes in specific directions.
		 * @throws Exception If there is an issue initializing or publishing the ports.
		 */
		private void initializeOutboundPorts() throws Exception {
	        this.outboundPortRegistre = new URISensorOutBoundPortToRegister(this);
	        this.outboundPortNE = new URISensorOutBoundPortToNode(this);
	        this.outboundPortNW = new URISensorOutBoundPortToNode(this);
	        this.outboundPortSE = new URISensorOutBoundPortToNode(this);
	        this.outboundPortSW = new URISensorOutBoundPortToNode(this);

	        this.nodeOutboundPorts = new HashMap<>();
	        
	        this.outboundPortClock = new ClocksServerOutboundPort(this);

	        // Publication des ports sortants
	        this.outboundPortRegistre.publishPort();
	        this.outboundPortNE.publishPort();
	        this.outboundPortNW.publishPort();
	        this.outboundPortSE.publishPort();
	        this.outboundPortSW.publishPort();
	        
	        this.outboundPortClock.publishPort();
	    }

		// -----------------------------------------------------------------------------
		
	}