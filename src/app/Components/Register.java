package app.Components;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import app.Models.Position;
import app.Ports.URIRegisterInboundPortForClient;
import app.Ports.URIRegisterInboundPortForNode;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;


/**
 * Represents a registration component within a sensor network system. This component offers interfaces for lookup and registration,
 * allowing sensor nodes and clients to register themselves and discover other registered nodes. It facilitates the management
 * of node information and supports node searches based on geographical proximity and connectivity requirements.
 *
 * The class maintains a set of registered nodes and provides methods to register new nodes, unregister existing nodes,
 * and discover neighbors based on directional and range-specific criteria. Each registered node is uniquely identified, and their
 * information can be retrieved or updated through this component.
 *
 * @author Malek Bouzarkouna, Younes Chetouani, Amine Zemali
 * @version 1.0
 * @since 1.0
 */

@OfferedInterfaces(offered = {LookupCI.class, RegistrationCI.class})
public class Register  extends AbstractComponent {
	
	// ------------------------------------------------------------------------
	// Instance variables
	// ------------------------------------------------------------------------
	
	/** Inbound port for communication with nodes */
	protected final URIRegisterInboundPortForNode inboundPortNode ;
	/** Inbound port for communication with clients */
	protected final URIRegisterInboundPortForClient inboundPortClient ;
	/** Set of all registered nodes */
	private Set<NodeInfoI> registeredNodes = new HashSet<>();
	
	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------
	
	 /**
     * Constructs a Register component with specified inbound ports for nodes and clients.
     * Initializes the component with necessary ports and publishes them for communication.
     *
     * @param inboundPortURINode The URI for the node inbound port.
     * @param inboundPortURIClient The URI for the client inbound port.
     * @throws Exception If there is an issue initializing the component or publishing ports.
     */
	protected Register(String inboundPortURINode, String inboundPortURIClient) throws Exception {	
		super("Register", 1, 0) ;	
		this.inboundPortNode = new URIRegisterInboundPortForNode(inboundPortURINode ,this) ;
		this.inboundPortNode.publishPort() ;	
		this.inboundPortClient = new URIRegisterInboundPortForClient(inboundPortURIClient ,this) ;
		this.inboundPortClient.publishPort() ;	
	}
	
	//-------------------------------------------------------------------------
	// Component life-cycle
	//-------------------------------------------------------------------------
	
	@Override
	public void start() throws ComponentStartException {
		this.logMessage("starting register component.") ;
		super.start() ;
	}
	
	@Override
	public void finalise() throws Exception {
		this.logMessage("stopping Register component.") ;
		this.printExecutionLogOnFile("Register");
		super.finalise();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			this.inboundPortNode.unpublishPort() ;
			this.inboundPortClient.unpublishPort() ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}
	
