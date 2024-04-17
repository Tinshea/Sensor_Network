package app.Models;

import java.util.ArrayList;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

/**
 * Represents the results of a query within a sensor network. This class can manage
 * both gathered sensor data and boolean-based results indicating the presence or absence
 * of certain conditions at sensor nodes.
 */
public class QueryResult implements QueryResultI {
    private static final long serialVersionUID = 7080596922014476376L;
    
    protected ArrayList<SensorDataI> sd;          // List to hold sensor data results
    private boolean isGather;                     // Flag indicating if the result is a gather type
    private boolean isBoolean;                    // Flag indicating if the result is a boolean type
    protected ArrayList<String> sensitiveNodes;   // List to hold identifiers of sensitive nodes

    /**
     * Constructs a QueryResult with specified sensor data and sensitive node identifiers.
     *
     * @param sd A list of {@link SensorDataI} representing the sensor data collected.
     * @param sensitiveNodes A list of node identifiers that are considered sensitive.
     */
    public QueryResult(ArrayList<SensorDataI> sd, ArrayList<String> sensitiveNodes) {
        this.sd = sd;
        this.sensitiveNodes = sensitiveNodes;
    }

    /**
     * Checks if the query result was meant to return a boolean value.
     *
     * @return true if the result is of a boolean type; false otherwise.
     */
    @Override
    public boolean isBooleanRequest() {
        return this.isBoolean;
    }

    /**
     * Provides a list of sensor nodes that returned positive results for the query.
     * This is applicable for boolean type queries.
     *
     * @return An ArrayList of String identifiers for sensors that have positive results.
     */
    @Override
    public ArrayList<String> positiveSensorNodes() {
        return this.sensitiveNodes;
    }

    /**
     * Checks if the query result was meant to gather data from sensors.
     *
     * @return true if the result is a gather type; false otherwise.
     */
    @Override
    public boolean isGatherRequest() {
        return this.isGather;
    }

    /**
     * Retrieves the gathered sensor data if the query was a gather type.
     *
     * @return An ArrayList of {@link SensorDataI} containing the gathered sensor data.
     */
    @Override
    public ArrayList<SensorDataI> gatheredSensorsValues() {
        return this.sd;
    }

    /**
     * Sets the result type of this query to gather, ensuring it is not considered a boolean request.
     */
    public void setGather() {
        this.isBoolean = false;
        this.isGather = true;
    }

    /**
     * Sets the result type of this query to boolean, ensuring it is not considered a gather request.
     */
    public void setBoolean() {
        this.isBoolean = true;
        this.isGather = false;
    }
}
