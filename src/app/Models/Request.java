package app.Models;

import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

/**
 * Represents a request in a sensor network, encapsulating the details needed to perform
 * a query, including the client's connection info and the query code itself.
 * This class conforms to the {@link RequestI} interface, defining the structure for requests
 * in the sensor network system.
 */
public class Request implements RequestI {
    private static final long serialVersionUID = 1L;
    private final String uri;          // Unique URI for this request, automatically generated
    private ConnectionInfoI client;    // Connection information for the client issuing the request
    private QueryI queryCode;          // The query code associated with this request

    /**
     * Constructs a Request with specified query code and client connection information.
     * Automatically assigns a unique URI to each request using {@link AbstractPort#generatePortURI()}.
     *
     * @param queryCode The {@link QueryI} code that specifies what operation is to be performed.
     * @param client The {@link ConnectionInfoI} providing details about the client's connection.
     */
    public Request(QueryI queryCode, ConnectionInfoI client) {
        this.queryCode = queryCode;
        this.client = client;
        this.uri = AbstractPort.generatePortURI();
    }

    public Request(QueryI queryCode, ConnectionInfoI client, String uri) {
        this.queryCode = queryCode;
        this.client = client;
        this.uri = uri;
    }
    /**
     * Retrieves the unique URI for this request.
     *
     * @return A string representing the unique URI of the request.
     */
    @Override
    public String requestURI() {
        return uri;
    }

    /**
     * Retrieves the query code associated with this request.
     *
     * @return The {@link QueryI} instance defining the operation of this request.
     */
    @Override
    public QueryI getQueryCode() {
        return queryCode;
    }

    /**
     * Indicates whether the request is to be processed asynchronously.
     * Currently, this implementation always returns false, indicating synchronous processing.
     *
     * @return A boolean value, always false, indicating that this request does not support asynchronous processing.
     */
    @Override
    public boolean isAsynchronous() {
        return false;
    }

    /**
     * Retrieves the client connection information associated with this request.
     *
     * @return The {@link ConnectionInfoI} that contains details about how the client is connected.
     */
    @Override
    public ConnectionInfoI clientConnectionInfo() {
        return client;
    }
}
