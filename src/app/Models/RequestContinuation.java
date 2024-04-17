package app.Models;

import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

/**
 * Extends the {@link Request} class to include handling of execution state,
 * facilitating tracking and management of request processing within a sensor network.
 * This class is used to represent a continuation of a request where the state of
 * the execution needs to be maintained and accessible.
 */
public class RequestContinuation extends Request implements RequestContinuationI {

    private static final long serialVersionUID = 1643394860402250861L;
    private ExecutionStateI es; // Holds the state of the request's execution.

    /**
     * Constructs a RequestContinuation with the specified query code, client connection info, and execution state.
     *
     * @param queryCode The {@link QueryI} code that specifies the operation to be performed.
     * @param clientConnectionInfo The {@link ConnectionInfoI} providing details about the client's connection.
     * @param es The {@link ExecutionStateI} object that tracks the current state of the request's execution.
     */
    public RequestContinuation(QueryI queryCode, ConnectionInfoI clientConnectionInfo, ExecutionStateI es) {
        super(queryCode, clientConnectionInfo);
        this.es = es;
    }

    /**
     * Retrieves the execution state associated with this request.
     *
     * @return The {@link ExecutionStateI} object representing the state of this request's processing.
     */
    @Override
    public ExecutionStateI getExecutionState() {
        return this.es;
    }
}
