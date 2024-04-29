package app.Components;

import java.time.Instant;
import java.util.ArrayList;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;



import app.Models.Bcm4javaEndPointDescriptor;
import app.Models.ClientConfig;
import app.Models.ConnectionInfo;
import app.Models.Request;
import app.Ports.URIClientInboundPortForNode;
import app.Ports.URIClientOutBoundPortToNode;
import app.Ports.URIClientOutBoundPortToRegister;
import app.connectors.ConnectorRegistreClient;
import app.connectors.ConnectorClientToSensor;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.helpers.TracerI;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

/**
 * Represents a client component in a sensor network system, responsible for
 * initiating and managing queries to sensors through a network of nodes. This
 * component is designed to interact with both a central register and individual
 * sensor nodes to perform various query tasks based on configured parameters.
 *
 * This class extends {@link AbstractComponent} to utilize its lifecycle
 * management and networking capabilities, integrating several custom outbound
 * ports for communication with other system components.
 *
 * <p>
 * Usage includes connecting to a clock server for timing operations, executing
 * scheduled queries, and handling responses. It also manages its own lifecycle
 * events such as starting, executing, finalizing, and shutting down, with
 * detailed logging at each step.
 * </p>
 *
 * @author Sorbonne_U Components
 * @version 1.0
 */
@RequiredInterfaces(required = { RequestingCI.class, LookupCI.class, ClocksServerCI.class })
@OfferedInterfaces(offered = { RequestResultCI.class })
public class Client extends AbstractComponent {

	// ------------------------------------------------------------------------
	// Instance variables
	// ------------------------------------------------------------------------

	protected URIClientOutBoundPortToRegister uriOutPortRegister;
	protected URIClientOutBoundPortToNode uriOutPortNode;
	private final String inBoundPortRegister;
	private URIClientInboundPortForNode inboundPortClient;

	private String TEST_CLOCK_URI;
	private ClocksServerOutboundPort outBoundPortClock;
	private final String requestNodeName;
	private final boolean isRequestAsync;
	private List<RequestI> requests;
	private ConcurrentHashMap<String, List<QueryResultI>> resultsMap = new ConcurrentHashMap<>();
	private final ConnectionInfoI ClientInfo;

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------

