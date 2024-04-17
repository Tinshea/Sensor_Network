package app.Models;

import java.util.Set;

import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

/**
 * Represents a processing node within a sensor network, managing sensor data and interactions
 * with neighboring nodes. This class encapsulates functionality for handling and responding
 * to data requests, including the propagation of such requests to neighboring nodes.
 */
public class ProcessingNode implements ProcessingNodeI {
    private String nodeIdentifier;
    private PositionI position;
    private Set<NodeInfoI> neighbours;
    private Set<SensorDataI> sensorData;

    /**
     * Constructs a ProcessingNode with specific properties.
     *
     * @param nodeIdentifier Unique identifier for the processing node.
     * @param position Geographical or logical position of the node within the network.
     * @param neighbours A set of neighboring nodes represented as {@link NodeInfoI}.
     * @param sensorData A set of sensor data points associated with this node.
     */
    public ProcessingNode(String nodeIdentifier, PositionI position, Set<NodeInfoI> neighbours, Set<SensorDataI> sensorData) {
        this.nodeIdentifier = nodeIdentifier;
        this.position = position;
        this.neighbours = neighbours;
        this.sensorData = sensorData;
    }

    /**
     * Retrieves the unique identifier of this processing node.
     * @return The node's unique identifier as a string.
     */
    @Override
    public String getNodeIdentifier() {
        return nodeIdentifier;
    }

    /**
     * Retrieves the position of the processing node.
     * @return The position as {@link PositionI}.
     */
    @Override
    public PositionI getPosition() {
        return position;
    }

    /**
     * Retrieves a set of neighboring nodes.
     * @return A set of nodes information as {@link NodeInfoI}.
     */
    @Override
    public Set<NodeInfoI> getNeighbours() {
        return neighbours;
    }

    /**
     * Retrieves specific sensor data by its identifier.
     * @param sensorIdentifier The unique identifier of the sensor data to retrieve.
     * @return The sensor data as {@link SensorDataI}, or null if not found.
     */
    @Override
    public SensorDataI getSensorData(String sensorIdentifier) {
    	 for(SensorDataI sensorData : this.sensorData){
             if (sensorData.getNodeIdentifier().equals(sensorIdentifier)){
                 return sensorData;
             }
         }
         return null;
    }

    /**
     * Propagates a data request to a specified node identifier, synchronously.
     * @param nodeIdentifier The identifier of the node to which the request should be propagated.
     * @param requestContinuation Continuation information for handling the request.
     * @return A {@link QueryResultI} representing the result of the query.
     * @throws Exception If there is an error in processing the request.
     */
    @Override
    public QueryResultI propagateRequest(String nodeIdentifier, RequestContinuationI requestContinuation)
            throws Exception {
        // Implementation details would be necessary here
        return null;
    }

    /**
     * Propagates a data request to a specified node identifier, asynchronously.
     * @param nodeIdentifier The identifier of the node to which the request should be propagated.
     * @param requestContinuation Continuation information for handling the request.
     * @throws Exception If there is an error in processing the request.
     */
    @Override
    public void propagateRequestAsync(String nodeIdentifier, RequestContinuationI requestContinuation) throws Exception {
        // Implementation details would be necessary here
    }
}