	@Override
	public void shutdownNow() throws ComponentShutdownException {
		try {
			this.inboundPortNode.unpublishPort() ;
			this.inboundPortClient.unpublishPort() ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdownNow();
	}

	//-------------------------------------------------------------------------
	// Component internal services
	//-------------------------------------------------------------------------
	
    /**
     * Checks if the node is already registered in the network.
     *
     * @param nodeIdentifier The identifier of the node to check.
     * @throws Exception If the check fails due to internal errors.
     */
	public boolean registered(String nodeIdentifier)throws Exception{
		return registeredNodes.stream().anyMatch(nodeInfo -> nodeInfo.nodeIdentifier().equals(nodeIdentifier));
		}
	
	 /**
     * Registers a node in the sensor network, calculates its neighbors based on geographic and connectivity constraints,
     * and logs the registration.
     *
     * @param nodeInfo The information about the node to register.
     * @return A set of neighbors for the newly registered node.
     * @throws Exception If the registration fails due to internal errors.
     */
	public   Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
	    registeredNodes.add(nodeInfo);
	    Position p = (Position) nodeInfo.nodePosition();
	    Set<NodeInfoI> neighbours =  new HashSet<>();
	    NodeInfoI closestNorthEastNeighbour = null;
	    NodeInfoI closestNorthWestNeighbour = null;
	    NodeInfoI closestSouthEastNeighbour = null;
	    NodeInfoI closestSouthWestNeighbour = null;
	    double closestNE = Double.MAX_VALUE;
	    double closestNW = Double.MAX_VALUE;
	    double closestSE = Double.MAX_VALUE;
	    double closestSW = Double.MAX_VALUE;

	    for (NodeInfoI otherNodeInfo : registeredNodes) {
	        if (!otherNodeInfo.nodeIdentifier().equals(nodeInfo.nodeIdentifier())) {
	            Position otherPosition = (Position) otherNodeInfo.nodePosition();
	            double distance = p.distance(otherPosition);
	            if (distance <= nodeInfo.nodeRange() && distance <= otherNodeInfo.nodeRange()) {
	                if (p.northOf(otherPosition) && p.eastOf(otherPosition) && distance < closestNE) {
	                    closestNE = distance;
	                    closestNorthEastNeighbour = otherNodeInfo;
	                } else if (p.northOf(otherPosition) && p.westOf(otherPosition) && distance < closestNW) {
	                    closestNW = distance;
	                    closestNorthWestNeighbour = otherNodeInfo;
	                } else if (p.southOf(otherPosition) && p.eastOf(otherPosition) && distance < closestSE) {
	                    closestSE = distance;
	                    closestSouthEastNeighbour = otherNodeInfo;
	                } else if (p.southOf(otherPosition) && p.westOf(otherPosition) && distance < closestSW) {
	                    closestSW = distance;
	                    closestSouthWestNeighbour = otherNodeInfo;
	                }
	            }
	        }
	    }
	    if (closestNorthEastNeighbour != null) neighbours.add(closestNorthEastNeighbour);
	    if (closestNorthWestNeighbour != null) neighbours.add(closestNorthWestNeighbour);
	    if (closestSouthEastNeighbour != null) neighbours.add(closestSouthEastNeighbour);
	    if (closestSouthWestNeighbour != null) neighbours.add(closestSouthWestNeighbour);
	    StringBuilder logMessageBuilder = new StringBuilder();
	    logMessageBuilder.append("Adding nodeInfo: ").append(nodeInfo.nodeIdentifier()).append(" Neighbours : [");

	    for (NodeInfoI neighbour : neighbours) {
	        logMessageBuilder.append(neighbour.nodeIdentifier()).append(", ");
	    }
	    if (!neighbours.isEmpty()) {
	        logMessageBuilder.setLength(logMessageBuilder.length() - 2); // Retire les deux derniers caractères (", ")
	    }
	    logMessageBuilder.append("]");
	    this.logMessage(logMessageBuilder.toString());
	    return neighbours;
	}

	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction direction) throws Exception {
	    if (!(nodeInfo.nodePosition() instanceof Position)) {
	        throw new IllegalArgumentException("Node position must be of type Position");
	    }
	    Position position = (Position) nodeInfo.nodePosition();
	    NodeInfoI closestNeighbour = null;
	    double closestDistance = Double.MAX_VALUE;

	    for (NodeInfoI potentialNeighbour : registeredNodes) {
	        if (!potentialNeighbour.nodeIdentifier().equals(nodeInfo.nodeIdentifier()) && potentialNeighbour.nodePosition() instanceof Position) {
	            Position potentialPosition = (Position) potentialNeighbour.nodePosition();
	            double distance = position.distance(potentialPosition);
	            Direction potentialDirection = position.directionFrom(potentialPosition);

	            if (distance <= nodeInfo.nodeRange() && distance < closestDistance && potentialDirection.equals(direction)) {
	                closestDistance = distance;
	                closestNeighbour = potentialNeighbour;
	            }
	        }
	    }
	    return closestNeighbour;
	}

	
	 /**
     * Unregisters a node from the sensor network.
     *
     * @param nodeIdentifier The identifier of the node to unregister.
     * @throws Exception If the unregistration fails due to internal errors.
     */
	public void unregister(String nodeIdentifier) throws Exception{
        registeredNodes.removeIf(nodeInfo -> nodeInfo.nodeIdentifier().equals(nodeIdentifier));
	}
	
	/**
	* Searches for and returns the connection information for a specific node identified by its ID.
	* This method iterates over the set of registered nodes and filters to find the one that matches
	* the provided identifier. If no matching node is found, the method returns {@code null}.
	*
	* @param sensorNodeId The unique identifier of the sensor node being searched for.
	* @return The {@link ConnectionInfoI} interface of the node if found, otherwise {@code null}.
	* @throws Exception If an error occurs during the search for the node.
	*/
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		this.logMessage("Request findByIdentifier received on "+sensorNodeId) ;
	    return registeredNodes.stream()
	            .filter(nodeInfo -> nodeInfo.nodeIdentifier().equals(sensorNodeId))
	            .findFirst()
	            .orElse(null);
	}
	
	/**
	* Searches for and returns a set of connection information for all nodes located within a specified geographical area.
	* It uses a geographical predicate to filter all registered nodes that are inside the provided zone.
	* This method is useful for identifying all nodes within a given region, facilitating operations such as regional
	* configuration updates or responses to localized events.
	*
	* @param zone The {@link GeographicalZoneI} interface defining the geographical area for which nodes are being searched.
	* @return A set of {@link ConnectionInfoI} interfaces representing the nodes found within the specified area.
	* @throws Exception If an error occurs during the search for nodes within the area.
	*/
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI zone) throws Exception {
	    return registeredNodes.stream()
	            .filter(nodeInfo -> zone.in(nodeInfo.nodePosition()))
	            .collect(Collectors.toSet());
	}
}