	/**
	 * Constructs a new Client with specified configuration.
	 *
	 * Initializes ports and sets up component based on the provided
	 * {@link ClientConfig}. It automatically publishes all necessary ports and
	 * prepares the component for starting.
	 *
	 * @param configClient the configuration object containing initial setup
	 *                     information such as node name, clock URI, requests, and
	 *                     network configuration.
	 * @throws Exception if there is an error in setting up the ports or any other
	 *                   configuration error.
	 */
	protected Client(ClientConfig configClient) throws Exception {

		super("Client " + configClient.getName(), 5, 10);

		this.TEST_CLOCK_URI = configClient.getUriClock();
		this.outBoundPortClock = new ClocksServerOutboundPort(this);
		outBoundPortClock.publishPort();

		this.uriOutPortRegister = new URIClientOutBoundPortToRegister(this);
		this.uriOutPortRegister.publishPort();

		this.requestNodeName = configClient.getRequestNodeName();
		this.uriOutPortNode = new URIClientOutBoundPortToNode(this);
		this.uriOutPortNode.publishPort();

		this.inBoundPortRegister = configClient.getInboundPortRegister();

		this.inboundPortClient = new URIClientInboundPortForNode(this);
		this.inboundPortClient.publishPort();

		BCM4JavaEndPointDescriptorI uriClient = new Bcm4javaEndPointDescriptor(inboundPortClient.getPortURI());

		this.ClientInfo = new ConnectionInfo(requestNodeName, uriClient);

		this.requests = configClient.getRequests();
		this.isRequestAsync = configClient.getIsRequestAsync();
		TracerI tracer = this.getTracer();

		tracer.setRelativePosition(configClient.getName() % 3, configClient.getName() / 3);
		AbstractComponent.checkImplementationInvariant(this);
		AbstractComponent.checkInvariant(this);
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * Starts the client component, establishing necessary connections for
	 * operations. Connects to both a clock server and a register to facilitate
	 * query operations.
	 *
	 * @throws ComponentStartException if there is any issue in starting the
	 *                                 component or establishing connections.
	 */
	@Override
	public synchronized void start() throws ComponentStartException {
		this.logMessage("starting client component.");

		// ---------------------------------------------------------------------
		// Connection phase
		// ---------------------------------------------------------------------

		// do the connection with the clock
		try {
			this.doPortConnection(outBoundPortClock.getPortURI(), ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());

		} catch (Exception e) {
			e.printStackTrace();
		}

		// do the connection with the register
		try {
			this.doPortConnection(this.uriOutPortRegister.getPortURI(), inBoundPortRegister,
					ConnectorRegistreClient.class.getCanonicalName());
			this.logMessage("Connected to Register");
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.start();
	}

	/**
	 * Executes the primary logic of the client, handling scheduled requests based
	 * on time and other triggering conditions defined in the configuration.
	 *
	 * @throws Exception if there is any error during the execution of scheduled
	 *                   tasks.
	 */
	@Override
	public void execute() throws Exception {
		AcceleratedClock ac = outBoundPortClock.getClock(TEST_CLOCK_URI);
		ac.waitUntilStart();
		Instant start = ac.getStartInstant();
		Instant i2 = start.plusSeconds(120);

		long delay = ac.nanoDelayUntilInstant(i2);

		this.scheduleTask(o -> {
			try {
				this.requestNodeAndConnectByName(this.requestNodeName);
				if (this.uriOutPortNode.connected()) {
					Instant i3 = i2.plusSeconds(60);
					scheduleTasks(requests, i3, ac, 200, isRequestAsync);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, delay, TimeUnit.NANOSECONDS);

	}

	/**
	 * Finalizes the client component, ensuring all connections are properly closed
	 * and any persistent states are cleaned up before shutdown.
	 *
	 * @throws Exception if there is an error during the component finalization
	 *                   process.
	 */
	@Override
	public synchronized void finalise() throws Exception {
		this.logMessage("stopping client component.");
		this.printExecutionLogOnFile("client");
		if (this.uriOutPortRegister.connected()) {
			this.doPortDisconnection(this.uriOutPortRegister.getPortURI());
		}
		if (this.uriOutPortNode.connected()) {
			this.doPortDisconnection(this.uriOutPortNode.getPortURI());
		}
		if (this.outBoundPortClock.connected()) {
			this.doPortDisconnection(this.outBoundPortClock.getPortURI());
		}

		super.finalise();
	}

	/**
	 * Shuts down the component immediately, without waiting for ongoing tasks to
	 * complete. Ensures all resources are released and ports are unpublished.
	 *
	 * @throws ComponentShutdownException if there is an error during the immediate
	 *                                    shutdown process.
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.uriOutPortRegister.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.uriOutPortNode.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			this.outBoundPortClock.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

	@Override
	public synchronized void shutdownNow() throws ComponentShutdownException {
		try {
			this.uriOutPortRegister.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.uriOutPortNode.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			this.outBoundPortClock.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdownNow();
	}

//-------------------------------------------------------------------------
// Component internal services
//-------------------------------------------------------------------------

	/**
	 * Executes a single request to a sensor node, processing the response and
	 * logging the result.
	 *
	 * @param clientRequest the request to execute.
	 * @throws Exception if there is an error in executing the request or processing
	 *                   the response.
	 */
	private void executeRequest(RequestI clientRequest) throws Exception {

		if (clientRequest instanceof Request) {
			Request request = (Request) clientRequest;
			request.setAsynchronous(false);
			QueryResultI queryResult = uriOutPortNode.execute(request);
			printQueryResult(queryResult);
		} else {
			throw new IllegalArgumentException("Expected clientRequest to be an instance of Request");
		}
	}

	private void executeRequestAsync(RequestI clientRequest) throws Exception {
		if (clientRequest instanceof Request) {
			Request request = (Request) clientRequest;
			request.setAsynchronous(true);
			uriOutPortNode.executeAsync(request);  
		} else {
			throw new IllegalArgumentException("Expected clientRequest to be an instance of Request");
		}
	}

	private void printQueryResult(QueryResultI queryResult) {
		if (queryResult.isBooleanRequest()) {
			logMessage("request result: " + queryResult.positiveSensorNodes().toString());
		} else if (queryResult.isGatherRequest()) {
			logMessage("request result: " + queryResult.gatheredSensorsValues().toString());
		}

	}

	private void requestNodeAndConnectByName(String nodeName) throws Exception {
		Optional<ConnectionInfoI> node = getNode(nodeName);
		node.ifPresentOrElse(this::connectToNode, () -> logMessage("Node " + nodeName + " not found."));
	}

	/**
	 * Attempts to retrieve connection information for a specified node by its
	 * identifier. This method queries the registered nodes through an outbound port
	 * to the register component, returning an {@link Optional} that contains the
	 * node's connection information if found. If the node is not found or if an
	 * error occurs during the query, the method returns an empty {@link Optional}.
	 *
	 * @param nodeName The identifier of the node for which connection information
	 *                 is sought.
	 * @return An {@link Optional} of {@link ConnectionInfoI} which will be
	 *         non-empty if the node is found, and empty if the node is not found or
	 *         if an error occurs during the request.
	 */
	private Optional<ConnectionInfoI> getNode(String nodeName) {
		try {
			logMessage("Requesting node: " + nodeName);
			ConnectionInfoI nodeResult = this.uriOutPortRegister.findByIdentifier(nodeName);
			return Optional.ofNullable(nodeResult);
		} catch (Exception e) {
			logError("Error requesting node: " + nodeName, e);
			return Optional.empty();
		}
	}

	/**
	 * Connects to a specified node within the network using detailed connection
	 * information.
	 *
	 * @param node the connection information for the node to connect to.
	 */
	private void connectToNode(ConnectionInfoI node) {
		try {
			BCM4JavaEndPointDescriptorI endPointDescriptor = (BCM4JavaEndPointDescriptorI) node.endPointInfo();
			String inboundPortSensor = endPointDescriptor.getInboundPortURI();
			logMessage("Found node: " + node.nodeIdentifier());

			this.doPortConnection(this.uriOutPortNode.getPortURI(), inboundPortSensor,
					ConnectorClientToSensor.class.getCanonicalName());
			this.logMessage("Connected to " + node.nodeIdentifier());
		} catch (Exception e) {
			logError("Error connecting to node: " + node.nodeIdentifier(), e);
		}
	}

	private void logError(String message, Exception e) {
		logMessage(message);
		e.printStackTrace();
	}

	/**
	 * Schedules a list of requests to be executed sequentially with a defined delay
	 * between each execution. The tasks are scheduled based on a starting instant
	 * and are spaced out by a specified delay. This method leverages an
	 * {@link AcceleratedClock} to determine the precise nanoseconds delay until
	 * each task's intended start time, ensuring accurate scheduling according to
	 * the clock's acceleration.
	 *
	 * @param requests          The list of {@link RequestI} objects to be executed.
	 * @param start             The starting {@link Instant} from which the first
	 *                          task is scheduled.
	 * @param ac                The {@link AcceleratedClock} used to calculate
	 *                          precise delay times for task scheduling.
	 * @param delayBetweenTasks The delay in seconds between consecutive task
	 *                          executions.
	 */
	private void scheduleTasks(List<RequestI> requests, Instant start, AcceleratedClock ac,
			int delayBetweenTasks, boolean isAsynchronous) {
		for (int i = 0; i < requests.size(); i++) {
			final int taskIndex = i;
			long delay = isAsynchronous ? taskIndex * 20
					: ac.nanoDelayUntilInstant(start.plusSeconds(taskIndex * delayBetweenTasks));
			this.scheduleTask(o -> {
				try {
					Request request = (Request) requests.get(taskIndex);
					if (isAsynchronous) {
						request.setClient(ClientInfo);
						executeRequestAsync(request);
						Instant i2 = start.plusSeconds(60);
						long delay2 = ac.nanoDelayUntilInstant(i2);
						this.scheduleTask(ob -> {
							try {
								this.mergeAndPrint(request.requestURI());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}, delay2, TimeUnit.NANOSECONDS);
					} else {
						executeRequest(request);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}, delay, TimeUnit.NANOSECONDS);
		}
	}
	
	public void acceptRequestResult(String requestURI, QueryResultI result) {
//		JOptionPane.showMessageDialog(null, "" + result.gatheredSensorsValues(), "Information", JOptionPane.INFORMATION_MESSAGE);
		//The compute methods are thread-safe and atomic
	    resultsMap.compute(requestURI, (k, v) -> {
	        if (v == null) {
	            v = new ArrayList<>();
	        }
	        v.add(result);
	        return v;
	    });
	}

	public void mergeAndPrint(String requestURI) throws Exception {
		//The compute methods are thread-safe and atomic
	    List<QueryResultI> resultsList = resultsMap.computeIfAbsent(requestURI, k -> {
	        this.logMessage("No results to process found");
	        return new ArrayList<>();
	    });

	    if (!resultsList.isEmpty()) {
	    QueryResultI mergedResults = resultsList.get(0);;
	    for (int i = 1; i < resultsList.size(); i++) {
	        QueryResultI result = resultsList.get(i);
	        if (result.isGatherRequest()) {
	            updateGatheredSensors(mergedResults, result);
	        } else if (result.isBooleanRequest()) {
	            updatePositiveSensorNodes(mergedResults, result);
	        }
	    }
	    
	    this.printQueryResult(mergedResults);
	    }
	}


	private void updateGatheredSensors(QueryResultI existingResult, QueryResultI newResult) {
	    int thread = this.getTotalNumberOfThreads();
	    List<SensorDataI> sensors = new ArrayList<>(newResult.gatheredSensorsValues());
	    ArrayList<AbstractComponent.AbstractService<Void>> requests =
				new ArrayList<>() ;
	    int chunkSize = sensors.size() / thread;
	    for (int i = 0; i <thread; i++) {
	        int start = i * chunkSize;
	        int end = i == thread - 1 ? sensors.size() : (start + chunkSize);
	        List<SensorDataI> sublist = sensors.subList(start, end);
	        AbstractComponent.AbstractService<Void> request =
					new AbstractComponent.AbstractService<Void>() {
						@Override
						public Void call() throws Exception {
							 synchronized (existingResult) {
					                for (SensorDataI sensorData : sublist) {
					                    if (!existingResult.gatheredSensorsValues().contains(sensorData)) {
					                        existingResult.gatheredSensorsValues().add(sensorData);
					                    }
					                }
					            }
					            return null;
						}
					} ;
					request.setOwnerReference(this) ;
					requests.add(request);
	    }
		try {
			this.getExecutorService().invokeAll(requests) ;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	   
	}


	private void updatePositiveSensorNodes(QueryResultI existingResult, QueryResultI newResult) {
	    Set<String> uniqueSensorNodes = new LinkedHashSet<>(existingResult.positiveSensorNodes());
	    uniqueSensorNodes.addAll(newResult.positiveSensorNodes());
	    existingResult.positiveSensorNodes().clear();
	    existingResult.positiveSensorNodes().addAll(uniqueSensorNodes);
	}


}
